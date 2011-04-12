package com.yelp.parcelgen;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class BusinessActivity extends Activity {

	public static final String EXTRA_BUSINESS = "business";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business);
        Business business = getIntent().getParcelableExtra(EXTRA_BUSINESS);
        setTitle(business.getName());
        StringBuilder info = new StringBuilder();
        info.append("Name: " + business.getName() + "\n");
        Location location = business.getLocation();
        info.append("Address: " + 
        		TextUtils.join(", ", location.getDisplayAddress()) + "\n");
        info.append("City: " + location.getCity() + "\n");
        info.append("State: " + location.getStateCode() + "\n");
        info.append("Url: " + business.getUri().toString() + "\n");
        info.append("Image Url: " + business.getImageUrl() + "\n");
        info.append("\n");
        info.append("Snippet Text: " + business.getSnippetText() + "\n");
        ((TextView)findViewById(R.id.textView)).setText(info.toString());
    }
}
