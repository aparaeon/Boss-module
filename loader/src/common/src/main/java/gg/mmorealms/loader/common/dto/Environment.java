package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import com.raduvoinea.utils.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Environment {

	private final Type type;
	private final GameMode gameMode;

	public enum Type {
		DEVELOPMENT, TESTING, PRODUCTION
	}

	public enum GameMode {
		COBBLEMON, PIXELMON
	}

	public void switchOn(
		Lambda cobblemonDevelopmentExecutor,
		Lambda cobblemonTextingExecutor,
		Lambda cobblemonProductionExecutor,
		Lambda pixelmonDevelopmentExecutor,
		Lambda pixelmonTextingExecutor,
		Lambda pixelmonProductionExecutor
	) {
		switchOn(
			() -> {
				cobblemonDevelopmentExecutor.run();
				return null;
			},
			() -> {
				cobblemonTextingExecutor.run();
				return null;
			},
			() -> {
				cobblemonProductionExecutor.run();
				return null;
			},
			() -> {
				pixelmonDevelopmentExecutor.run();
				return null;
			},
			() -> {
				pixelmonTextingExecutor.run();
				return null;
			},
			() -> {
				pixelmonProductionExecutor.run();
				return null;
			}
		);
	}

	public <T> T switchOn(
		ReturnLambda<T> cobblemonDevelopmentExecutor,
		ReturnLambda<T> cobblemonTextingExecutor,
		ReturnLambda<T> cobblemonProductionExecutor,
		ReturnLambda<T> pixelmonDevelopmentExecutor,
		ReturnLambda<T> pixelmonTextingExecutor,
		ReturnLambda<T> pixelmonProductionExecutor
	) {
		String methodName = this.type.name() + "_" + this.gameMode.name();
		return switch (methodName) {
			case "DEVELOPMENT_COBBLEMON" -> cobblemonDevelopmentExecutor.run();
			case "TESTING_COBBLEMON" -> cobblemonTextingExecutor.run();
			case "PRODUCTION_COBBLEMON" -> cobblemonProductionExecutor.run();
			case "DEVELOPMENT_PIXELMON" -> pixelmonDevelopmentExecutor.run();
			case "TESTING_PIXELMON" -> pixelmonTextingExecutor.run();
			case "PRODUCTION_PIXELMON" -> pixelmonProductionExecutor.run();
			default -> {
				Logger.error("Attempted to switch on an unknown environment: " + methodName);
				yield null;
			}
		};
	}
}
