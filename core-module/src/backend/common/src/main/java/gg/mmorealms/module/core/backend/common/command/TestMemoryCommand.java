package gg.mmorealms.module.core.backend.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"test_memory"}, arguments = {"start_x", "start_z", "end_x", "end_z"})
public class TestMemoryCommand extends UserCommand {

	private static final int MAX_CHUNK_LIMIT = 256;
	private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((object, object2) -> Component.translatableEscape("commands.forceload.toobig", new Object[]{object, object2}));
	private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((object, object2) -> Component.translatableEscape("commands.forceload.query.failure", new Object[]{object, object2}));
	private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.added.failure"));
	private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.removed.failure"));


	public TestMemoryCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String startXString = arguments.get(0);
		String startZString = arguments.get(1);
		String endXString = arguments.get(2);
		String endZString = arguments.get(3);

		int startX, startZ, endX, endZ;
		try {
			startX = Integer.parseInt(startXString);
			startZ = Integer.parseInt(startZString);
			endX = Integer.parseInt(endXString);
			endZ = Integer.parseInt(endZString);
		} catch (NumberFormatException e) {
			sendMessage(user, "Invalid number");
			return;
		}


		try {
			changeForceLoad(user, new ColumnPos(startX, startZ), new ColumnPos(endX, endZ), true);
		} catch (CommandSyntaxException e) {
			Logger.error(e);
			user.sendMessage("error");
		}
	}

	private static int changeForceLoad(User user, ColumnPos from, ColumnPos to, boolean add) throws CommandSyntaxException {
		ServerLevel serverLevel = LocationUtils.getWorld();

		int i = Math.min(from.x(), to.x());
		int j = Math.min(from.z(), to.z());
		int k = Math.max(from.x(), to.x());
		int l = Math.max(from.z(), to.z());
		if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
			int m = SectionPos.blockToSectionCoord(i);
			int n = SectionPos.blockToSectionCoord(j);
			int o = SectionPos.blockToSectionCoord(k);
			int p = SectionPos.blockToSectionCoord(l);
			long q = ((long) (o - m) + 1L) * ((long) (p - n) + 1L);

			ResourceKey<Level> resourceKey = serverLevel.dimension();
			ChunkPos chunkPos = null;
			int r = 0;

			for (int s = m; s <= o; ++s) {
				for (int t = n; t <= p; ++t) {
					boolean bl = serverLevel.setChunkForced(s, t, add);
					if (bl) {
						++r;
						if (chunkPos == null) {
							chunkPos = new ChunkPos(s, t);
						}
					}
				}
			}

			if (r == 0) {
				throw (add ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
			} else {
				if (r == 1) {
					user.sendMessage(Component.translatable("commands.forceload." + (add ? "added" : "removed") + ".single", Component.translationArg(chunkPos), Component.translationArg(resourceKey.location())).getString());
				} else {
					ChunkPos chunkPos3 = new ChunkPos(m, n);
					ChunkPos chunkPos4 = new ChunkPos(o, p);
					user.sendMessage(Component.translatable("commands.forceload." + (add ? "added" : "removed") + ".multiple", r, Component.translationArg(resourceKey.location()), Component.translationArg(chunkPos3), Component.translationArg(chunkPos4)).getString());
				}

				return r;
			}
		} else {
			throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
		}
	}
}
