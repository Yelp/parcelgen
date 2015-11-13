package com.yelp.parcelgen;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    /**
     * JsonUtil consists only of static methods and cannot be instantiated.
     */
    private JsonUtil() {
    }

    /**
     * @return An ArrayList of new objects created by <code>creator</code>.
     * @throws JSONException If parsing of one of the objects fails.
     */
    public static <E> ArrayList<E> parseJsonList(JSONArray array, JsonParser<E> creator)
            throws JSONException {
        if (array == null) {
            return new ArrayList<E>();
        }
        int size = array.length();
        ArrayList<E> list = new ArrayList<E>(size);
        for (int i = 0; i < size; i++) {
            if (creator.getElementType() == JsonParser.ARRAY_TYPE) {
                list.add(creator.parse(array.getJSONArray(i)));
            } else {
                if (array.isNull(i)) {
                    list.add(null);
                } else {
                    list.add(creator.parse(array.getJSONObject(i)));
                }
            }
        }
        return list;
    }

    /**
     * If object[key] is a valid integer Unix timestamp, returns the appropriate Date for that
     * timestamp. Otherwise, returns null.
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
     * Returns a primitive array of the JSONArray's string values or an empty array if the provided
     * array is empty. If an entry is invalid or null, the corresponding index in the returned array
     * will be <code>null</code>.
     */
    public static String[] getStringArray(JSONArray array) {
        if (array == null) {
            return new String[0];
        }

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
     * @return An ArrayList of the elements of array converted to strings, or the empty list if
     * array is null. Empty strings in the input array are preserved.
     */
    public static List<String> getStringList(JSONArray array) {
        String[] stringArray = getStringArray(array);
        if (stringArray == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(stringArray);
    }

    public static <Param extends Parcelable> Map<String, Param> parseJsonMap(JSONObject object,
            JsonParser<Param> creator) throws JSONException {
        Map<String, Param> map = new ArrayMap<String, Param>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, creator.parse((JSONObject) object.get(key)));
        }
        return map;
    }

    public static Bundle toBundle(Map<String, ? extends Parcelable> input) {
        Bundle output = new Bundle();
        for (String key : input.keySet()) {
            output.putParcelable(key, input.get(key));
        }
        return output;
    }

    public static <T extends Parcelable> Map<String, T> fromBundle(Bundle input, Class<T> claz) {
        input.setClassLoader(claz.getClassLoader());
        Map<String, T> output = new ArrayMap<String, T>();
        for(String key : input.keySet()) {
            output.put(key, (T) input.getParcelable(key));
        }
        return output;
    }
}
