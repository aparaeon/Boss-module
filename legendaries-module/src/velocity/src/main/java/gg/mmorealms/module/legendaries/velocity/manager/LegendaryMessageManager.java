package gg.mmorealms.module.legendaries.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.enums.LegendaryEventType;
import gg.mmorealms.module.legendaries.velocity.LegendariesVelocityModule;
import gg.mmorealms.module.legendaries.velocity.config.LegendarySpawnConfig;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LegendaryMessageManager {
	private final LegendarySpawnConfig config;
	private final ProxyServer proxyServer;
	private final VelocityMiniMessageManager miniMessageManager;

	private final Map<String, String> dimensionNames = Map.of(
			"minecraft:overworld", "Overworld",
			"minecraft:the_nether", "The Nether",
			"minecraft:the_end", "The End"
	);

	public LegendaryMessageManager(ProxyServer proxyServer, VelocityMiniMessageManager miniMessageManager) {
		this.config = LegendariesVelocityModule.instance().getConfig();
		this.proxyServer = proxyServer;
		this.miniMessageManager = miniMessageManager;
	}

	public void sendMessage(LegendaryInfo info, LegendaryEventType type) {
		String message = getMessage(info, type);
		Component component = miniMessageManager.toNative(miniMessageManager.toComponent(message));

		proxyServer.sendMessage(component);
	}

	public String getMessage(LegendaryInfo info, LegendaryEventType type) {
		return switch (type) {
			case SPAWNED -> formatFullMessage(config.lang.legendarySpawned, info);
			case DESPAWNED -> formatSimpleMessage(config.lang.legendaryDespawned, info.getPokemonName());
			case CAPTURED -> formatFullMessage(config.lang.legendaryCaptured, info);
			case FAINTED -> formatSimpleMessage(config.lang.legendaryKilled, info.getPokemonName());
			default -> "";
		};
	}

	private String formatFullMessage(GenericMessageBuilder<?> template, LegendaryInfo info) {
		String name = info.getPokemonName();
		String player = info.getInteractedPlayerName();
		String dimension = info.getLevelName();
		String biome = info.getBiomeName();
		Location location = info.getLocation();

		boolean hasPlayer = player != null && config.printPlayerName;
		boolean hasDimension = dimension != null && config.printSpawnDimension;
		boolean hasBiome = biome != null && config.printSpawnBiome;
		boolean hasLocation = location != null && config.printSpawnLocation;
		boolean obfuscate = hasLocation && config.obfuscateSpawnLocation;

		Location actualLoc = obfuscate
				? obfuscateLocation(location, config.obfuscationRange)
				: location;

		return template
				.parse("name", parseName(name))
				.parse("playerAnnotation", hasPlayer ? parsePlayer(player) : "")
				.parse("dimensionAnnotation", hasDimension ? parseDimension(dimension) : "")
				.parse("biomeAnnotation", hasBiome ? parseBiome(biome) : "")
				.parse("locationAnnotation", hasLocation ? parseLocation(actualLoc) : "")
				.parse("offsetAnnotation", obfuscate ? parseOffset() : "")
				.toString()
				.trim();
	}

	private String formatSimpleMessage(GenericMessageBuilder<?> template, String name) {
		return template
				.parse("name", parseName(name))
				.toString()
				.trim();
	}

	private Location obfuscateLocation(Location location, Range range) {
		int offset = RandomUtils.getRandom(range);
		double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;

		double offsetX = Math.round(Math.cos(angle) * offset);
		double offsetZ = Math.round(Math.sin(angle) * offset);

		return location.offset(offsetX, 0, offsetZ);
	}

	private String parseName(String name) {
		return config.printPokemonName && name != null ? name : config.lang.defaultName;
	}

	private String parsePlayer(String player) {
		return parseAnnotation(config.lang.playerAnnotation, "player", player);
	}

	private String parseDimension(String dimension) {
		return parseAnnotation(config.lang.dimensionAnnotation, "dimension", dimensionNames.get(dimension));
	}

	private String parseBiome(String biome) {
		return parseAnnotation(config.lang.biomeAnnotation, "biome", biome);
	}

	private String parseOffset() {
		return parseAnnotation(config.lang.offsetAnnotation, "offset", config.obfuscationRange.getMax());
	}

	private String parseLocation(Location location) {
		return config.lang.locationAnnotation
				.parse("x", location.getX())
				.parse("y", location.getY())
				.parse("z", location.getZ())
				.toString()
				.trim();
	}

	private String parseAnnotation(GenericMessageBuilder<?> template, String placeholder, Object value) {
		return template
				.parse(placeholder, value)
				.toString()
				.trim();
	}
}
