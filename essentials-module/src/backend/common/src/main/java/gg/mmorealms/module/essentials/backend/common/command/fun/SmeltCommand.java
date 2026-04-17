package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.ICooldownCommand;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"smelt"}, onlyFor = Command.OnlyFor.PLAYERS)
public class SmeltCommand extends UserCommand implements ICooldownCommand {

	@Getter
	private final String COMMAND_COOLDOWN_KEY = "SMELT_COOLDOWN";

	private @Inject EssentialsConfig essentialsConfig;
	private @Inject MinecraftServer server;

	public SmeltCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (isCommandOnCooldown(user)) {
			return;
		}

		Level world = user.getPlayer().level();
		List<ItemStack> items = user.getPlayer().getInventory().items;

		for (int index = 0; index < items.size(); index++) {
			ItemStack itemStack = items.get(index);

			ItemStack result = getSmeltedItem(itemStack, world);

			if (result.isEmpty()) {
				continue;
			}

			result.setCount(itemStack.getCount());

			items.set(index, result);
		}

		setCommandCooldown(user, essentialsConfig.smeltCooldown);
		user.sendMessage("Your inventory has been smelted!"); // TODO Config
	}

	private ItemStack getSmeltedItem(ItemStack itemStack, Level world) {
		List<RecipeHolder<SmeltingRecipe>> recipes = server.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING);

		for (RecipeHolder<SmeltingRecipe> recipeHolder : recipes) {
			SmeltingRecipe recipe = recipeHolder.value();
			if (recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(itemStack))) {
				return recipe.assemble(new SingleRecipeInput(itemStack), world.registryAccess());
			}
		}

		return ItemStack.EMPTY;
	}

}
