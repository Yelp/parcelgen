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
    public static <T extends Parcelable> ArrayList<T> parseJsonList(JSONArray array, JsonParser<T> creator)
            throws JSONException {
        if (array == null) {
            return new ArrayList<T>();
        }
        int size = array.length();
        ArrayList<T> list = new ArrayList<T>(size);
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

    public static List<Boolean> parseBooleanJsonList(JSONArray array)
            throws JSONException {
        if (array == null) {
            return new ArrayList<Boolean>();
        }

        int size = array.length();
        ArrayList<Boolean> list = new ArrayList<Boolean>(size);
        for (int i = 0; i < size; i++) {
            list.add(array.getBoolean(i));
        }

        return list;
    }

    public static List<Double> parseDoubleJsonList(JSONArray array)
            throws JSONException {
        if (array == null) {
            return new ArrayList<Double>();
        }

        int size = array.length();
        ArrayList<Double> list = new ArrayList<Double>(size);
        for (int i = 0; i < size; i++) {
            list.add(array.getDouble(i));
        }

        return list;
    }

    public static List<Integer> parseIntegerJsonList(JSONArray array)
            throws JSONException {
        if (array == null) {
            return new ArrayList<Integer>();
        }

        int size = array.length();
        ArrayList<Integer> list = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            list.add(array.getInt(i));
        }

        return list;
    }

    public static List<Long> parseLongJsonList(JSONArray array)
            throws JSONException {
        if (array == null) {
            return new ArrayList<Long>();
        }

        int size = array.length();
        ArrayList<Long> list = new ArrayList<Long>(size);
        for (int i = 0; i < size; i++) {
            list.add(array.getLong(i));
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
            } catch (JSONException T) {
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
     * @return An mutable ArrayList of the elements of array converted to strings, or the empty list if
     * array is null. Empty strings in the input array are preserved.
     */
    public static List<String> getStringList(JSONArray array) {
        String[] stringArray = getStringArray(array);
        if (stringArray == null) {
            return new ArrayList<String>();
        }

        return new ArrayList<String>(Arrays.asList(stringArray));
    }

    public static <T extends Parcelable> Map<String, T> parseJsonMap(JSONObject object,
            JsonParser<T> creator) throws JSONException {
        Map<String, T> map = new ArrayMap<String, T>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, creator.parse((JSONObject) object.get(key)));
        }
        return map;
    }

    public static Map<String, Boolean> parseBooleanJsonMap(JSONObject object)
            throws JSONException {
        Map<String, Boolean> map = new ArrayMap<String, Boolean>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.getBoolean(key));
        }
        return map;
    }

    public static Map<String, Double> parseDoubleJsonMap(JSONObject object)
            throws JSONException {
        Map<String, Double> map = new ArrayMap<String, Double>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.getDouble(key));
        }
        return map;
    }

    public static Map<String, Integer> parseIntegerJsonMap(JSONObject object)
            throws JSONException {
        Map<String, Integer> map = new ArrayMap<String, Integer>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.getInt(key));
        }
        return map;
    }

    public static Map<String, Long> parseLongJsonMap(JSONObject object)
            throws JSONException {
        Map<String, Long> map = new ArrayMap<String, Long>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.getLong(key));
        }
        return map;
    }

    public static Map<String, String> parseStringJsonMap(JSONObject object)
            throws JSONException {
        Map<String, String> map = new ArrayMap<String, String>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.getString(key));
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

    public static <T> Map<String, T> fromBundle(Bundle input, Class<T> claz) {
        input.setClassLoader(claz.getClassLoader());
        Map<String, T> output = new ArrayMap<String, T>();
        for(String key : input.keySet()) {
            output.put(key, (T) input.get(key));
        }
        return output;
    }
}
