package com.yelp.parcelgen;

import android.os.Parcel;
import android.os.Parcelable;
import com.yelp.parcelgen.JsonUtil;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/** Automatically generated Parcelable implementation for _Location.
 *    DO NOT MODIFY THIS FILE MANUALLY! IT WILL BE OVERWRITTEN THE NEXT TIME
 *    _Location's PARCELABLE DESCRIPTION IS CHANGED.
 */
/* package */ abstract class _Location implements Parcelable {

	protected List<String> mAddress;
	protected List<String> mDisplayAddress;
	protected List<String> mNeighborhoods;
	protected String mCity;
	protected String mStateCode;
	protected String mPostalCode;
	protected String mCountryCode;
	protected String mCrossStreets;
	protected double mLatitude;
	protected double mLongitude;
	protected double mGeoAccuracy;

	protected _Location(List<String> address, List<String> displayAddress, List<String> neighborhoods, String city, String stateCode, String postalCode, String countryCode, String crossStreets, double latitude, double longitude, double geoAccuracy) {
		this();
		mAddress = address;
		mDisplayAddress = displayAddress;
		mNeighborhoods = neighborhoods;
		mCity = city;
		mStateCode = stateCode;
		mPostalCode = postalCode;
		mCountryCode = countryCode;
		mCrossStreets = crossStreets;
		mLatitude = latitude;
		mLongitude = longitude;
		mGeoAccuracy = geoAccuracy;
	}

	protected _Location() {
		super();
	}

	public List<String> getAddress() {
		 return mAddress;
	}
	public List<String> getDisplayAddress() {
		 return mDisplayAddress;
	}
	public List<String> getNeighborhoods() {
		 return mNeighborhoods;
	}
	public String getCity() {
		 return mCity;
	}
	public String getStateCode() {
		 return mStateCode;
	}
	public String getPostalCode() {
		 return mPostalCode;
	}
	public String getCountryCode() {
		 return mCountryCode;
	}
	public String getCrossStreets() {
		 return mCrossStreets;
	}
	public double getLatitude() {
		 return mLatitude;
	}
	public double getLongitude() {
		 return mLongitude;
	}
	public double getGeoAccuracy() {
		 return mGeoAccuracy;
	}


	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeStringList(mAddress);
		parcel.writeStringList(mDisplayAddress);
		parcel.writeStringList(mNeighborhoods);
		parcel.writeString(mCity);
		parcel.writeString(mStateCode);
		parcel.writeString(mPostalCode);
		parcel.writeString(mCountryCode);
		parcel.writeString(mCrossStreets);
		parcel.writeDouble(mLatitude);
		parcel.writeDouble(mLongitude);
		parcel.writeDouble(mGeoAccuracy);
	}

	public void readFromParcel(Parcel source) {
		mAddress = source.createStringArrayList();
		mDisplayAddress = source.createStringArrayList();
		mNeighborhoods = source.createStringArrayList();
		mCity = source.readString();
		mStateCode = source.readString();
		mPostalCode = source.readString();
		mCountryCode = source.readString();
		mCrossStreets = source.readString();
		mLatitude = source.readDouble();
		mLongitude = source.readDouble();
		mGeoAccuracy = source.readDouble();
	}

	public void readFromJson(JSONObject json) throws JSONException {
		if (!json.isNull("address")) {
			mAddress = JsonUtil.getStringList(json.optJSONArray("address"));
		} else {
			mAddress = java.util.Collections.emptyList();
		}
		if (!json.isNull("display_address")) {
			mDisplayAddress = JsonUtil.getStringList(json.optJSONArray("display_address"));
		} else {
			mDisplayAddress = java.util.Collections.emptyList();
		}
		if (!json.isNull("neighborhoods")) {
			mNeighborhoods = JsonUtil.getStringList(json.optJSONArray("neighborhoods"));
		} else {
			mNeighborhoods = java.util.Collections.emptyList();
		}
		if (!json.isNull("city")) {
			mCity = json.optString("city");
		}
		if (!json.isNull("state_code")) {
			mStateCode = json.optString("state_code");
		}
		if (!json.isNull("postal_code")) {
			mPostalCode = json.optString("postal_code");
		}
		if (!json.isNull("country_code")) {
			mCountryCode = json.optString("country_code");
		}
		if (!json.isNull("cross_streets")) {
			mCrossStreets = json.optString("cross_streets");
		}
		if (!json.isNull("geo_accuracy")) {
			mGeoAccuracy = json.optDouble("geo_accuracy");
		} else {
			mGeoAccuracy = -1;
		}
	}

}
