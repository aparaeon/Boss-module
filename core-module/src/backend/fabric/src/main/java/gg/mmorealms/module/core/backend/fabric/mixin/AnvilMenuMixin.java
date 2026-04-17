package gg.mmorealms.module.core.backend.fabric.mixin;

import gg.mmorealms.module.core.backend.fabric.registry.CoreDataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

	@Shadow
	@Final
	public static int INPUT_SLOT;

	@Unique
	private final AnvilMenu self = (AnvilMenu) (Object) this;

	@Unique
	private final ItemCombinerMenuAccessor itemCombinerMenu = (ItemCombinerMenuAccessor) this;

	@Inject(method = "createResult", at = @At("RETURN"))
	private void removeInvalidResults(CallbackInfo ci) {
		if (isNotRenameable()) {
			itemCombinerMenu
					.getResultSlots()
					.setItem(0, ItemStack.EMPTY);
		}
	}

	@Unique
	private boolean isNotRenameable() {
		return isNotRenameable(getItemStack());
	}

	@Unique
	private boolean isNotRenameable(ItemStack itemStack) {
		return Boolean.TRUE.equals(itemStack.get(CoreDataComponents.NO_RENAME));
	}

	@Unique
	private ItemStack getItemStack() {
		return self.getSlot(INPUT_SLOT).getItem();
	}

}
