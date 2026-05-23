package gg.mmorealms.module.essentials.backend.fabric.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// Cobblemon 1.7.0 changed heal_powder from a smelting recipe to a crafting recipe.
// Furnaces that previously smelted it still have the old recipe ID in their RecipesUsed NBT,
// causing a ClassCastException when breaking them (vanilla tries to cast a crafting recipe to AbstractCookingRecipe).
// This mixin strips any non-cooking recipe IDs before the XP drop logic runs.
// Safe to remove once all pre-1.7.0 furnace NBT has been cleared.
@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

	@Final
	@Shadow
	private Object2IntOpenHashMap<ResourceLocation> recipesUsed;

	@Inject(method = "getRecipesToAwardAndPopExperience", at = @At("HEAD"))
	private void stripNonCookingRecipes(ServerLevel level, Vec3 popVec, CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
		recipesUsed.keySet().removeIf(id ->
				level.getRecipeManager()
						.byKey(id)
						.map(entry -> !(entry.value() instanceof AbstractCookingRecipe))
						.orElse(true)
		);
	}
}