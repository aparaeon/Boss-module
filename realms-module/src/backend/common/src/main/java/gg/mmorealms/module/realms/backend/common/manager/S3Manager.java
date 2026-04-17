package gg.mmorealms.module.realms.backend.common.manager;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.realms.backend.common.config.S3Config;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.retry.AwsRetryStrategy;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class S3Manager {

	private static final int MAX_VERSIONS = 7;
	private static final long MIN_VERSION_SPACING = Duration.ofDays(1).toMillis();

	private List<S3Client> clients;
	private List<S3Config> configs;

	public S3Manager(List<S3Config> configs) {
		if (CommonLoader.DUMMY_MODE) {
			return;
		}

		this.configs = configs;
		this.clients = this.configs.stream().map(this::createClient).toList();
	}

	private S3Client createClient(S3Config config) {
		StandardRetryStrategy retryStrategy = AwsRetryStrategy.standardRetryStrategy()
				.toBuilder()
				.maxAttempts(5)
				.build();

		return S3Client.builder()
				.forcePathStyle(config.forcePathStyleAccess)
				.region(Region.of(config.region))
				.endpointOverride(URI.create(config.endpointURL))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.accessKey, config.secretKey)))
				.overrideConfiguration(configurationBuilder -> configurationBuilder.retryStrategy(retryStrategy))
				.build();
	}

	public boolean download(String key, Path destination) {
		if (CommonLoader.DUMMY_MODE) {
			Logger.warn("Attempted to download from S3 in dummy mode, skipping download.");
			return false;
		}

		for (int i = 0; i < clients.size(); i++) {
			S3Client client = clients.get(i);
			S3Config config = configs.get(i);
			boolean result = download(client, config, key, destination);

			if (result) {
				return true;
			}
		}

		return false;
	}

	public boolean download(S3Client client, S3Config config, String key, Path destination) {
		if (CommonLoader.DUMMY_MODE) {
			Logger.warn("Attempted to download from S3 in dummy mode, skipping download.");
			return false;
		}

		try {
			Files.deleteIfExists(destination);
		} catch (IOException exception) {
			Logger.error(exception);
			return false;
		}

		//noinspection ResultOfMethodCallIgnored
		destination.getParent().toFile().mkdirs();

		try {
			Logger.debug(new MessageBuilder("[{bucket}] Downloading {from} to {to}")
					.parse("bucket", config.bucket)
					.parse("from", key)
					.parse("to", destination.toString())
			);

			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
					.bucket(config.bucket)
					.key(key)
					.build();

			client.getObject(getObjectRequest, destination);
			return true;
		} catch (NoSuchKeyException exception) {
			Logger.warn(new MessageBuilder("[{bucket}]Failed to download {key} from S3. Key {key} does not exist")
					.parse("key", key)
					.parse("bucket", config.bucket)
			);
			return false;
		} catch (Throwable error) {
			Logger.error(error);
			return false;
		}
	}

	public void upload(String key, Path source) {
		if (CommonLoader.DUMMY_MODE) {
			Logger.warn("Attempted to upload to S3 in dummy mode, skipping upload.");
			return;
		}

		upload(clients.getFirst(), configs.getFirst(), key, source); // Always use the first S3 config for uploads
	}

	public void upload(S3Client client, S3Config config, String key, Path source) {
		if (CommonLoader.DUMMY_MODE) {
			Logger.warn("Attempted to upload to S3 in dummy mode, skipping upload.");
			return;
		}

		try {
			uploadItem(client, config, key, source);
			uploadBackup(client, config, key, source);
		} catch (UncheckedIOException exception) {
			Logger.error(exception);
			Logger.error(new MessageBuilder("Failed to upload {key} to S3.")
					.parse("key", key)
					.parse()
			);
		} catch (Throwable error) {
			Logger.error(error);
		}
	}

	@SneakyThrows(value = {IOException.class})
	private void uploadItem(S3Client client, S3Config config, String key, Path source) {
		Logger.debug(new MessageBuilder("Uploading {key} from {source}")
				.parse("key", key)
				.parse("source", source.toString())
		);

		byte[] fileBytes = Files.readAllBytes(source);
//		MessageDigest md = MessageDigest.getInstance("MD5");
//		byte[] md5Bytes = md.digest(fileBytes);
//		String md5Base64 = Base64.getEncoder().encodeToString(md5Bytes);

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(config.bucket)
				.key(key)
				.storageClass(StorageClass.STANDARD)
//				.contentMD5(md5Base64)
				.build();

		client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
	}

	private void uploadBackup(S3Client client, S3Config config, String key, Path source) {
		if (!config.isBackupEnabled()) {
			return;
		}

		String folder = config.getBackupPath(key) + "/";

		List<S3Object> objects = new ArrayList<>();
		String continuationToken = null;
		do {
			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
					.bucket(config.bucket)
					.prefix(folder)
					.continuationToken(continuationToken)
					.build();

			ListObjectsV2Response listObjectsResponse = client.listObjectsV2(listObjectsRequest);
			objects.addAll(listObjectsResponse.contents());
			continuationToken = listObjectsResponse.nextContinuationToken();
		} while (continuationToken != null);

		if (objects.isEmpty()) {
			uploadItem(client, config, folder + DateUtils.getDate("YY_MM_DD_hh_mm_ss"), source);
			return;
		}

		List<S3Object> sortedObjects = objects.stream()
				.sorted(Comparator.comparing(S3Object::lastModified).reversed())
				.toList();

		if (Duration.between(sortedObjects.getFirst().lastModified(), Instant.now()).toMillis() <= MIN_VERSION_SPACING) {
			return;
		}

		uploadItem(client, config, folder + DateUtils.getDate("YY_MM_DD_hh_mm_ss"), source);

		if (sortedObjects.size() + 1 <= MAX_VERSIONS) {
			return;
		}

		List<S3Object> toDelete = sortedObjects.subList(MAX_VERSIONS, sortedObjects.size());

		Logger.debug(new MessageBuilder("Pruning {count} old versions in {folder}:")
				.parse("count", toDelete.size())
				.parse("folder", folder)
		);

		for (S3Object object : toDelete) {
			Logger.debug("- " + object.key());
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
					.bucket(config.bucket)
					.key(object.key())
					.build();
			client.deleteObject(deleteObjectRequest);
		}
	}

}