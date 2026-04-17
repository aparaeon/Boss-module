package gg.mmorealms.loader.common.dto.remote;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.naming.CommunicationException;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public abstract class RemoteObject<Identifier, ObjectInterface> {

	private final @NotNull Class<Identifier> identifierClass;
	private final @NotNull Class<ObjectInterface> interfaceClass;

	private final @NotNull
	@Getter Identifier identifier;
	private final @NotNull String server;

	public RemoteObject(@NotNull Class<Identifier> identifierClass, @NotNull Class<ObjectInterface> interfaceClass, @NotNull Identifier identifier, @NotNull String server) {
		this.identifierClass = identifierClass;
		this.interfaceClass = interfaceClass;

		this.identifier = identifier;
		this.server = server;
	}

	public @NotNull String getServer() {
		return server;
	}

	protected <T> T sendRequest(Object... args) {
		StackWalker.StackFrame stackFrame = StackWalker
				.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(frames ->
						frames.skip(1)
								.findFirst()
								.orElse(null)
				);

		if (stackFrame == null) {
			RuntimeException exception = new RuntimeException("Can not call RemoteObject#sendRequest. StackFrame is empty.");
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		String className = interfaceClass.getName();
		String methodName = stackFrame.getMethodName();
		List<Class<?>> parameterTypes = stackFrame.getMethodType().parameterList();

		if (parameterTypes.size() != args.length) {
			RuntimeException exception = new RuntimeException("Can not call RemoteObject#sendRequest for method `" + methodName + "`. Parameter count does not match. Expected: " + parameterTypes.size() + " Got: " + args.length);
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		CompletableFuture<T> future = new RemoteMethodExecuteRequest<T>(this.server, className, methodName, parameterTypes, this.identifier, args).send();
		T response;
		try {
			response = future.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException exception) {
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		if (future.state() != Future.State.SUCCESS) {
			CommunicationException exception = new CommunicationException(this.server);
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		return response;
	}
}