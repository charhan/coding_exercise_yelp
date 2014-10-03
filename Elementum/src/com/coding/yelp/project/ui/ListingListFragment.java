/**
 * 
 */
package com.coding.yelp.project.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.coding.yelp.project.R;
import com.coding.yelp.project.adapters.YelpListAdapater;
import com.coding.yelp.project.utils.ConnectivityMonitor;
import com.coding.yelp.project.utils.LocationHelper;
import com.coding.yelp.project.utils.Util;
import com.coding.yelp.project.models.Business;
import com.coding.yelp.project.models.BusinessFactory;
import com.coding.yelp.project.models.Query;
import com.coding.yelp.project.oauth.VolleyYelpClient;

public class ListingListFragment extends ListFragment {

    private Activity mActivity;
    private FragmentManager fm;
    private List<Business> mListingList;
    private YelpListAdapater mListAdapter;
//    private ListingListArrayAdapaterNoHolder mListAdapter;
    private Location mUserLocation;
    private TextView mListfilterDistance;
    private TextView mListfilterRating;
    private String mQuery;

    private EndlessScrollListener mEndlessListener;

    private ListView mListView;

    private ConnectivityMonitor mConnectivityManager;

    private LocationHelper mLocationHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater
                .inflate(R.layout.fragment_list_view, container, false);

        mActivity = getActivity();
        mLocationHelper = new LocationHelper(mActivity);
        mUserLocation = Util.getCurrentLocation(mActivity);
        if (mUserLocation == null) {
            mUserLocation = Util.getDefaultLocation();
        }
        mConnectivityManager = new ConnectivityMonitor(mActivity);
        setHasOptionsMenu(true);

        mListfilterDistance = (TextView) v
                .findViewById(R.id.tv_list_filters_distance);
        mListfilterRating = (TextView) v
                .findViewById(R.id.tv_list_filters_rating);
        
        mListAdapter = new YelpListAdapater(mActivity);
        
        // Handler for distance filter
        mListfilterDistance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListingList == null){
                    Toast.makeText(mActivity, "No Results to Filter", Toast.LENGTH_SHORT).show();
                } else {
                    sortByDistance(mListingList);
                    refreshList(mListingList);
                }
            }
        });

        // Handler for rating filter
        mListfilterRating.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListingList  == null){
                    Toast.makeText(mActivity, "No Results to Filter", Toast.LENGTH_SHORT).show();
                } else {
                    sortByRating(mListingList);
                    refreshList(mListingList);
                }
            }
        });

        // get Fragment Manager
        fm = getFragmentManager();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mEndlessListener = new EndlessScrollListener();
        mListView = getListView();
        mListView.setOnScrollListener(mEndlessListener);
        mListView.setAdapter(mListAdapter);
        
        if (!mConnectivityManager.isNetworkAvailable()){
            loadDataFromStorage();
        }
        Bundle extras = getArguments();
        if (extras != null){
            String queryString = extras.getString("query");
            search(queryString);
        }
        
//        extras = getArguments();
//        String queryString = extras.getString("query");
//        search(queryString);

    }

    private void loadDataFromStorage() {
        mListingList = Business.getAllLastQuery();
        mListAdapter.addAll(mListingList);
        mListAdapter.notifyDataSetChanged();
        getListView().invalidateViews();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void sortListDistance() {
        sortByDistance(mListingList);

    }

    public void search(String query) {
        if (mListAdapter != null){
            mListAdapter.clear();
            mListView.invalidate();
        }
        mEndlessListener.resetListener();

        mQuery = query;

        // set title
        mActivity.setTitle("Searching - " + mQuery);
        
        Query.storeQuery(mQuery);
        
        loadMore();
    }

    public void loadMore() {
        mActivity.setProgressBarIndeterminateVisibility(true);
        int count = 0;
        if (mListAdapter != null){
            count = mListAdapter.getCount();
        }
//        LatLng userLocation = mLocationHelper.getLatLng();
        Location userLocation = mLocationHelper.getLocation();
        if (null == userLocation) {
            userLocation = Util.getDefaultLocation();
        }

        VolleyYelpClient.search(mQuery, count,
                userLocation.getLatitude() + "", userLocation.getLongitude()
                        + "", yelpResponseSuccessListener(),
                yelpErrorListener());
    }


    public void sortByDistance(List<Business> listingList) {
        // Comparator by distance
        Comparator<Business> distanceSort = new Comparator<Business>() {
            public int compare(Business listing1, Business listing2) {
                Double distance1 = listing1.distance;
                Double distance2 = listing2.distance;

                return distance1.compareTo(distance2);
            }
        };
        Collections.sort(listingList, distanceSort);
    }

    public void sortByRating(List<Business> list) {
        // Comparator by rating
        Comparator<Business> ratingSort = new Comparator<Business>() {
            public int compare(Business obj1, Business obj2) {
                return obj1.rating.compareTo(obj2.rating);
            }
        };

        Collections.sort(list, ratingSort);
        Collections.reverse(list);
    }

    public void sortByReviews(List<Business> list) {
        // Comparator by review count
        Comparator<Business> revewSort = new Comparator<Business>() {
            public int compare(Business obj1, Business obj2) {
                return obj1.reviewCount.compareTo(obj2.reviewCount);
            }
        };

        Collections.sort(list, revewSort);
        Collections.reverse(list);
    }

    private void refreshList(List<Business> list) {
        mListAdapter.clear();
        mListAdapter.addAll(list);
//        mListAdapter = new ListingListArrayAdapaterNoHolder(getActivity(), list);
        mListAdapter.notifyDataSetChanged();
        getListView().invalidateViews();
    }

    public class EndlessScrollListener implements OnScrollListener {
        private int visibleThreshold = 10;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {

        }

        public void resetListener() {
            previousTotal = 0;
            loading = true;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            // if we are not loading and the (total - visible in view) <=
            // (current top item position + the threshold value)
            if (!loading
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loadMore();
                loading = true;
            }
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }

    private Response.Listener<JSONObject> yelpResponseSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mListingList = BusinessFactory
                        .getBusinessList(response);
                Business.storeAll(mListingList);
                mListAdapter.addAll(mListingList);
                mListAdapter.notifyDataSetChanged();
                getListView().invalidateViews();
                mActivity.setProgressBarIndeterminateVisibility(false);
            }
        };
    }

    private Response.ErrorListener yelpErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorDialog(error);
            }
        };
    }
    
    private void showErrorDialog(Exception e) {
        e.printStackTrace();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(mActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                //search(queryString);
                search(queryString);
                //collapse  search 
                searchItem.collapseActionView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String currentText) {
                return false;
            }
        });
        
        super.onCreateOptionsMenu(menu, inflater);
    }

}
