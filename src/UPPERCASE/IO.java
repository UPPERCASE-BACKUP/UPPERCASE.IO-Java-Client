package UPPERCASE;

import static UPPERCASE.JAVA.PACK_DATA;
import static UPPERCASE.JAVA.UNPACK_DATA;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IO {

	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter out;
	private static Map<String, PrintWriter> pipeMap = new HashMap<String, PrintWriter>();
	private static int sendKey = 0;

	private static Map<String, List<Handler>> methodMap = new HashMap<String, List<Handler>>();

	private static Thread checkUpdate = new Thread() {
		public void run() {
			try {
				while (true) {

					JSONObject json = new JSONObject(reader.readLine());
					String methodName = json.getString("methodName");
					Object data = json.get("data");

					PrintWriter pipe = pipeMap.get(methodName);

					if (pipe != null) {
						pipe.println(data);
						pipe.close();
						pipe = null;
					} else {

						List<Handler> methods = methodMap.get(methodName);

						if (methods != null) {
							for (Handler method : methods) {
								method.handle(data instanceof JSONObject ? UNPACK_DATA((JSONObject) data) : data);
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

			socket = new Socket(host, socketServerPort);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		checkUpdate.start();
	}

	public static void CONNECT_TO_IO_SERVER(String host, int webServerPort, int socketServerPort) {
		CONNECT_TO_IO_SERVER(host, false, webServerPort, socketServerPort);
	}

	public static class MyThread implements Runnable {

		JSONObject sendData;

		public MyThread(JSONObject sendData) {
			this.sendData = sendData;
		}

		public void run() {
			out.println(sendData);
		}
	}

	private static Object _send(String methodName, Object data, boolean isToReceive) throws JSONException {

		JSONObject sendData = new JSONObject();
		sendData.put("methodName", methodName);
		sendData.put("data", data instanceof JSONObject ? PACK_DATA((JSONObject) data) : data);
		sendData.put("sendKey", sendKey);

		String callbackMethodName = "__CALLBACK_" + sendKey;

		sendKey += 1;

		new Thread(new MyThread(sendData)).start();

		if (isToReceive == true) {

			try {
				PipedOutputStream output = new PipedOutputStream();

				pipeMap.put(callbackMethodName, new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)), true));

				@SuppressWarnings("resource")
				JSONObject json = new JSONObject(new BufferedReader(new InputStreamReader(new PipedInputStream(output))).readLine());

				return UNPACK_DATA(json);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static interface Handler {
		public void handle(Object data);
	}

	public static class ROOM {

		private String roomName;
		private List<String> methodNames = new ArrayList<String>();

		public ROOM(String boxName, String name) throws JSONException {
			_send("__ENTER_ROOM", roomName = boxName + "/" + name, false);
		}

		public void on(String methodName, Handler method) {

			List<Handler> methods = methodMap.get(roomName + "/" + methodName);

			if (methodMap.get(roomName + "/" + methodName) == null) {
				methodMap.put(roomName + "/" + methodName, methods = new ArrayList<Handler>());
				methodNames.add(methodName);
			}

			methods.add(method);
		}

		public void off(String methodName, Handler method) {

			List<Handler> methods = methodMap.get(roomName + "/" + methodName);

			methods.remove(method);

			if (methods.size() == 0) {
				off(methodName);
			}
		}

		public void off(String methodName) {
			methodMap.remove(roomName + "/" + methodName);
			methodNames.remove(methodName);
		}

		public Object send(String methodName, Object data, boolean isWithCallback) throws JSONException {
			return _send(roomName + "/" + methodName, data, isWithCallback);
		}

		public void send(String methodName, Object data) throws JSONException {
			send(roomName + "/" + methodName, data, false);
		}

		public void exit() throws JSONException {

			_send("__EXIT_ROOM", roomName, false);

			for (String methodName : methodNames) {
				methodMap.remove(roomName + "/" + methodName);
			}
		}
	}

	public static class MODEL {

		private String boxName;
		private String name;

		private ROOM room;
		private ROOM roomForCreate;
		private Map<String, ROOM> roomsForCreate = new HashMap<String, ROOM>();

		public MODEL(String boxName, String name) throws JSONException {

			this.boxName = boxName;
			this.name = name;

			room = new ROOM(boxName, name);
		}

		public String getName() {
			return name;
		}

		public ROOM getRoom() {
			return room;
		}

		public JSONObject create(JSONObject data) throws JSONException {

			JSONObject result = ((JSONObject) room.send("create", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject get(String id) throws JSONException {

			JSONObject result = ((JSONObject) room.send("get", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject update(JSONObject data) throws JSONException {

			JSONObject result = ((JSONObject) room.send("update", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject remove(String id) throws JSONException {

			JSONObject result = ((JSONObject) room.send("remove", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count) throws JSONException {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}
			if (sort != null) {
				params.put("sort", sort);
			}
			if (start != null) {
				params.put("start", start);
			}
			if (count != null) {
				params.put("count", count);
			}

			JSONObject result = ((JSONObject) room.send("find", params, true));

			if (result.isNull("savedDataSet")) {
				return null;
			}

			List<JSONObject> savedDataSet = new ArrayList<JSONObject>();

			JSONArray jsonArray = result.getJSONArray("savedDataSet");

			for (int i = 0; i < jsonArray.length(); i += 1) {
				savedDataSet.add((JSONObject) jsonArray.get(i));
			}

			return savedDataSet;
		}

		public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count) throws JSONException {
			return find(null, null, null, null);
		}

		public List<JSONObject> find(JSONObject filter, JSONObject sort) throws JSONException {
			return find(null, null, null);
		}

		public List<JSONObject> find(JSONObject filter) throws JSONException {
			return find(null, null);
		}

		public List<JSONObject> find() throws JSONException {
			return find(null);
		}

		public Long count(JSONObject filter) throws JSONException {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("count", params, true));

			return result.isNull("count") ? null : result.getLong("count");
		}

		public Long count() throws JSONException {
			return count(null);
		}

		public Boolean checkIsExists(JSONObject filter) throws JSONException {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("checkIsExists", params, true));

			return result.isNull("isExists") ? null : result.getBoolean("isExists");
		}

		public Boolean checkIsExists() throws JSONException {
			return checkIsExists(null);
		}

		public void onNew(Handler func) throws JSONException {

			if (roomForCreate == null) {
				roomForCreate = new ROOM(boxName, name + "/create");
			}

			roomForCreate.on("create", func);
		}

		public void onNew(Map<String, Object> properties, Handler func) throws JSONException {

			for (String propertyName : properties.keySet()) {
				Object value = properties.get(propertyName);

				ROOM room = roomsForCreate.get(propertyName + "/" + value);

				if (room == null) {
					roomsForCreate.put(propertyName + "/" + value, room = new ROOM(boxName, name + "/" + propertyName + "/" + value + "/create"));
				}

				room.on("create", func);
			}
		}
	}
}
