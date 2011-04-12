package com.yelp.parcelgen;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

/** Automatically generated Parcelable implementation for _Business.
 *  DO NOT MODIFY THIS FILE MANUALLY! IT WILL BE OVERWRITTEN THE NEXT TIME
 *  _Business's PARCELABLE DESCRIPTION IS CHANGED.
 */
/* package */ abstract class _Business implements Parcelable {

	protected Location mLocation;
	protected String mId;
	protected String mName;
	protected String mImageUrl;
	protected String mPhone;
	protected String mDisplayPhone;
	protected String mRatingImageUrl;
	protected String mRatingImageUrlSmall;
	protected String mSnippetText;
	protected String mSnippetImageUrl;
	protected Uri mUri;
	protected Uri mMobileUri;
	protected double mDistance;
	protected int mReviewCount;

	protected _Business(Location location, String id, String name, String imageUrl, String phone, String displayPhone, String ratingImageUrl, String ratingImageUrlSmall, String snippetText, String snippetImageUrl, Uri uri, Uri mobileUri, double distance, int reviewCount) {
		this();
		mLocation = location;
		mId = id;
		mName = name;
		mImageUrl = imageUrl;
		mPhone = phone;
		mDisplayPhone = displayPhone;
		mRatingImageUrl = ratingImageUrl;
		mRatingImageUrlSmall = ratingImageUrlSmall;
		mSnippetText = snippetText;
		mSnippetImageUrl = snippetImageUrl;
		mUri = uri;
		mMobileUri = mobileUri;
		mDistance = distance;
		mReviewCount = reviewCount;
	}

	protected _Business() {
		super();
	}

	public Location getLocation() {
		 return mLocation;
	}
	public String getId() {
		 return mId;
	}
	public String getName() {
		 return mName;
	}
	public String getImageUrl() {
		 return mImageUrl;
	}
	public String getPhone() {
		 return mPhone;
	}
	public String getDisplayPhone() {
		 return mDisplayPhone;
	}
	public String getRatingImageUrl() {
		 return mRatingImageUrl;
	}
	public String getRatingImageUrlSmall() {
		 return mRatingImageUrlSmall;
	}
	public String getSnippetText() {
		 return mSnippetText;
	}
	public String getSnippetImageUrl() {
		 return mSnippetImageUrl;
	}
	public Uri getUri() {
		 return mUri;
	}
	public Uri getMobileUri() {
		 return mMobileUri;
	}
	public double getDistance() {
		 return mDistance;
	}
	public int getReviewCount() {
		 return mReviewCount;
	}


	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeParcelable(mLocation, 0);
		parcel.writeString(mId);
		parcel.writeString(mName);
		parcel.writeString(mImageUrl);
		parcel.writeString(mPhone);
		parcel.writeString(mDisplayPhone);
		parcel.writeString(mRatingImageUrl);
		parcel.writeString(mRatingImageUrlSmall);
		parcel.writeString(mSnippetText);
		parcel.writeString(mSnippetImageUrl);
		parcel.writeParcelable(mUri, 0);
		parcel.writeParcelable(mMobileUri, 0);
		parcel.writeDouble(mDistance);
		parcel.writeInt(mReviewCount);
	}

	public void readFromParcel(Parcel source) {
		mLocation = source.readParcelable(Location.class.getClassLoader());
		mId = source.readString();
		mName = source.readString();
		mImageUrl = source.readString();
		mPhone = source.readString();
		mDisplayPhone = source.readString();
		mRatingImageUrl = source.readString();
		mRatingImageUrlSmall = source.readString();
		mSnippetText = source.readString();
		mSnippetImageUrl = source.readString();
		mUri = source.readParcelable(Uri.class.getClassLoader());
		mMobileUri = source.readParcelable(Uri.class.getClassLoader());
		mDistance = source.readDouble();
		mReviewCount = source.readInt();
	}

	public void readFromJson(JSONObject json) throws JSONException {
		if (!json.isNull("location")) {
			mLocation = Location.CREATOR.parse(json.getJSONObject("location"));
		}
		if (!json.isNull("id")) {
			mId = json.optString("id");
		}
		if (!json.isNull("name")) {
			mName = json.optString("name");
		}
		if (!json.isNull("image_url")) {
			mImageUrl = json.optString("image_url");
		}
		if (!json.isNull("phone")) {
			mPhone = json.optString("phone");
		}
		if (!json.isNull("display_phone")) {
			mDisplayPhone = json.optString("display_phone");
		}
		if (!json.isNull("rating_img_url")) {
			mRatingImageUrl = json.optString("rating_img_url");
		}
		if (!json.isNull("rating_img_url_small")) {
			mRatingImageUrlSmall = json.optString("rating_img_url_small");
		}
		if (!json.isNull("snippet_text")) {
			mSnippetText = json.optString("snippet_text");
		}
		if (!json.isNull("snippet_image_url")) {
			mSnippetImageUrl = json.optString("snippet_image_url");
		}
		if (!json.isNull("url")) {
			mUri = Uri.parse(json.getString("url"));
		}
		if (!json.isNull("mobile_url")) {
			mMobileUri = Uri.parse(json.getString("mobile_url"));
		}
		mDistance = json.optDouble("distance");
		mReviewCount = json.optInt("review_count");
	}

}
