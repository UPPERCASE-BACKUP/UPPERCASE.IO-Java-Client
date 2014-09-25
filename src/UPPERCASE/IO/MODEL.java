package UPPERCASE.IO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MODEL {

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

		try {

			JSONObject result = ((JSONObject) room.send("create", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject get(String id) {

		try {

			JSONObject result = ((JSONObject) room.send("get", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject update(JSONObject data) {

		try {

			JSONObject result = ((JSONObject) room.send("update", data, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject remove(String id) {

		try {

			JSONObject result = ((JSONObject) room.send("remove", id, true));

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count) {

		try {

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

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
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

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("count", params, true));

			return result.isNull("count") ? null : result.getLong("count");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Long count() {
		return count(null);
	}

	public Boolean checkIsExists(JSONObject filter) {

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("checkIsExists", params, true));

			return result.isNull("isExists") ? null : result.getBoolean("isExists");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Boolean checkIsExists() {
		return checkIsExists(null);
	}

	public void onNew(Method func) {

		if (roomForCreate == null) {
			roomForCreate = new ROOM(boxName, name + "/create");
		}

		roomForCreate.on("create", func);
	}

	public void onNew(Map<String, Object> properties, Method func) {

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
