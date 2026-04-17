package gg.mmorealms.loader.backend.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.CommonLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CodecUtils {

	public static <T> JsonObject serialize(@NotNull Codec<T> codec, @Nullable T object) {
		if (object == null) {
			return new JsonObject();
		}

		DataResult<JsonElement> jsonData = codec.encodeStart(
			BackendLoader.instance().getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE),
			object
		);

		if (jsonData.result().isEmpty()) {
			if (jsonData.error().isPresent() &&
				jsonData.error().get().message().contains("Item must not be minecraft:air")) {
				return new JsonObject();
			}
			Logger.error(jsonData);
			return new JsonObject();
		}

		return jsonData.result().get().getAsJsonObject();
	}

	public static @NotNull <T> List<JsonElement> serialize(@NotNull Codec<T> codec, @Nullable List<T> object) {
		List<JsonElement> elements = new ArrayList<>();

		if (object == null) {
			return elements;
		}

		for (T item : object) {
			elements.add(serialize(codec, item));
		}

		return elements;
	}

	public static <T> T deserialize(@NotNull Codec<T> codec, @Nullable String json, @NotNull ReturnArgLambda<T, CodecError> defaultProvider) {
		return deserialize(codec, json == null ? null : CommonLoader.instance().getGsonSettings().getInternalGsonHolder().value().fromJson(json, JsonElement.class), defaultProvider);
	}

	public static <T> T deserialize(@NotNull Codec<T> codec, @Nullable JsonElement json, @NotNull ReturnArgLambda<T, CodecError> defaultProvider) {
		if (json == null) {
			return defaultProvider.run(CodecError.Type.NULL.of(
				"null",
				"Attempted to deserialize null json, returning default value."
			));
		}

		if (json.toString().equals("{}")) {
			return defaultProvider.run(CodecError.Type.EMPTY.of(
				json.toString(),
				"Attempted to deserialize empty json, returning default value."
			));
		}

		DataResult<Pair<T, JsonElement>> dataResult = codec.decode(
			BackendLoader.instance().getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE),
			json
		);

		if (dataResult.result().isEmpty()) {
			return defaultProvider.run(CodecError.Type.NULL.of(
				json.toString(),
				dataResult.error().toString()
			));
		}

		return dataResult.result().get().getFirst();
	}

	public static @NotNull <T> List<T> deserialize(@NotNull Codec<T> codec, @Nullable List<JsonElement> json, @NotNull ReturnArgLambda<T, CodecError> defaultProvider) {
		List<T> elements = new ArrayList<>();

		if (json == null) {
			return elements;
		}

		for (JsonElement item : json) {
			elements.add(deserialize(codec, item, defaultProvider));
		}

		return elements;
	}

	@AllArgsConstructor
	@Getter
	public static class CodecError {

		private final Type type;
		private final String json;
		private final String message;

		@Override
		public String toString() {
			return new MessageBuilder("""
				Attempted to deserialize invalid json, returning default value.
				Json: {json}
				Error: {error}
				Error Message: {message}
				""")
				.parse("error", type)
				.parse("json", json)
				.parse("message", message)
				.parse();
		}

		public void printSevere() {
			switch (type) {
				case NULL, EMPTY -> {
				}
				case ERROR -> Logger.warn(this);
			}
		}

		public void print() {
			Logger.warn(this);
		}

		public enum Type {
			NULL,
			EMPTY,
			ERROR;

			public CodecError of(String json, String message) {
				return new CodecError(this, json, message);
			}
		}
	}

	public static class CodecErrorProcessor<T> implements ReturnArgLambda<T, CodecError> {

		private final ReturnLambda<T> defaultProvider;

		public CodecErrorProcessor(ReturnLambda<T> defaultProvider) {
			this.defaultProvider = defaultProvider;
		}

		public static <T> CodecErrorProcessor<T> of(ReturnLambda<T> defaultProvider) {
			return new CodecErrorProcessor<>(defaultProvider);
		}

		public static <T> CodecErrorProcessor<T> ofNull() {
			return of(() -> null);
		}

		@Override
		public T run(CodecError error) {
			error.printSevere();
			return defaultProvider.run();
		}
	}


}
