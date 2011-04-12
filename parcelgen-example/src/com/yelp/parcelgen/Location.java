package com.yelp.parcelgen;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import com.yelp.parcelgen.JsonParser.DualCreator;


public class Location extends _Location {

	public static final DualCreator<Location> CREATOR = new DualCreator<Location>() {

		public Location[] newArray(int size) {
			return new Location[size];
		}

		public Location createFromParcel(Parcel source) {
			Location object = new Location();
			object.readFromParcel(source);
			return object;
		}

		@Override
		public Location parse(JSONObject obj) throws JSONException {
			Location newInstance = new Location();
			newInstance.readFromJson(obj);
			return newInstance;
		}
	};

}
