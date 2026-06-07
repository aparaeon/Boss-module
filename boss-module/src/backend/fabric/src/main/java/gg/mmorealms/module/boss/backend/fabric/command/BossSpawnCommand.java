package gg.mmorealms.module.boss.backend.fabric.command;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
import gg.mmorealms.module.boss.backend.fabric.manager.BossSpawner;
import gg.mmorealms.module.boss.common.BossTier;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Command(aliases = {"spawn"}, arguments = {"tier", "?species", "?level", "?shiny", "?x", "?y", "?z"},
		parent = BossAdminCommand.class)
public class BossSpawnCommand extends BackendCommand {

	/** Cached full species name list — Cobblemon registry is immutable at runtime. */
	private static volatile @Nullable List<String> SPECIES_NAME_CACHE;

	public BossSpawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected @NotNull List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		BossFabricModule mod = BossFabricModule.instance();
		BossConfig cfg = mod != null ? mod.getConfig() : null;
		switch (argument) {
			case "tier":
				return Arrays.stream(BossTier.values()).map(Enum::name).collect(Collectors.toList());
			case "species": {
				BossTier tier = parseTierFromContext(context);
				if (tier != null && cfg != null) {
					TierConfig tc = cfg.tiers.get(tier);
					if (tc != null) {
						BossSpawner spawner = mod.getBossSpawner();
						if (spawner != null) {
							List<String> pool = spawner.buildSpeciesPoolForTier(tier, tc);
							if (!pool.isEmpty()) return pool;
						}
					}
				}
				return cachedSpeciesNames();
			}
			case "level": {
				BossTier tier = parseTierFromContext(context);
				if (tier != null && cfg != null) {
					TierConfig tc = cfg.tiers.get(tier);
					if (tc != null && tc.levelRange != null) {
						int min = (int) tc.levelRange.getMin();
						int max = (int) tc.levelRange.getMax();
						int mid = (min + max) / 2;
						return List.of(String.valueOf(min), String.valueOf(mid), String.valueOf(max));
					}
				}
				return List.of("1", "50", "100");
			}
			case "shiny":
				return List.of("true", "false");
			case "x":
			case "y":
			case "z": {
				if (context.getSource().getEntity() instanceof ServerPlayer p) {
					BlockPos pos = p.blockPosition();
					return switch (argument) {
						case "x" -> List.of(String.valueOf(pos.getX()));
						case "y" -> List.of(String.valueOf(pos.getY()));
						case "z" -> List.of(String.valueOf(pos.getZ()));
						default -> List.of();
					};
				}
				return List.of();
			}
			default:
				return List.of();
		}
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		BossFabricModule mod = BossFabricModule.instance();
		if (mod == null || mod.getBossManager() == null) {
			if (mod != null && mod.getConfig() != null) {
				mod.sendLang(sender, mod.getConfig().lang.adminModuleNotInitialized);
			}
			return;
		}
		BossConfig.Lang lang = mod.getConfig().lang;

		// The command framework returns a fixed-size list with NULL for any unprovided
		// optional arg, so `arguments.size()` does NOT reflect what the user typed.
		// Test each slot with arg(i) != null, not size > i.
		String tierArg = arg(arguments, 0);
		if (tierArg == null) {
			mod.sendLang(sender, lang.adminUsageSpawn);
			return;
		}

		BossTier tier;
		try {
			tier = BossTier.valueOf(tierArg.toUpperCase());
		} catch (IllegalArgumentException e) {
			mod.sendLang(sender, lang.adminTierUnknown.parse("tier", tierArg));
			return;
		}

		String species = arg(arguments, 1);

		Integer level = null;
		String levelArg = arg(arguments, 2);
		if (levelArg != null) {
			try {
				level = Integer.parseInt(levelArg);
			} catch (NumberFormatException nfe) {
				mod.sendLang(sender, lang.adminLevelInvalid.parse("value", levelArg));
				return;
			}
		}

		boolean shiny = Boolean.parseBoolean(arg(arguments, 3));

		// Coords: all three of x, y, z must be present together or all omitted. Mixed = reject.
		BlockPos forcedPos = null;
		String xArg = arg(arguments, 4);
		String yArg = arg(arguments, 5);
		String zArg = arg(arguments, 6);
		int provided = (xArg != null ? 1 : 0) + (yArg != null ? 1 : 0) + (zArg != null ? 1 : 0);
		if (provided > 0 && provided < 3) {
			mod.sendLang(sender, lang.adminCoordsIncomplete);
			return;
		}
		if (provided == 3) {
			try {
				forcedPos = new BlockPos(Integer.parseInt(xArg), Integer.parseInt(yArg), Integer.parseInt(zArg));
			} catch (NumberFormatException nfe) {
				mod.sendLang(sender, lang.adminCoordsInvalid.parse("value", nfe.getMessage()));
				return;
			}
		}

