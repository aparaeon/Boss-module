package com.raduvoinea.utils.minecraft;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class MinecraftPing {

	private static final byte PACKET_HANDSHAKE = 0x00;
	private static final byte PACKET_STATUSREQUEST = 0x00;
	private static final byte PACKET_PING = 0x01;
	private static final int PROTOCOL_VERSION = 4;
	private static final int STATUS_HANDSHAKE = 1;

	private final Options options;

	public MinecraftPing(String hostname) {
		this(new Options().hostname(hostname));
	}

	public MinecraftPing(Options options) {
		this.options = options;
	}

	public Reply send() throws IOException {
		if (options.hostname == null) {
			throw new RuntimeException("Hostname cannot be null");
		}

		try (
			Socket socket = new Socket();
			ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
			DataOutputStream handshake = new DataOutputStream(handshake_bytes)
		) {
			socket.connect(new InetSocketAddress(options.hostname, options.port), options.timeout);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			//> Handshake

			handshake.writeByte(PACKET_HANDSHAKE);
			writeVarInt(handshake, PROTOCOL_VERSION);
			writeVarInt(handshake, options.hostname.length());
			handshake.writeBytes(options.hostname);
			handshake.writeShort(options.port);
			writeVarInt(handshake, STATUS_HANDSHAKE);

			writeVarInt(out, handshake_bytes.size());
			out.write(handshake_bytes.toByteArray());

			//> Status request

			out.writeByte(0x01); // Size of packet
			out.writeByte(PACKET_STATUSREQUEST);

			//< Status response

			readVarInt(in); // Size
			int id = readVarInt(in);

			if (id == -1) {
				throw new IOException("Server prematurely ended stream.");
			}
			if (id != PACKET_STATUSREQUEST) {
				throw new IOException("Server returned invalid packet.");
			}

			int length = readVarInt(in);
			if (length < -1) {
				throw new IOException("Server returned invalid length: " + length);
			}
			if (length == 0) {
				throw new IOException("Server returned unexpected value.");
			}

			byte[] data = new byte[length];
			in.readFully(data);
			String json = new String(data, options.charset());

			//> Ping

			out.writeByte(0x09); // Size of packet
			out.writeByte(PACKET_PING);
			out.writeLong(System.currentTimeMillis());

			//< Ping

			readVarInt(in); // Size
			id = readVarInt(in);
			if (id == -1) {
				throw new IOException("Server prematurely ended stream.");
			}
			if (id != PACKET_PING) {
				throw new IOException("Server returned invalid packet.");
			}

			return new Gson().fromJson(json, Reply.class);
		}
	}

	@Getter
	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Options {

		private String hostname;
		private int port = 25565;
		private int timeout = 2000;
		private String charset = "UTF-8";

		public Options hostname(String hostname) {
			if (hostname.contains(":")) {
				String[] parts = hostname.split(":");
				hostname(parts[0]);
				port(Integer.parseInt(parts[1]));
				return this;
			}

			this.hostname = hostname;
			return this;
		}

	}

	@Getter
	public static class Reply {

		private Description description;
		private Players players;
		private Version version;
		private String favicon;

		@Getter
		public static class Description {
			private String text;
		}

		@Getter
		public static class Players {
			private int max;
			private int online;
			private List<Player> sample;
		}

		@Getter
		public static class Player {
			private String name;
			private String id;
		}

		@Getter
		public static class Version {
			private String name;
			private int protocol;
		}

	}

	private int readVarInt(DataInputStream inputStream) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = inputStream.readByte();

			i |= (k & 0x7F) << j++ * 7;

			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}

			if ((k & 0x80) != 128) {
				break;
			}
		}

		return i;
	}

	public static void writeVarInt(DataOutputStream outputStream, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				outputStream.writeByte(paramInt);
				return;
			}

			outputStream.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}

}