package com.yelp.parcelgen;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A standard class for json-representable objects to extend as
 * a static member for a standardized JSON parsing interface, much
 * like Parcelable's CREATOR member.
 * @author pretz
 *
 * @param <E> The class that is to be created from its JSON representation.
 */
public abstract class JsonParser<E> {

	public static final int OBJECT_TYPE = 1;
	public static final int ARRAY_TYPE = 2;

	/**
	 * Usually objects use JSONObject (dict) as their JSON representation.
	 * Sometimes, however, they are represented as arrays. In this case,
	 * override this method to return ARRAY_TYPE, and parseJsonList will
	 * call parse(JSONArray) instead of parse(JSONObject) for your subclass.
	 *
	 * @return either OBJECT_TYPE or ARRAY_TYPE. Default implementation
	 *         returns OBJECT_TYPE.
	 */
	public int getElementType() {
		return OBJECT_TYPE;
	}

	public E parse(JSONObject object) throws JSONException {
		if (getElementType() != OBJECT_TYPE) {
			throw new UnsupportedOperationException("This JsonParser requires you to call parse(JSONArray): " + this.getClass().getCanonicalName());
		}
		throw new UnsupportedOperationException("Must implement parse(JSONObject): " + this.getClass().getCanonicalName());
	}

	public E parse(JSONArray object) throws JSONException {
		if (getElementType() != ARRAY_TYPE) {
			throw new UnsupportedOperationException("This JsonParser requires you to call parse(JSONObject): " + this.getClass().getCanonicalName());
		}
		throw new UnsupportedOperationException("Must implement parse(JSONArray): " + this.getClass().getCanonicalName());
	}

	public E make(JSONArray array, int index) {
		try {
			if (getElementType() == OBJECT_TYPE) {
				return parse(array.getJSONObject(index));
			} else {
				return parse(array.getJSONArray(index));
			}
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Helper abstract class for allowing the parcelable CREATOR and json CREATOR to be
	 * one object.
	 * @author pretz
	 *
	 * @param <E>
	 */
	public static abstract class DualCreator<E> extends JsonParser<E> implements Parcelable.Creator<E> {

	}
}