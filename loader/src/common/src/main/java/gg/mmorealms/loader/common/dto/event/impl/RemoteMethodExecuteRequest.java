package gg.mmorealms.loader.common.dto.event.impl;

import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
@Getter
public class RemoteMethodExecuteRequest<T> extends NetworkRequest<T> {

	private final static HashMap<Class<?>, ReturnArgLambda<Object, Object>> objectFetchMap = new HashMap<>();

	private final String identifierJson;
	private final String identifierClassName;
	private final String targetClassName;
	private final String methodName;
	private final List<SerializableObject> arguments = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public static <T> void registerObjectFetch(Class<T> clazz, ReturnArgLambda<T, Object> lambda) {
		if (objectFetchMap.containsKey(clazz)) {
			return;
		}

		Logger.log("Registering object fetch for class " + clazz.getName());
		objectFetchMap.put(clazz, (ReturnArgLambda<Object, Object>) lambda);
	}

	public RemoteMethodExecuteRequest(String redisTarget, Method method, Object identifier, Object... arguments) {
		this(redisTarget, method.getDeclaringClass(), method, identifier, arguments);
	}

	public RemoteMethodExecuteRequest(String redisTarget, Class<?> targetClass, Method method, Object identifier, Object... arguments) {
		this(redisTarget, targetClass.getName(), method.getName(), List.of(method.getParameterTypes().clone()), identifier, arguments);
	}

	@SuppressWarnings("unchecked")
	public RemoteMethodExecuteRequest(String redisTarget, String targetClassName, String methodName,
	                                  List<Class<?>> argumentTypes, Object identifier, Object... arguments) {
		super(redisTarget);

		this.targetClassName = targetClassName;
		this.methodName = methodName;
		this.identifierJson = CommonLoader.instance().toJson(identifier);
		this.identifierClassName = identifier.getClass().getName();

		for (int index = 0; index < argumentTypes.size(); index++) {
			Class<?> argumentClass = argumentTypes.get(index);
			Object argument = arguments[index];

			this.arguments.add(new SerializableObject(argumentClass, argument));
		}
	}

	private @Nullable Class<?> getTargetClass() {
		try {
			return CommonLoader.instance().getClassLoader().loadClass(targetClassName);
		} catch (ClassNotFoundException error) {
			Logger.error(error);
		}
		return null;
	}

	private Class<?>[] getArgumentTypes() {
		return this.arguments
			.stream()
			.map(SerializableObject::getObjectClass)
			.toArray(Class[]::new);
	}

	public Object[] getArguments() {
		return arguments
			.stream()
			.map(SerializableObject::getObject)
			.toArray(Object[]::new);
	}


	@SneakyThrows(value = {NoSuchMethodException.class})
	private @Nullable Method getMethod() {
		Class<?> clazz = getTargetClass();

		if (clazz == null) {
			Logger.error("Class " + targetClassName + " not found");
			return null;
		}

		return clazz.getMethod(methodName, getArgumentTypes());
	}

	private Class<?> getIdentifierClass() {
		try {
			return CommonLoader.instance().getClassLoader().loadClass(identifierClassName);
		} catch (ClassNotFoundException error) {
			Logger.error(error);
		}
		return null;
	}

	private Object getIdentifier() {
		Class<?> identifierClass = getIdentifierClass();

		if (identifierClass == null) {
			Logger.error("Class " + identifierClassName + " not found");
			return null;
		}

		return CommonLoader.instance().fromJson(identifierJson, getIdentifierClass());
	}

	@SuppressWarnings("unchecked")
	public T invoke() throws InvocationTargetException, IllegalAccessException {
		Class<?> targetClass = getTargetClass();

		if (targetClass == null) {
			Logger.error("Class " + targetClassName + " not found");
			return null;
		}

		ReturnArgLambda<Object, Object> targetObject = objectFetchMap.getOrDefault(getTargetClass(), null);

		if (targetObject == null) {
			Logger.error("Fetch for object of type " + getTargetClass().getName() + " failed. Please check if" +
				" you have correctly registered the object with " + this.getClass().getName());
			return null;
		}

		Object object = targetObject.run(getIdentifier());

		if (object == null) {
			Logger.warn("Object with id " + identifierJson + " was not found");
			return null;
		}

		Method method = getMethod();

		if (method == null) {
			Logger.error("Method " + targetClassName + "#" + methodName + " was not found");
			return null;
		}

		try {
			return (T) method.invoke(object, getArguments());
		} catch (IllegalArgumentException exception) {
			Logger.error("IllegalArgumentException: ");
			Logger.error("Expected (types): " + Arrays.toString(method.getParameterTypes()));
			Logger.error("Actual (types)  : " + Arrays.toString(getArgumentTypes()));
			Logger.error("Actual (objects): " + Arrays.toString(getArguments()));

			throw exception;
		}
	}
}
