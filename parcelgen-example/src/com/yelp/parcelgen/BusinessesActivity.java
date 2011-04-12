package com.yelp.parcelgen;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BusinessesActivity extends ListActivity {
	
	public static final String EXTRA_BUSINESSES = "businesses";
	
	ArrayList<Business> mBusinesses;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Search results");
        mBusinesses = getIntent().getParcelableArrayListExtra(EXTRA_BUSINESSES);
        ArrayAdapter<Business> adapter = new ArrayAdapter<Business>(this, android.R.layout.simple_list_item_1, mBusinesses);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent = new Intent(this, BusinessActivity.class);
    	intent.putExtra(BusinessActivity.EXTRA_BUSINESS, mBusinesses.get(position));
    	startActivity(intent);
    }
}
