package gg.mmorealms.module.plushies.backend.common.utils;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PlushieUtils {

	public static void spawnPlushie(ServerPlayer player, Vec3 pos, IPokemon pokemon) {
		ServerLevel world = (ServerLevel) player.level();

		Entity entity = PlushiesBackendModule.instance().getPlatformImplementation().createPlushieEntity(world, pokemon);
		PlushieUtils.movePlushie(entity, pos, player);
		if (!world.addFreshEntity(entity)) {
			Logger.error(new MessageBuilder("Failed to spawn plushie {pokemon} for player {player}")
					.parse("pokemon", pokemon.getSpeciesName())
					.parse("player", player.getName().getString())
			);
			player.sendSystemMessage(PlushiesBackendModule.instance().getMiniMessageManager()
					.parse("Failed to spawn your plushie") // TODO Config
			);
		}
	}

	private static void movePlushie(Entity entity, Vec3 spawnLocation, ServerPlayer player) {
		entity.moveTo(spawnLocation.x, spawnLocation.y, spawnLocation.z);
		Pair2<Float, Float> rotation = computeRotation(player.getEyePosition(), entity.position()); // yaw, pitch
		float yRot = rotation.first(); // yaw
		float xRot = rotation.second(); // pitch

		entity.setYRot(yRot);
		entity.setYBodyRot(yRot);
		entity.setYHeadRot(yRot);

		entity.setXRot(xRot);
	}

	private static Pair2<Float, Float> computeRotation(Vec3 playerPos, Vec3 plushiePos) {
		double deltaX = playerPos.x() - plushiePos.x();
		double deltaY = playerPos.y() - plushiePos.y();
		double deltaZ = playerPos.z() - plushiePos.z();

		double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

		float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;

		yaw %= 360.0f;
		if (yaw > 180.0f) {
			yaw -= 360.0f;
		} else if (yaw < -180.0f) {
			yaw += 360.0f;
		}

		float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

		pitch = Math.max(-90.0f, Math.min(90.0f, pitch));

		return new Pair2<>(yaw, pitch);
	}

	public static ItemStack createPlushieItemStack(IPokemon pokemon) {
		return pokemon.toItemBuilder(false)
				.name(
						new MessageBuilder("{shiny}{species} Plushie")
								.parse("species", pokemon.getSpeciesName())
								.parse("shiny", pokemon.isShiny() ? "<gold>Shiny <white>" : "")
				)
				.build();
	}

}
