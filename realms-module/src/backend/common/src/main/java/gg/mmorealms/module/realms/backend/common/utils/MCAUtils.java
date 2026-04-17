package gg.mmorealms.module.realms.backend.common.utils;

import com.raduvoinea.utils.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MCAUtils {

	private final static int HEADER_SIZE = 4096 * 2; // 4096 bytes (locations) + 4096 bytes (timestamps0

	public static void executeOnRandomAccessFile(RandomAccessFile file, RandomAccessFileArgExecutor executor) {
		try {
			long originalPosition = file.getFilePointer();

			executor.execute(file);

			file.seek(originalPosition);
		} catch (IOException exception) {
			Logger.error(exception);
		}
	}

	public static @Nullable <T> T executeOnRandomAccessFile(RandomAccessFile file, boolean restoreLocation, RandomAccessFileReturnArgExecutor<T> executor) {
		try {
			long originalPosition = file.getFilePointer();

			T result = executor.execute(file);

			if (restoreLocation) {
				file.seek(originalPosition);
			}

			return result;
		} catch (IOException exception) {
			Logger.error(exception);
		}

		return null;
	}

	public static @Nullable <T> T executeOnRandomAccessFile(RandomAccessFile file, RandomAccessFileReturnArgExecutor<T> executor) {
		return executeOnRandomAccessFile(file, true, executor);
	}


	public static byte[] readHeader(RandomAccessFile __file) {
		return executeOnRandomAccessFile(__file, file -> {
			file.seek(0);

			byte[] header = new byte[HEADER_SIZE];
			file.readFully(header);

			return header;
		});
	}

	public static int getChunkOffset(byte[] header, int chunkX, int chunkZ) {
		int chunkStartIndex = 4 * (chunkX + chunkZ * 32);

		return ((header[chunkStartIndex] & 0xFF) << 16) |
				((header[chunkStartIndex + 1] & 0xFF) << 8) |
				(header[chunkStartIndex + 2] & 0xFF);
	}

	public static int getChunkSectorCount(byte[] header, int chunkX, int chunkZ) {
		int chunkStartIndex = 4 * (chunkX + chunkZ * 32);

		return header[chunkStartIndex + 3] & 0xFF;
	}

	public static int getChunkTimestamp(byte[] header, int chunkX, int chunkZ) {
		int chunkStartIndex = 4096 + 4 * (chunkX + chunkZ * 32);

		return ((header[chunkStartIndex] & 0xFF) << 24) |
				((header[chunkStartIndex + 1] & 0xFF) << 16) |
				((header[chunkStartIndex + 2] & 0xFF) << 8) |
				(header[chunkStartIndex + 3] & 0xFF);
	}


	public static ChunkData getChunkData(RandomAccessFile __file, int chunkX, int chunkZ) {
		return executeOnRandomAccessFile(__file, file -> {
			byte[] header = readHeader(file);

			int offset = getChunkOffset(header, chunkX, chunkZ);
			int sectorCount = getChunkSectorCount(header, chunkX, chunkZ);

			if (offset == 0 || sectorCount == 0) {
				Logger.error("Chunk is not present.");
				return null;
			}

			file.seek((long) offset * 4096);

			int chunkLength = file.readInt();
			int compressionType = file.readUnsignedByte();

			byte[] chunkData = new byte[chunkLength - 1];
			file.readFully(chunkData);

			return new ChunkData(offset, chunkLength, sectorCount, compressionType, chunkData);
		});
	}

	@AllArgsConstructor
	@Getter
	public static class ChunkData {
		private final int offset;
		private final int sectorCount;
		private final int length;
		private final int compressionType;
		private final byte[] data;
	}

	public interface RandomAccessFileArgExecutor {
		void execute(RandomAccessFile file) throws IOException;
	}

	public interface RandomAccessFileReturnArgExecutor<Return> {
		Return execute(RandomAccessFile file) throws IOException;
	}

}
