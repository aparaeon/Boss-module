package gg.mmorealms.module.core.velocity.dto.event;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PlayerChoseInitialServerEventWrapper extends LocalRequest<PlayerChoseInitialServerEventWrapper.Result> {

	private final Player player;

	public PlayerChoseInitialServerEventWrapper(Player player) {
		super(Result.deferred());
		this.player = player;
	}

	public void fail(String message) {
		setResult(Result.fail(message));
	}

	public void success(RegisteredServer initialServer) {
		setResult(Result.success(initialServer));
	}

	public void deferred() {
		setResult(Result.deferred());
	}

	public boolean isFailure() {
		return getResult().isFailure();
	}

	public boolean isSuccess() {
		return getResult().isSuccess();
	}

	@AllArgsConstructor
	@Getter
	public static class Result {
		private RegisteredServer initialServer;
		private String message;
		private Type type;

		public boolean isSuccess() {
			return this.type != Result.Type.FAILURE;
		}

		public boolean isFailure() {
			return this.type == Result.Type.FAILURE;
		}

		public static Result fail(String message) {
			return new Result(null, message, Type.FAILURE);
		}

		public static Result success(RegisteredServer initialServer) {
			return new Result(initialServer, "", Type.SUCCESS);
		}

		public static Result deferred() {
			return new Result(null, "Unable to determine initial server - Generic error", Type.DEFERRED);
		}

		public enum Type {
			SUCCESS,
			FAILURE,
			DEFERRED
		}
	}

}
