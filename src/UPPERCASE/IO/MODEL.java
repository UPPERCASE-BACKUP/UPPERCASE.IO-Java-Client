package UPPERCASE.IO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import UPPERCASE.IO.MODELHandlers.CheckIsExistsHandlers;
import UPPERCASE.IO.MODELHandlers.CountHandlers;
import UPPERCASE.IO.MODELHandlers.CreateHandlers;
import UPPERCASE.IO.MODELHandlers.FindHandlers;
import UPPERCASE.IO.MODELHandlers.GetHandlers;
import UPPERCASE.IO.MODELHandlers.RemoveHandlers;
import UPPERCASE.IO.MODELHandlers.UpdateHandlers;

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

	public JSONObject create(JSONObject data, CreateHandlers handlers) {

		try {

			JSONObject result = ((JSONObject) room.send("create", data, true));

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("validErrors") != true) {
					handlers.notValid(result.getJSONObject("validErrors"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else {
					handlers.success(result.getJSONObject("savedData"));
				}
			}

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject create(JSONObject data) {
		return create(data, null);
	}

	public JSONObject get(String id, GetHandlers handlers) {

		try {

			JSONObject result = ((JSONObject) room.send("get", id, true));

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else if (result.isNull("savedData") == true) {
					handlers.notExists();
				} else {
					handlers.success(result.getJSONObject("savedData"));
				}
			}

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject get(String id) {
		return get(id, null);
	}

	public JSONObject update(JSONObject data, UpdateHandlers handlers) {

		try {

			JSONObject result = ((JSONObject) room.send("update", data, true));

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("validErrors") != true) {
					handlers.notValid(result.getJSONObject("validErrors"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else if (result.isNull("savedData") == true) {
					handlers.notExists();
				} else {
					handlers.success(result.getJSONObject("savedData"));
				}
			}

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject update(JSONObject data) {
		return update(data, null);
	}

	public JSONObject remove(String id, RemoveHandlers handlers) {

		try {

			JSONObject result = ((JSONObject) room.send("remove", id, true));

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else if (result.isNull("savedData") == true) {
					handlers.notExists();
				} else {
					handlers.success(result.getJSONObject("savedData"));
				}
			}

			return result.isNull("savedData") ? null : result.getJSONObject("savedData");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject remove(String id) {
		return remove(id, null);
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count, FindHandlers handlers) {

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

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else {
					handlers.success(savedDataSet);
				}
			}

			return savedDataSet;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count) {
		return find(filter, sort, start, count, null);
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count, FindHandlers handlers) {
		return find(filter, sort, null, count, handlers);
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count) {
		return find(filter, sort, count, (FindHandlers) null);
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort, FindHandlers handlers) {
		return find(filter, sort, null, handlers);
	}

	public List<JSONObject> find(JSONObject filter, JSONObject sort) {
		return find(filter, sort, (FindHandlers) null);
	}

	public List<JSONObject> find(JSONObject filter, FindHandlers handlers) {
		return find(filter, null, handlers);
	}

	public List<JSONObject> find(JSONObject filter) {
		return find(filter, (FindHandlers) null);
	}

	public List<JSONObject> find(FindHandlers handlers) {
		return find(null, handlers);
	}

	public List<JSONObject> find() {
		return find((FindHandlers) null);
	}

	public Long count(JSONObject filter, CountHandlers handlers) {

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			JSONObject result = ((JSONObject) room.send("count", params, true));

			if (handlers != null) {
				if (result.isNull("errorMsg") != true) {
					handlers.error(result.getString("errorMsg"));
				} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
					handlers.notAuthed();
				} else {
					handlers.success(result.getLong("count"));
				}
			}

			return result.isNull("count") ? null : result.getLong("count");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Long count(JSONObject filter) {
		return count(filter, null);
	}

	public Long count(CountHandlers handlers) {
		return count(null, handlers);
	}

	public Long count() {
		return count((CountHandlers) null);
	}

	public Boolean checkIsExists(JSONObject filter, CheckIsExistsHandlers handlers) {

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

	public Boolean checkIsExists(JSONObject filter) {
		return checkIsExists(filter, null);
	}

	public Boolean checkIsExists(CheckIsExistsHandlers handlers) {
		return checkIsExists(null, handlers);
	}

	public Boolean checkIsExists() {
		return checkIsExists((CheckIsExistsHandlers) null);
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