		ServerPlayer anchor = resolveAnchor(sender, mod);
		if (anchor == null) {
			mod.sendLang(sender, lang.adminNoAnchor);
			return;
		}

		BossSpawner spawner = mod.getBossSpawner();
		if (spawner == null) {
			mod.sendLang(sender, lang.adminModuleNotInitialized);
			return;
		}

		BossSpawner.AdminSpawnResult result = spawner.adminSpawn(anchor, tier, species, shiny, level, forcedPos);
		switch (result.status()) {
			case SUCCESS:
				String shortId = result.pokemonUUID() != null
						? BossManager.shortId(result.pokemonUUID()) : "?";
				String shinySuffix = shiny ? lang.adminSpawnShinySuffix.parse() : "";
				mod.sendLang(sender, lang.adminSpawnSuccess
						.parse("tier", tier.name())
						.parse("species", result.species() != null ? result.species() : "?")
						.parse("level", result.level())
						.parse("shiny_suffix", shinySuffix)
						.parse("short_id", shortId)
						.parse("x", result.x())
						.parse("y", result.y())
						.parse("z", result.z()));
				break;
			case SPECIES_NOT_FOUND:
				mod.sendLang(sender, lang.adminSpeciesUnknown.parse("species", species != null ? species : ""));
				break;
			case SPECIES_NOT_IN_POOL:
				mod.sendLang(sender, lang.adminSpeciesNotInPool
						.parse("species", species != null ? species : "")
						.parse("tier", tier.name()));
				break;
			case LEVEL_OUT_OF_RANGE: {
				TierConfig tc = mod.getConfig().tiers.get(tier);
				mod.sendLang(sender, lang.adminLevelOutOfRange
						.parse("level", level != null ? level : "")
						.parse("tier", tier.name())
						.parse("min", tc != null && tc.levelRange != null ? (int) tc.levelRange.getMin() : "?")
						.parse("max", tc != null && tc.levelRange != null ? (int) tc.levelRange.getMax() : "?"));
				break;
			}
			case POSITION_NOT_FOUND:
				mod.sendLang(sender, lang.adminPositionNotFound);
				break;
			case ANCHOR_NOT_FOUND:
				mod.sendLang(sender, lang.adminNoAnchor);
				break;
			case SPAWN_FAILED:
			default:
				mod.sendLang(sender, lang.adminSpawnFailed);
				break;
		}
	}

	private @Nullable ServerPlayer resolveAnchor(@NotNull CommandSource sender, @NotNull BossFabricModule mod) {
		if (sender instanceof ServerPlayer p) return p;
		var server = mod.getServer();
		if (server == null) return null;
		var players = server.getPlayerList().getPlayers();
		return players.isEmpty() ? null : players.get(0);
	}

	private @Nullable BossTier parseTierFromContext(@NotNull CommandContext<CommandSourceStack> context) {
		String input = context.getInput();
		// Tokenize after "spawn" — defensive across "/boss admin spawn TIER ..." or aliases.
		String[] tokens = input.split("\\s+");
		for (int i = 0; i < tokens.length - 1; i++) {
			if (tokens[i].equalsIgnoreCase("spawn")) {
				try { return BossTier.valueOf(tokens[i + 1].toUpperCase()); } catch (IllegalArgumentException ignored) {}
				return null;
			}
		}
		return null;
	}

	/**
	 * Safe accessor — returns null for any out-of-range slot. The command framework
	 * returns a fixed-size list with NULLs for unprovided optional args, so we treat
	 * "null in slot" and "slot doesn't exist" as the same thing.
	 */
	private static @Nullable String arg(@NotNull List<String> arguments, int i) {
		if (i < 0 || i >= arguments.size()) return null;
		return arguments.get(i);
	}

	private static List<String> cachedSpeciesNames() {
		List<String> cached = SPECIES_NAME_CACHE;
		if (cached != null) return cached;
		Collection<Species> all = PokemonSpecies.INSTANCE.getSpecies();
		List<String> names = new ArrayList<>(all.size());
		for (Species s : all) {
			names.add(s.getName().toLowerCase());
		}
		SPECIES_NAME_CACHE = names;
		return names;
	}
}
