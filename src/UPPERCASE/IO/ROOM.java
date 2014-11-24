package UPPERCASE.IO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ROOM {

	private String roomName;
	private Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();
	private boolean isExited;

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

		CONNECT.on(roomName + "/" + methodName, method);

		if (methodMap.get(roomName + "/" + methodName) == null) {
			methodMap.put(roomName + "/" + methodName, methods = new ArrayList<Method>());
		}

		methods.add(method);
	}

	/**
	 * @param methodName
	 * @param method
	 */
	public void off(String methodName, Method method) {

		List<Method> methods = methodMap.get(roomName + "/" + methodName);

		CONNECT.off(roomName + "/" + methodName, method);

		methods.remove(method);

		if (methods.size() == 0) {
			off(methodName);
		}
	}

	/**
	 * @param methodName
	 */
	public void off(String methodName) {

		List<Method> methods = methodMap.get(roomName + "/" + methodName);

		for (Method method : methods) {
			CONNECT.off(roomName + "/" + methodName, method);
		}

		methodMap.remove(roomName + "/" + methodName);
	}

	/**
	 * @param methodName
	 * @param data
	 * @param method
	 */
	public void send(String methodName, Object data, Method method) {

		if (isExited != true) {

			CONNECT.send(roomName + "/" + methodName, data, method);

		} else {
			System.out.println("[UPPERCASE.IO-ROOM] `ROOM.send` ERROR! ROOM EXITED!");
		}
	}

	/**
	 * @param methodName
	 * @param data
	 */
	public void send(String methodName, Object data) {
		send(methodName, data, null);
	}

	/**
	 * exit.
	 */
	public void exit() {

		if (isExited != true) {

			CONNECT.exitRoom(roomName);

			for (String methodName : methodMap.keySet()) {
				off(methodName);
			}

			// free method map.
			methodMap = null;

			isExited = true;
		}
	}
}
