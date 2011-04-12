package com.yelp.parcelgen;

import com.yelp.example.Yelp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends Activity {
	
	public static final int DIALOG_PROGRESS = 42;

	private Yelp mYelp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mYelp = new Yelp(getString(R.string.consumer_key), getString(R.string.consumer_secret),
        		getString(R.string.api_token), getString(R.string.api_token_secret));
    }
    
    public void search(View v) {
    	String terms = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
    	String location = ((EditText)findViewById(R.id.location)).getText().toString();
    	showDialog(DIALOG_PROGRESS);
    	new AsyncTask<String, Void, ArrayList<Business>>() {

			@Override
			protected ArrayList<Business> doInBackground(String... params) {
				String result = mYelp.search(params[0], params[1]);
				try {
					JSONObject response = new JSONObject(result);
					if (response.has("businesses")) {
						return JsonUtil.parseJsonList(
							response.getJSONArray("businesses"), Business.CREATOR);
					}
				} catch (JSONException e) {
					return null;
				}
				return null;
			}
		
			@Override
			protected void onPostExecute(ArrayList<Business> businesses) {
				onSuccess(businesses);
			}
    		
    	}.execute(terms, location);
    }
    
    public void onSuccess(ArrayList<Business> businesses) {
    	// Launch BusinessesActivity with an intent that includes the received businesses
		dismissDialog(DIALOG_PROGRESS);
		if (businesses != null) {
			Intent intent = new Intent(SearchActivity.this, BusinessesActivity.class);
			intent.putParcelableArrayListExtra(BusinessesActivity.EXTRA_BUSINESSES, businesses);
			startActivity(intent);
		} else {
			Toast.makeText(this, "An error occured during search", Toast.LENGTH_LONG).show();
		}
    }
    
    @Override
    public Dialog onCreateDialog(int id) {
    	if (id == DIALOG_PROGRESS) {
    		ProgressDialog dialog = new ProgressDialog(this);
    		dialog.setMessage("Loading...");
    		return dialog;
    	} else {
    		return null;
    	}
    }
}