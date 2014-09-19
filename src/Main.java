import static UPPERCASE.IO.*;

import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {

		CONNECT_TO_IO_SERVER("127.0.0.1", 9126);

		MODEL TestModel = new MODEL("TestBox", "Test");

		JSONObject data = new JSONObject();
		data.put("name", "YJ Sim");
		data.put("age", 27);
		data.put("isMan", true);

		TestModel.onNew();

		JSONObject savedData = TestModel.create(data);

		System.out.println(savedData);
		System.out.println(TestModel.get(savedData.getString("id")));
	}
}
