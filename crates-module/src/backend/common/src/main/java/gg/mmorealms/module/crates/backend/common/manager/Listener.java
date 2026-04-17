package gg.mmorealms.module.crates.backend.common.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.PlayerAttackBlockEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.PlayerBlockInteractEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.PermissionException;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import gg.mmorealms.module.crates.backend.common.gui.PreviewCrateGUI;
import net.minecraft.core.BlockPos;

@OnlyOn(servers = ServerType.SPAWN)
public class Listener {

	private @Inject CratesConfig config;

	@EventHandler
	public void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event) {
		User user = User.get(event.getPlayer());
		BlockPos blockPos = event.getPositon();

		event.setResult(crateCheck(user, blockPos));
	}

	@EventHandler
	public void onPlayerAttackBlockEvent(PlayerAttackBlockEvent event) {
		User user = User.get(event.getPlayer());
		BlockPos blockPos = event.getPosition();

		event.setResult(crateCheck(user, blockPos));
	}

	private boolean crateCheck(User user, BlockPos blockPos) {
		Location location = Location.of(blockPos.getX(), blockPos.getY(), blockPos.getZ());

		for (Crate crate : config.crates) {
			if (crate.getPhysicalLocation().equalsCoords(location)) {
				try {
					if (InventoryUtils.hasFullInventory(user)) {
						user.sendMessage(config.fullInventory);
						return false;
					}

					new PreviewCrateGUI(user, crate).open();
				} catch (PermissionException exception) {
					user.sendMessage(exception.getMessage());
				}
				return false;
			}
		}

		return true;
	}
}
