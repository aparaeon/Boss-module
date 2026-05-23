package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;
import gg.mmorealms.loader.common.exception.SyncedRequestException;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.CommunicationException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Getter
@Setter
public abstract class SyncedNetworkObject<Identifier, ObjectClass, DestinationServerFinder> {
	private final int methodsToSkip;
	private @NotNull String className;

	protected SyncedNetworkObject(@NotNull Class<ObjectClass> objectClass, int methodsToSkip) {
		this.className = objectClass.getName();
		this.methodsToSkip = methodsToSkip;

		//noinspection unchecked
		RemoteMethodExecuteRequest.registerObjectFetch(objectClass,
			(identifier) -> getByIdentifier((Identifier) identifier));
	}

	// For raw use of this class. Although it is recommended to use a specific subclass of it
	public SyncedNetworkObject(Class<ObjectClass> objectClass) {
		this(objectClass, 1);
	}

	public abstract @Nullable ObjectClass getByIdentifier(@NotNull Identifier identifier);

	public abstract @Nullable Identifier getIdentifier();

	public abstract String getDestinationServer(DestinationServerFinder destinationServerFinder);

	protected <T> T sendRequest(String destinationServer, Object... args) throws SyncedRequestException {
		StackWalker.StackFrame stackFrame = StackWalker
			.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
			.walk(frames ->
				Objects.requireNonNull(frames.skip(methodsToSkip)
					.findFirst()
					.orElse(null))
			);

		String methodName = stackFrame.getMethodName();

		List<Class<?>> parameterTypes = stackFrame.getMethodType().parameterList();

		if (parameterTypes.size() != args.length) {
			RuntimeException exception = new RuntimeException("Can not call SyncedNetworkObject#sendRequest for method `" + methodName + "`. Parameter count does not match. Expected: " + parameterTypes.size() + " Got: " + args.length);
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		CompletableFuture<T> future = new RemoteMethodExecuteRequest<T>(
			destinationServer,
			className,
			methodName,
			parameterTypes,
			getIdentifier(),
			args).send();

		T response;

		try {
			response = future.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException exception) {
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		if (future.state() != Future.State.SUCCESS) {
			CommunicationException exception = new CommunicationException(destinationServer);
			Logger.error(exception);
			throw new RuntimeException(exception);
		}

		return response;
	}

	protected <T> T sendRequest(DestinationServerFinder destinationServerFinder, Object... args) throws SyncedRequestException {
		return sendRequest(getDestinationServer(destinationServerFinder), args);
	}
}
