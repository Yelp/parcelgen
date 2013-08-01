package com.yelp.parcelgen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class JsonUtil {

	/**
	 * JsonUtil consists only of static methods and cannot be instantiated.
	 */
	private JsonUtil() {}

	/**
	 * Parse the provided JSONArray as an array of Json representations of E
	 * which can be generated using the provided <code>creator</code>.
	 * @param <E>
	 * @param array
	 * @param creator
	 * @return An ArrayList of new objects created by <code>creator</code>.
	 * @throws JSONException If parsing of one of the objects fails.
	 */
	public static <E> ArrayList<E> parseJsonList(JSONArray array, JsonParser<E> creator) throws JSONException {
		if (array == null) {
			return new ArrayList<E>();
		}
		int size = array.length();
		ArrayList<E> list = new ArrayList<E>(size);
		for (int i = 0; i < size; i++) {
			if (creator.getElementType() == JsonParser.ARRAY_TYPE) {
				list.add(creator.parse(array.getJSONArray(i)));
			} else {
				if(array.isNull(i)){
					list.add(null);
				} else {
					list.add(creator.parse(array.getJSONObject(i)));
				}
			}
		}
		return list;
	}

	/**
	 * If object[key] is a valid integer Unix timestamp,
	 * returns the appropriate Date for that timestamp.
	 * Otherwise, returns null.
	 * @param object
	 * @param key
	 * @return
	 */
	public static Date parseTimestamp(JSONObject object, String key) {
		if (object != null && !object.isNull(key)) {
			try {
				return new Date(object.getLong(key) * 1000L);
			} catch (JSONException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns a primitive array of the JSONArray's string values
	 * or an empty array if the provided array is empty.
	 * If an entry is invalid or null, the corresponding index
	 * in the returned array will be <code>null</code>.
	 * @param array
	 * @return
	 */
	public static String[] getStringArray(JSONArray array) {
		if (array == null) return new String[0];

		int len = array.length();
		String[] list = new String[len];
		for (int i = 0; i < len; i++) {
			String obj = array.optString(i, null);
			if (obj != null) {
				list[i] = obj;
			}
		}
		return list;
	}

	/**
	 * Returns a new ArrayList by calling optString() on each
	 * index in the provided array.
	 * Empty strings in the input array are preserved.
	 * @param array
	 * @return An ArrayList of the elements of array converted to strings,
	 * or the empty list if array is null.
	 */
	public static List<String> getStringList(JSONArray array) {
		String[] stringArray = getStringArray(array);
		if (stringArray == null) return Collections.emptyList();

		return Arrays.asList(stringArray);
	}

}
