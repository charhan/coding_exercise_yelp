package com.coding.yelp.project.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.coding.yelp.project.R;
import com.coding.yelp.project.ui.ListingListFragment;

public class YelpSearchActivity extends FragmentActivity {
    private FragmentManager fm;
    ListingListFragment mBusinessListFrag;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
        setContentView(R.layout.search_parent_layout);
        
        fm = getSupportFragmentManager();
        inflateSearch();
    }    
    
    private void inflateSearch() {            
            mBusinessListFrag = new ListingListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, mBusinessListFrag);
            ft.commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        default:
            break;
        }
        return true;
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
     @Override
     public void onSaveInstanceState(Bundle savedInstanceState) {
           // Always call the superclass so it can save the view hierarchy state
           super.onSaveInstanceState(savedInstanceState);
      }
     
     public void onRestoreInstanceState(Bundle savedInstanceState) {
            // Always call the superclass so it can restore the view hierarchy
            super.onRestoreInstanceState(savedInstanceState);
    }
}