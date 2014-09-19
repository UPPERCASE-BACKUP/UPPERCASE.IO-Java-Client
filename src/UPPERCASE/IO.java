package UPPERCASE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class IO {

	private static Socket socket;
	private static BufferedReader reader;
	private static PrintWriter out;
	private static PrintWriter pipe;
	private static int sendKey = 0;

	private static Thread checkUpdate = new Thread() {
		public void run() {
			try {
				while (true) {

					String string = reader.readLine();
					JSONObject json = new JSONObject(string);

					if (pipe != null && json.getString("methodName").equals("__CALLBACK_" + (sendKey - 1))) {
						pipe.println(string);
						pipe.close();
						pipe = null;
					} else {
						System.out.println(json);
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

	private static void send(String methodName, Object data) {

		JSONObject sendData = new JSONObject();
		sendData.put("methodName", methodName);
		sendData.put("data", data);
		sendData.put("sendKey", sendKey);

		sendKey += 1;

		out.println(sendData);
	}

	@SuppressWarnings("resource")
	private static JSONObject receive() {
		try {
			PipedOutputStream output = new PipedOutputStream();
			pipe = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)), true);
			return new JSONObject(new BufferedReader(new InputStreamReader(new PipedInputStream(output))).readLine()).getJSONObject("data");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class MODEL {

		private String roomName;

		public MODEL(String boxName, String name) {
			send("__ENTER_ROOM", roomName = boxName + "/" + name);
		}

		public JSONObject create(JSONObject data) {
			send(roomName + "/create", data);
			return receive().getJSONObject("savedData");
		}

		public JSONObject get(String id) {
			send(roomName + "/get", id);
			return receive().getJSONObject("savedData");
		}

		public void onNew() {
			send("__ENTER_ROOM", roomName + "/create");
		}
	}
}
