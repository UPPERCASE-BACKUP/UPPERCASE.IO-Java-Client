package UPPERCASE.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import UPPERCASE.JAVA.JSON.UTIL;

public class CONNECT {

	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter out;
	private static int sendKey = 0;

	private static List<String> enterRoomNames = new ArrayList<String>();
	private static Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();

	private static boolean isDisconnected;
	private static DisconnectedHandler disconnectedHandler;

	private static Thread checkUpdateThread;

	/**
	 * @param host
	 * @param socketServerPort
	 * @param disconnectedHandler
	 */
	public static void CONNECT_TO_ROOM_SERVER(String host, int socketServerPort, DisconnectedHandler _disconnectedHandler) {

		disconnectedHandler = _disconnectedHandler;

		try {

			socket = new Socket(host, socketServerPort);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		isDisconnected = false;

		// create check update thread.
		if (checkUpdateThread == null) {
			checkUpdateThread = new Thread() {

				public void run() {
					try {
						while (true) {

							if (isDisconnected != true) {

								String str = reader.readLine();

								if (str != null) {

									JSONObject json = new JSONObject(str);

									String methodName = json.getString("methodName");
									Object data = null;

									if (json.isNull("data") != true) {
										data = json.get("data");
									}

									List<Method> methods = methodMap.get(methodName);

									if (methods != null) {
										for (Method method : methods) {
											method.handle(data instanceof JSONObject ? UTIL.UNPACK_DATA((JSONObject) data) : data);
										}
									}
								}

								// disconnected
								else {
									isDisconnected = true;
									disconnectedHandler.handle();
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};

			checkUpdateThread.start();
		}

		for (String roomName : enterRoomNames) {
			send("__ENTER_ROOM", roomName);
		}
	}

	/**
	 * @param doorHost
	 * @param isSecure
	 * @param webServerPort
	 * @param socketServerPort
	 * @param disconnectedHandler
	 */
	public static boolean CONNECT_TO_IO_SERVER(String doorHost, boolean isSecure, int webServerPort, int socketServerPort, DisconnectedHandler disconnectedHandler) {

		String host = null;

		try {

			URL url = new URL((isSecure ? "https://" : "http://") + doorHost + ":" + webServerPort + "/__SOCKET_SERVER_HOST?defaultHost=" + doorHost);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			host = "";
			String line;

			while ((line = rd.readLine()) != null) {
				host += line;
			}

			rd.close();

		} catch (IOException e) {

			e.printStackTrace();

			disconnectedHandler.handle();
		}

		if (host != null) {

			CONNECT_TO_ROOM_SERVER(host, socketServerPort, disconnectedHandler);

			return true;
		}

		return false;
	}

	/**
	 * @param host
	 * @param webServerPort
	 * @param socketServerPort
	 * @param disconnectedHandler
	 */
	public static boolean CONNECT_TO_IO_SERVER(String host, int webServerPort, int socketServerPort, DisconnectedHandler disconnectedHandler) {
		return CONNECT_TO_IO_SERVER(host, false, webServerPort, socketServerPort, disconnectedHandler);
	}

	/**
	 * @param methodName
	 * @param method
	 */
	public static void on(String methodName, Method method) {

		List<Method> methods = methodMap.get(methodName);

		if (methodMap.get(methodName) == null) {
			methodMap.put(methodName, methods = new ArrayList<Method>());
		}

		methods.add(method);
	}

	/**
	 * @param methodName
	 * @param method
	 */
	public static void off(String methodName, Method method) {

		List<Method> methods = methodMap.get(methodName);

		methods.remove(method);

		if (methods.size() == 0) {
			off(methodName);
		}
	}

	/**
	 * @param methodName
	 */
	public static void off(String methodName) {
		methodMap.remove(methodName);
	}

	private static class SendThread implements Runnable {

		JSONObject sendData;

		public SendThread(JSONObject sendData) {
			this.sendData = sendData;
		}

		public void run() {
			out.write(sendData + "\r\n");
			out.flush();
		}
	}

	public static void send(String methodName, Object data, final Method method) {

		try {

			JSONObject sendData = new JSONObject();
			sendData.put("methodName", methodName);
			sendData.put("data", data instanceof JSONObject ? UTIL.PACK_DATA((JSONObject) data) : data);
			sendData.put("sendKey", sendKey);

			final String callbackName = "__CALLBACK_" + sendKey;

			if (method != null) {

				// on callback.
				on(callbackName, new Method() {

					@Override
					public void handle(Object data) {

						// run callback.
						method.handle(data);

						// off callback.
						off(callbackName);
					}
				});
			}

			sendKey += 1;

			new Thread(new SendThread(sendData)).start();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void send(String methodName, Object data) {
		send(methodName, data, null);
	}

	public static void enterRoom(String roomName) {

		enterRoomNames.add(roomName);

		send("__ENTER_ROOM", roomName);
	}

	public static void exitRoom(String roomName) {

		send("__EXIT_ROOM", roomName);

		enterRoomNames.remove(roomName);
	}
}
