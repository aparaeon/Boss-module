package gg.mmorealms.loader.backend.common.manager;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.utils.value.IntValue;
import gg.mmorealms.loader.backend.common.dto.event.fabric.ExplodeEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.EntityDamageEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.*;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class BackendEvents {

	public void registerEvents() {

		//noinspection CodeBlock2Expr
		PlayerEvent.PLAYER_JOIN.register((ServerPlayer player) -> {
			new PlayerJoinEvent(player).fireSync();
		});

		PlayerEvent.PLAYER_QUIT.register((ServerPlayer player) ->
			new PlayerLeaveEvent(player).fireAsync()
		);

		ChatEvent.RECEIVED.register((ServerPlayer player, Component component) ->
			EventResult.interruptFalse() // TODO Instead of this we should use SignedVelocity for NeoForge (does not exist as of the time of writing) And Fabric
		);

		BlockEvent.BREAK.register((Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) ->
			new PlayerBlockBreakEvent(level, player, pos, state).fireSync()
		);

		PlayerEvent.PICKUP_ITEM_POST.register(((player, itemEntity, itemStack) -> {
			PlayerPickupItemEvent event = new PlayerPickupItemEvent(player, itemEntity, itemStack);
			event.fireAsync();
		}));

		EntityEvent.LIVING_HURT.register((LivingEntity entity, DamageSource source, float amount) ->
			new EntityDamageEvent(entity, source, amount).fireSync()
		);

		EntityEvent.LIVING_DEATH.register((LivingEntity entity, DamageSource source) -> {
			if (!(entity instanceof ServerPlayer player)) {
				return EventResult.pass();
			}

			PlayerPreDeathEvent event = new PlayerPreDeathEvent(player, source);
			return event.fireSync();
		});

		InteractionEvent.INTERACT_ENTITY.register((Player player, Entity entity, InteractionHand hand) ->
			new PlayerEntityInteractEvent((ServerPlayer) player, hand, entity).fireSync()
		);

		InteractionEvent.LEFT_CLICK_BLOCK.register((Player player, InteractionHand hand, BlockPos pos, Direction face) ->
			new PlayerAttackBlockEvent((ServerPlayer) player, hand, pos, face).fireSync()
		);

		InteractionEvent.RIGHT_CLICK_BLOCK.register((Player __player, InteractionHand hand, BlockPos pos, Direction face) -> {
			ServerPlayer player = (ServerPlayer) __player;
			ServerLevel world = (ServerLevel) player.level();
			BlockState blockState = world.getBlockState(pos);
			ItemStack useItem = switch (hand) {
				case MAIN_HAND -> player.getMainHandItem();
				case OFF_HAND -> player.getOffhandItem();
			};

			if (useItem.getItem() instanceof DispensibleContainerItem) {
				PlayerDispensableBlockPlaceEvent event = new PlayerDispensableBlockPlaceEvent(player, hand, pos, face, world, blockState, useItem);
				return event.fireSync();
			}

			if (useItem.getItem() instanceof BoatItem) {
				PlayerBoatPlaceEvent event = new PlayerBoatPlaceEvent(player, hand, pos, face, world, blockState, useItem);
				return event.fireSync();
			}

			if (blockState.getBlock() instanceof DecoratedPotBlock) {
				PlayerPotBlockInteractEvent event = new PlayerPotBlockInteractEvent(player, hand, pos, face, world, blockState, useItem);
				return event.fireSync();
			}

			if (blockState.getBlock() instanceof Container || blockState.getBlock() instanceof EntityBlock) {
				PlayerBlockInteractEvent event = new PlayerBlockInteractEvent(player, hand, pos, face, world, blockState, useItem);
				return event.fireSync();
			}

			PlayerBlockPlaceEvent event = new PlayerBlockPlaceEvent(player, hand, pos, face, world, blockState, useItem);
			return event.fireSync();
		});

		InteractionEvent.RIGHT_CLICK_ITEM.register((Player player, InteractionHand hand) -> {
			PlayerUseItemEvent event = new PlayerUseItemEvent((ServerPlayer) player, hand);
			return event.fireSync();
		});

		PlayerEvent.ATTACK_ENTITY.register((Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) -> {
			PlayerAttackEntityEvent event = new PlayerAttackEntityEvent((ServerPlayer) player, level, hand, target, result);
			return event.fireSync();
		});

		PlayerEvent.PLAYER_RESPAWN.register((ServerPlayer newPlayer, boolean conqueredEnd, Entity.RemovalReason removalReason) ->
			new PlayerRespawnEvent(newPlayer, conqueredEnd, removalReason).fireSync()
		);

		TickEvent.SERVER_PRE.register((server) ->
			new ServerTickEvent(server).fireSync(true)
		);

		ExplosionEvent.PRE.register((Level level, Explosion explosion) ->
			new ExplodeEvent((ServerLevel) level, explosion).fireSync()
		);

		CommandPerformEvent.EVENT.register((CommandPerformEvent event) -> {
			CommandSourceStack source = event.getResults().getContext().getSource();

			if (!(source.getEntity() instanceof ServerPlayer player)) {
				return EventResult.pass();
			}

			return new PlayerCommandEvent(player, event.getResults()).fireSync();
		});
	}
}
