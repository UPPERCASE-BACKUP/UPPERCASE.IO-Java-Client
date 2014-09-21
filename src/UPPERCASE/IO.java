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
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class IO {

	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter out;
	private static PrintWriter pipe;
	private static int sendKey = 0;

	private static Map<String, List<Handler>> methodMap = new HashMap<String, List<Handler>>();

	private static Thread checkUpdate = new Thread() {
		public void run() {
			try {
				while (true) {

					JSONObject json = new JSONObject(reader.readLine());
					String methodName = json.getString("methodName");
					Object data = json.get("data");

					if (pipe != null && methodName.equals("__CALLBACK_" + (sendKey - 1))) {
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
			}
		}
	};

	public static void CONNECT_TO_IO_SERVER(String host, int socketServerPort) {

		try {
			socket = new Socket(host, socketServerPort);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		checkUpdate.start();
	}

	private static void _send(String methodName, Object data) {

		JSONObject sendData = new JSONObject();
		sendData.put("methodName", methodName);
		sendData.put("data", data instanceof JSONObject ? PACK_DATA((JSONObject) data) : data);
		sendData.put("sendKey", sendKey);

		sendKey += 1;

		out.println(sendData);
	}

	@SuppressWarnings("resource")
	private static Object receive() {

		try {
			PipedOutputStream output = new PipedOutputStream();
			pipe = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)), true);

			JSONObject json = new JSONObject(new BufferedReader(new InputStreamReader(new PipedInputStream(output))).readLine());

			return UNPACK_DATA(json);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static interface Handler {
		public void handle(Object data);
	}

	public static class ROOM {

		private String roomName;
		private List<String> methodNames = new ArrayList<String>();

		public ROOM(String boxName, String name) {
			_send("__ENTER_ROOM", roomName = boxName + "/" + name);
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

		public Object send(String methodName, Object data, boolean isWithCallback) {

			_send(roomName + "/" + methodName, data);

			if (isWithCallback) {
				return receive();
			}
			return null;
		}

		public void send(String methodName, Object data) {
			send(roomName + "/" + methodName, data, false);
		}

		public void exit() {

			_send("__EXIT_ROOM", roomName);

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

		public MODEL(String boxName, String name) {

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

		public JSONObject create(JSONObject data) {

			JSONObject result = ((JSONObject) room.send("create", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject get(String id) {

			JSONObject result = ((JSONObject) room.send("get", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject update(JSONObject data) {

			JSONObject result = ((JSONObject) room.send("update", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public JSONObject remove(String id) {

			JSONObject result = ((JSONObject) room.send("remove", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");
		}

		public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count) {

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

		public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count) {
			return find(null, null, null, null);
		}

		public List<JSONObject> find(JSONObject filter, JSONObject sort) {
			return find(null, null, null);
		}

		public List<JSONObject> find(JSONObject filter) {
			return find(null, null);
		}

		public List<JSONObject> find() {
			return find(null);
		}

		public Long count(JSONObject filter) {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("count", params, true));

			return result.isNull("count") ? null : result.getLong("count");
		}

		public Long count() {
			return count(null);
		}

		public Boolean checkIsExists(JSONObject filter) {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("checkIsExists", params, true));

			return result.isNull("isExists") ? null : result.getBoolean("isExists");
		}

		public Boolean checkIsExists() {
			return checkIsExists(null);
		}

		public void onNew(Handler func) {

			if (roomForCreate == null) {
				roomForCreate = new ROOM(boxName, name + "/create");
			}

			roomForCreate.on("create", func);
		}

		public void onNew(Map<String, Object> properties, Handler func) {

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
