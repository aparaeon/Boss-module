package gg.mmorealms.module.realms.backend.common.config;

public class S3Config {

	public String accessKey;
	public String secretKey;
	public String region;
	public String bucket;
	public String endpointURL;
	public String backupPathPrefix;
	public boolean forcePathStyleAccess;

	public boolean isBackupEnabled() {
		return backupPathPrefix != null && !backupPathPrefix.isEmpty() && !backupPathPrefix.equals("DISABLED");
	}

	public String getBackupPath(String key) {
		if (backupPathPrefix.endsWith("/")) {
			return backupPathPrefix + key;
		}
		return backupPathPrefix + "/" + key;
	}

}
