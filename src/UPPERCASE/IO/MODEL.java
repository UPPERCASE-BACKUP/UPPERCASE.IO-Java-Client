package UPPERCASE.IO;

import java.util.ArrayList;
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

	/**
	 * @param boxName
	 * @param name
	 */
	public MODEL(String boxName, String name) {

		this.boxName = boxName;
		this.name = name;

		room = new ROOM(boxName, name);
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return room
	 */
	public ROOM getRoom() {
		return room;
	}

	/**
	 * @param data
	 * @param handlers
	 * @return savedData
	 */
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

	/**
	 * @param data
	 * @return savedData
	 */
	public JSONObject create(JSONObject data) {
		return create(data, null);
	}

	/**
	 * @param id
	 * @param handlers
	 * @return savedData
	 */
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

	/**
	 * @param id
	 * @return savedData
	 */
	public JSONObject get(String id) {
		return get(id, null);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param isRandom
	 * @param handlers
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter, JSONObject sort, Boolean isRandom, GetHandlers handlers) {

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}
			if (sort != null) {
				params.put("sort", sort);
			}
			if (isRandom != null) {
				params.put("isRandom", isRandom);
			}

			JSONObject result = ((JSONObject) room.send("get", params, true));

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

	/**
	 * @param filter
	 * @param sort
	 * @param isRandom
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter, JSONObject sort, Boolean isRandom) {
		return get(filter, sort, isRandom, null);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param handlers
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter, JSONObject sort, GetHandlers handlers) {
		return get(filter, sort, null, handlers);
	}

	/**
	 * @param filter
	 * @param sort
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter, JSONObject sort) {
		return get(filter, sort, (GetHandlers) null);
	}

	/**
	 * @param filter
	 * @param handlers
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter, GetHandlers handlers) {
		return get(filter, null, handlers);
	}

	/**
	 * @param filter
	 * @return savedData
	 */
	public JSONObject get(JSONObject filter) {
		return get(filter, (GetHandlers) null);
	}

	/**
	 * @param data
	 * @param handlers
	 * @return savedData
	 */
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

	/**
	 * @param data
	 * @return savedData
	 */
	public JSONObject update(JSONObject data) {
		return update(data, null);
	}

	/**
	 * @param id
	 * @param handlers
	 * @return savedData
	 */
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

	/**
	 * @param id
	 * @return savedData
	 */
	public JSONObject remove(String id) {
		return remove(id, null);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param start
	 * @param count
	 * @param handlers
	 * @return savedDataSet
	 */
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

	/**
	 * @param filter
	 * @param sort
	 * @param start
	 * @param count
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long start, Long count) {
		return find(filter, sort, start, count, null);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param count
	 * @param handlers
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count, FindHandlers handlers) {
		return find(filter, sort, null, count, handlers);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param count
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, JSONObject sort, Long count) {
		return find(filter, sort, count, (FindHandlers) null);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param handlers
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, JSONObject sort, FindHandlers handlers) {
		return find(filter, sort, null, handlers);
	}

	/**
	 * @param filter
	 * @param sort
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, JSONObject sort) {
		return find(filter, sort, (FindHandlers) null);
	}

	/**
	 * @param filter
	 * @param handlers
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter, FindHandlers handlers) {
		return find(filter, null, handlers);
	}

	/**
	 * @param filter
	 * @return savedDataSet
	 */
	public List<JSONObject> find(JSONObject filter) {
		return find(filter, (FindHandlers) null);
	}

	/**
	 * @param handlers
	 * @return savedDataSet
	 */
	public List<JSONObject> find(FindHandlers handlers) {
		return find(null, handlers);
	}

	/**
	 * @return savedDataSet
	 */
	public List<JSONObject> find() {
		return find((FindHandlers) null);
	}

	/**
	 * @param filter
	 * @param handlers
	 * @return count
	 */
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

	/**
	 * @param filter
	 * @return count
	 */
	public Long count(JSONObject filter) {
		return count(filter, null);
	}

	/**
	 * @param handlers
	 * @return count
	 */
	public Long count(CountHandlers handlers) {
		return count(null, handlers);
	}

	/**
	 * @return count
	 */
	public Long count() {
		return count((CountHandlers) null);
	}

	/**
	 * @param filter
	 * @param handlers
	 * @return isExists
	 */
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

	/**
	 * @param filter
	 * @return isExists
	 */
	public Boolean checkIsExists(JSONObject filter) {
		return checkIsExists(filter, null);
	}

	/**
	 * @param handlers
	 * @return isExists
	 */
	public Boolean checkIsExists(CheckIsExistsHandlers handlers) {
		return checkIsExists(null, handlers);
	}

	/**
	 * @return isExists
	 */
	public Boolean checkIsExists() {
		return checkIsExists((CheckIsExistsHandlers) null);
	}

	private interface Rooms {
		public void exit();
	}

	/**
	 * @param handler
	 * @return rooms
	 */
	public Rooms onNew(Method handler) {

		final ROOM roomForCreate = new ROOM(boxName, name + "/create");

		roomForCreate.on("create", handler);

		return new Rooms() {

			@Override
			public void exit() {
				roomForCreate.exit();
			}
		};
	}

	/**
	 * @param properties
	 * @param handler
	 * @return rooms
	 */
	public Rooms onNew(final Map<String, Object> properties, final Method handler) {

		for (String propertyName : properties.keySet()) {
			Object value = properties.get(propertyName);

			final ROOM roomForCreate = new ROOM(boxName, name + "/" + propertyName + "/" + value + "/create");

			roomForCreate.on("create", new Method() {

				@Override
				public void handle(Object savedData) {

					for (String propertyName : properties.keySet()) {
						Object value = properties.get(propertyName);

						try {

							if (!((JSONObject) savedData).get(propertyName).equals(value)) {
								return;
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					handler.handle(savedData);
				}
			});

			return new Rooms() {

				@Override
				public void exit() {
					roomForCreate.exit();
				}
			};
		}

		return null;
	}

	/**
	 * @param handler
	 * @return rooms
	 */
	public Rooms onRemove(Method handler) {

		final ROOM roomForRemove = new ROOM(boxName, name + "/remove");

		roomForRemove.on("remove", handler);

		return new Rooms() {

			@Override
			public void exit() {
				roomForRemove.exit();
			}
		};
	}

	/**
	 * @param properties
	 * @param handler
	 * @return rooms
	 */
	public Rooms onRemove(final Map<String, Object> properties, final Method handler) {

		for (String propertyName : properties.keySet()) {
			Object value = properties.get(propertyName);

			final ROOM roomForRemove = new ROOM(boxName, name + "/" + propertyName + "/" + value + "/remove");

			roomForRemove.on("remove", new Method() {

				@Override
				public void handle(Object savedData) {

					for (String propertyName : properties.keySet()) {
						Object value = properties.get(propertyName);

						try {

							if (!((JSONObject) savedData).get(propertyName).equals(value)) {
								return;
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					handler.handle(savedData);
				}
			});

			return new Rooms() {

				@Override
				public void exit() {
					roomForRemove.exit();
				}
			};
		}

		return null;
	}
}
