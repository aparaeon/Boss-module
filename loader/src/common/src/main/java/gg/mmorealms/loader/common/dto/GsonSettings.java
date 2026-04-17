package gg.mmorealms.loader.common.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.utils.file_manager.dto.gson.interfaces.InterfaceTypeFactory;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GsonSettings {

	private final GsonBuilder internalGsonBuilder = new GsonBuilder()
		.disableHtmlEscaping();
	private final GsonBuilder userFacingGsonBuilder = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting();
	private final GsonBuilder noInterfaceGsonBuilder = new GsonBuilder()
		.disableHtmlEscaping();

	private final InterfaceTypeFactory interfaceTypeFactory;

	private final Holder<Gson> internalGsonHolder = Holder.empty();
	private final Holder<Gson> userFacingGsonHolder = Holder.empty();
	private final Holder<Gson> noInterfaceGsonHolder = Holder.empty();

	public GsonSettings(ClassLoader classLoader) {
		this.interfaceTypeFactory = new InterfaceTypeFactory(classLoader, this::legacyClassMapper);

		this.internalGsonBuilder.registerTypeAdapterFactory(interfaceTypeFactory);
		this.userFacingGsonBuilder.registerTypeAdapterFactory(interfaceTypeFactory);
	}

	public void updateGson(ArgLambda<GsonBuilder> executor) {
		executor.run(this.internalGsonBuilder);
		executor.run(this.userFacingGsonBuilder);
		executor.run(this.noInterfaceGsonBuilder);

		this.internalGsonHolder.set(this.internalGsonBuilder.create());
		this.userFacingGsonHolder.set(this.userFacingGsonBuilder.create());
		this.noInterfaceGsonHolder.set(this.noInterfaceGsonBuilder.create());
	}

	protected String legacyClassMapper(String className) {
		String[] split = className.split("\\.");

		if (split.length >= 5) {
			String tld = split[0];
			String domain = split[1];
			String platform = split[2];
			String type = split[3];
			String id = split[4];
			String rest = String.join(".", new ArrayList<>(List.of(split)).subList(5, split.length));

			if (tld.equals("gg") && domain.equals("mmorealms") && (type.equals("module") || type.equals("loader"))) {
				String output = String.join(".", List.of(
					tld, domain, type, id, platform, rest
				));
				Logger.debug(new MessageBuilder("Converting class {old} to {new}")
					.parse("old", className)
					.parse("new", output)
					.parse());
				return output;
			}
		}

		return className;
	}


}
