package UPPERCASE;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JAVA {

	/**
	 * pack data with Date type.
	 */
	public static JSONObject PACK_DATA(JSONObject json) {

		try {

			// date attribute names
			List<String> dateAttrNames = new ArrayList<String>();

			@SuppressWarnings("unchecked")
			Iterator<String> iterator = json.keys();

			while (iterator.hasNext()) {

				String name = iterator.next();
				Object value = json.get(name);

				// when value is Date type
				if (value instanceof Date) {

					// change to timestamp integer.
					json.put(name, ((Date) value).getTime());
					dateAttrNames.add(name);
				}

				// when value is data
				else if (value instanceof JSONObject) {
					json.put(name, PACK_DATA((JSONObject) value));
				}

				// when value is array
				else if (value instanceof JSONArray) {

					for (int i = 0; i < ((JSONArray) value).length(); i += 1) {
						Object v = ((JSONArray) value).get(i);

						if (v instanceof JSONObject) {
							((JSONArray) value).put(i, PACK_DATA((JSONObject) v));
						}
					}
				}
			}

			json.put("__DATE_ATTR_NAMES", dateAttrNames);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * unpack data with Date type.
	 */
	public static JSONObject UNPACK_DATA(JSONObject json) {

		try {

			// when date attribute names exists
			if (!json.isNull("__DATE_ATTR_NAMES")) {

				// change timestamp integer to Date type.
				for (int i = 0; i < ((JSONArray) json.get("__DATE_ATTR_NAMES")).length(); i += 1) {
					String dateAttrName = (String) ((JSONArray) json.get("__DATE_ATTR_NAMES")).get(i);
					json.put(dateAttrName, new Date((Long) json.get(dateAttrName)));
				}
				json.remove("__DATE_ATTR_NAMES");
			}

			@SuppressWarnings("unchecked")
			Iterator<String> iterator = json.keys();

			while (iterator.hasNext()) {

				String name = iterator.next();
				Object value = json.get(name);

				// when value is data
				if (value instanceof JSONObject) {
					json.put(name, UNPACK_DATA((JSONObject) value));
				}

				// when value is array
				else if (value instanceof JSONArray) {

					for (int i = 0; i < ((JSONArray) value).length(); i += 1) {
						Object v = ((JSONArray) value).get(i);

						if (v instanceof JSONObject) {
							((JSONArray) value).put(i, UNPACK_DATA((JSONObject) v));
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
}
