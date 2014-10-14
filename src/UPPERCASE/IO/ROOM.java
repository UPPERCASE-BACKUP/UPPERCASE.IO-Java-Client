package UPPERCASE.IO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ROOM {

	private String roomName;
	private Map<String, List<Method>> methodMap = CONNECT.getMethodMap();
	private List<String> methodNames = new ArrayList<String>();

	/**
	 * @param boxName
	 * @param name
	 */
	public ROOM(String boxName, String name) {
		CONNECT.enterRoom(roomName = boxName + "/" + name);
	}

	/**
	 * @param methodName
	 * @param method
	 */
	public void on(String methodName, Method method) {

		List<Method> methods = methodMap.get(roomName + "/" + methodName);

		if (methodMap.get(roomName + "/" + methodName) == null) {
			methodMap.put(roomName + "/" + methodName, methods = new ArrayList<Method>());
			methodNames.add(methodName);
		}

		methods.add(method);
	}

	/**
	 * @param methodName
	 * @param method
	 */
	public void off(String methodName, Method method) {

		List<Method> methods = methodMap.get(roomName + "/" + methodName);

		methods.remove(method);

		if (methods.size() == 0) {
			off(methodName);
		}
	}

	/**
	 * @param methodName
	 */
	public void off(String methodName) {
		methodMap.remove(roomName + "/" + methodName);
		methodNames.remove(methodName);
	}

	/**
	 * @param methodName
	 * @param data
	 * @param isWithCallback
	 * @return returnData
	 */
	public Object send(String methodName, Object data, boolean isWithCallback) {
		return CONNECT.send(roomName + "/" + methodName, data, isWithCallback);
	}

	/**
	 * @param methodName
	 * @param data
	 */
	public void send(String methodName, Object data) {
		send(roomName + "/" + methodName, data, false);
	}

	/**
	 * exit.
	 */
	public void exit() {

		CONNECT.send("__EXIT_ROOM", roomName, false);

		for (String methodName : methodNames) {
			methodMap.remove(roomName + "/" + methodName);
		}
	}
}
