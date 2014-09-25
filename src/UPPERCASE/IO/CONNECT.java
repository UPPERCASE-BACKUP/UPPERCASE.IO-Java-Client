package UPPERCASE.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class CONNECT {

	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter out;
	private static Map<String, PrintWriter> pipeMap = new HashMap<String, PrintWriter>();
	private static int sendKey = 0;

	private static Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();

	private static Thread checkUpdate = new Thread() {
		public void run() {
			try {
				while (true) {

					String str = reader.readLine();

					if (str == null) {
						// TODO:!
					}

					JSONObject json = new JSONObject(str);
					String methodName = json.getString("methodName");
					Object data = json.get("data");

					PrintWriter pipe = pipeMap.get(methodName);

					if (pipe != null) {
						pipe.println(data);
						pipe.close();
						pipe = null;
					} else {

						List<Method> methods = getMethodMap().get(methodName);

						if (methods != null) {
							for (Method method : methods) {
								method.handle(data instanceof JSONObject ? UTIL.UNPACK_DATA((JSONObject) data) : data);
							}
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

	public static void CONNECT_TO_ROOM_SERVER(String host, int socketServerPort) {

		try {

			socket = new Socket(host, socketServerPort);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		checkUpdate.start();
	}

	public static void CONNECT_TO_IO_SERVER(String doorHost, boolean isSecure, int webServerPort, int socketServerPort) {

		try {

			URL url = new URL((isSecure ? "https://" : "http://") + doorHost + ":" + webServerPort + "/__SOCKET_SERVER_HOST?defaultHost=" + doorHost);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			String host = "";
			String line;

			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = rd.readLine()) != null) {
				host += line;
			}
			rd.close();

			CONNECT_TO_ROOM_SERVER(host, socketServerPort);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void CONNECT_TO_IO_SERVER(String host, int webServerPort, int socketServerPort) {
		CONNECT_TO_IO_SERVER(host, false, webServerPort, socketServerPort);
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

	public static Object send(String methodName, Object data, boolean isToReceive) {

		try {

			JSONObject sendData = new JSONObject();
			sendData.put("methodName", methodName);
			sendData.put("data", data instanceof JSONObject ? UTIL.PACK_DATA((JSONObject) data) : data);
			sendData.put("sendKey", sendKey);

			String callbackMethodName = "__CALLBACK_" + sendKey;

			sendKey += 1;

			new Thread(new SendThread(sendData)).start();

			if (isToReceive == true) {

				try {
					PipedOutputStream output = new PipedOutputStream();

					pipeMap.put(callbackMethodName, new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)), true));

					@SuppressWarnings("resource")
					JSONObject json = new JSONObject(new BufferedReader(new InputStreamReader(new PipedInputStream(output))).readLine());

					return UTIL.UNPACK_DATA(json);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Map<String, List<Method>> getMethodMap() {
		return methodMap;
	}
}
