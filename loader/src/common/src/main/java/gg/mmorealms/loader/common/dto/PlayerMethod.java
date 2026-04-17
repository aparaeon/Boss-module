package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgsLambda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class PlayerMethod<Player> {
	private @NotNull String name;
	private @NotNull ArgLambda<Player> loader;
	private @NotNull ArgsLambda<Player, Boolean> saver; // player, evictFromCache
	private @Nullable CancelableTimeTask autoSaveTask;
}
