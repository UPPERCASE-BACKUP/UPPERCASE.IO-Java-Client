package UPPERCASE.IO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UTIL {

	/**
	 * copy array.
	 * 
	 * @param jsonArray
	 * @return copiedArray
	 */
	public static JSONArray COPY_ARRAY(JSONArray jsonArray) {

		JSONArray copy = null;

		try {

			copy = new JSONArray();

			for (int i = 0; i < jsonArray.length(); i += 1) {
				Object value = jsonArray.get(i);

				// when value is Date type
				if (value instanceof Date) {
					copy.put(i, new Date(((Date) value).getTime()));
				}

				// when value is data
				else if (value instanceof JSONObject) {
					copy.put(i, COPY_DATA((JSONObject) value));
				}

				// when value is array
				else if (value instanceof JSONArray) {
					copy.put(i, COPY_ARRAY((JSONArray) value));
				}

				else {
					copy.put(i, value);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return copy;
	}

	/**
	 * copy data.
	 * 
	 * @param json
	 * @return copiedData
	 */
	public static JSONObject COPY_DATA(JSONObject json) {

		JSONObject copy = null;

		try {

			copy = new JSONObject();

			@SuppressWarnings("unchecked")
			Iterator<String> iterator = json.keys();

			while (iterator.hasNext()) {

				String name = iterator.next();
				Object value = json.get(name);

				// when value is Date type
				if (value instanceof Date) {
					copy.put(name, new Date(((Date) value).getTime()));
				}

				// when value is data
				else if (value instanceof JSONObject) {
					copy.put(name, COPY_DATA((JSONObject) value));
				}

				// when value is array
				else if (value instanceof JSONArray) {
					copy.put(name, COPY_ARRAY((JSONArray) value));
				}

				else {
					copy.put(name, value);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return copy;
	}

	/**
	 * pack data with Date type.
	 * 
	 * @param json
	 * @return packedData
	 */
	public static JSONObject PACK_DATA(JSONObject json) {

		JSONObject result = null;

		try {

			// result
			result = COPY_DATA(json);

			// date attribute names
			List<String> dateAttrNames = new ArrayList<String>();

			@SuppressWarnings("unchecked")
			Iterator<String> iterator = result.keys();

			while (iterator.hasNext()) {

				String name = iterator.next();
				Object value = result.get(name);

				// when value is Date type
				if (value instanceof Date) {

					// change to timestamp integer.
					result.put(name, ((Date) value).getTime());
					dateAttrNames.add(name);
				}

				// when value is data
				else if (value instanceof JSONObject) {
					result.put(name, PACK_DATA((JSONObject) value));
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

			result.put("__DATE_ATTR_NAMES", dateAttrNames);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * unpack data with Date type.
	 * 
	 * @param json
	 * @return unpackedData
	 */
	public static JSONObject UNPACK_DATA(JSONObject json) {

		JSONObject result = null;

		try {

			// result
			result = COPY_DATA(json);

			// when date attribute names exists
			if (!result.isNull("__DATE_ATTR_NAMES")) {

				// change timestamp integer to Date type.
				for (int i = 0; i < ((JSONArray) result.get("__DATE_ATTR_NAMES")).length(); i += 1) {
					String dateAttrName = (String) ((JSONArray) result.get("__DATE_ATTR_NAMES")).get(i);
					result.put(dateAttrName, new Date((Long) result.get(dateAttrName)));
				}
				result.remove("__DATE_ATTR_NAMES");
			}

			@SuppressWarnings("unchecked")
			Iterator<String> iterator = result.keys();

			while (iterator.hasNext()) {

				String name = iterator.next();
				Object value = result.get(name);

				// when value is data
				if (value instanceof JSONObject) {
					result.put(name, UNPACK_DATA((JSONObject) value));
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

		return result;
	}
}
