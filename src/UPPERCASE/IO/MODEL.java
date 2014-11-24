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
	 */
	public void create(JSONObject data, final CreateHandlers handlers) {

		room.send("create", data, new Method() {

			@Override
			public void handle(Object data) {

				try {

					JSONObject result = (JSONObject) data;

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

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param data
	 */
	public void create(JSONObject data) {
		create(data, null);
	}

	/**
	 * @param id
	 * @param handlers
	 */
	public void get(String id, final GetHandlers handlers) {

		room.send("get", id, new Method() {

			@Override
			public void handle(Object data) {

				try {

					JSONObject result = (JSONObject) data;

					if (result.isNull("errorMsg") != true) {
						handlers.error(result.getString("errorMsg"));
					} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
						handlers.notAuthed();
					} else if (result.isNull("savedData") == true) {
						handlers.notExists();
					} else {
						handlers.success(result.getJSONObject("savedData"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param filter
	 * @param sort
	 * @param isRandom
	 * @param handlers
	 */
	public void get(JSONObject filter, JSONObject sort, Boolean isRandom, final GetHandlers handlers) {

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

			room.send("get", params, new Method() {

				@Override
				public void handle(Object data) {

					try {

						JSONObject result = (JSONObject) data;

						if (result.isNull("errorMsg") != true) {
							handlers.error(result.getString("errorMsg"));
						} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
							handlers.notAuthed();
						} else if (result.isNull("savedData") == true) {
							handlers.notExists();
						} else {
							handlers.success(result.getJSONObject("savedData"));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param filter
	 * @param sort
	 * @param handlers
	 */
	public void get(JSONObject filter, JSONObject sort, GetHandlers handlers) {
		get(filter, sort, null, handlers);
	}

	/**
	 * @param filter
	 * @param handlers
	 */
	public void get(JSONObject filter, GetHandlers handlers) {
		get(filter, null, handlers);
	}

	/**
	 * @param data
	 * @param handlers
	 */
	public void update(JSONObject data, final UpdateHandlers handlers) {

		room.send("update", data, new Method() {

			@Override
			public void handle(Object data) {

				try {

					JSONObject result = (JSONObject) data;

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

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param id
	 * @param handlers
	 */
	public void remove(String id, final RemoveHandlers handlers) {

		room.send("remove", id, new Method() {

			@Override
			public void handle(Object data) {

				try {

					JSONObject result = (JSONObject) data;

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

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param filter
	 * @param sort
	 * @param start
	 * @param count
	 * @param handlers
	 */
	public void find(JSONObject filter, JSONObject sort, Long start, Long count, final FindHandlers handlers) {

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

			room.send("find", params, new Method() {

				@Override
				public void handle(Object data) {

					try {

						JSONObject result = (JSONObject) data;

						if (result.isNull("errorMsg") != true) {
							handlers.error(result.getString("errorMsg"));
						} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
							handlers.notAuthed();
						} else {

							List<JSONObject> savedDataSet = new ArrayList<JSONObject>();

							JSONArray jsonArray = result.getJSONArray("savedDataSet");

							for (int i = 0; i < jsonArray.length(); i += 1) {
								savedDataSet.add((JSONObject) jsonArray.get(i));
							}

							handlers.success(savedDataSet);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param filter
	 * @param sort
	 * @param count
	 * @param handlers
	 */
	public void find(JSONObject filter, JSONObject sort, Long count, FindHandlers handlers) {
		find(filter, sort, null, count, handlers);
	}

	/**
	 * @param filter
	 * @param sort
	 * @param handlers
	 */
	public void find(JSONObject filter, JSONObject sort, FindHandlers handlers) {
		find(filter, sort, null, handlers);
	}

	/**
	 * @param filter
	 * @param handlers
	 */
	public void find(JSONObject filter, FindHandlers handlers) {
		find(filter, null, handlers);
	}

	/**
	 * @param handlers
	 */
	public void find(FindHandlers handlers) {
		find(null, handlers);
	}

	/**
	 * @param filter
	 * @param handlers
	 */
	public void count(JSONObject filter, final CountHandlers handlers) {

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			room.send("count", params, new Method() {

				@Override
				public void handle(Object data) {

					try {

						JSONObject result = (JSONObject) data;

						if (result.isNull("errorMsg") != true) {
							handlers.error(result.getString("errorMsg"));
						} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
							handlers.notAuthed();
						} else {
							handlers.success(result.getLong("count"));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param handlers
	 */
	public void count(CountHandlers handlers) {
		count(null, handlers);
	}

	/**
	 * @param filter
	 * @param handlers
	 */
	public void checkIsExists(JSONObject filter, final CheckIsExistsHandlers handlers) {

		try {

			JSONObject params = new JSONObject();

			if (filter != null) {
				params.put("filter", filter);
			}

			room.send("checkIsExists", params, new Method() {

				@Override
				public void handle(Object data) {

					try {

						JSONObject result = (JSONObject) data;

						if (result.isNull("errorMsg") != true) {
							handlers.error(result.getString("errorMsg"));
						} else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
							handlers.notAuthed();
						} else {
							handlers.success(result.getBoolean("isExists"));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param handlers
	 */
	public void checkIsExists(CheckIsExistsHandlers handlers) {
		checkIsExists(null, handlers);
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
