package com.coding.yelp.project.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coding.yelp.project.utils.Util;

import android.util.Log;

public class BusinessFactory {
    
    public static String RATING_IMG_URL_LARGE = "rating_img_url_large";
    public static String SNIPPET_TEXT = "snippet_text";
    public static String DISPLAY_PHONE = "display_phone";
    public static String PHONE = "phone";
    public static String STATE_CODE = "state_code";
    public static String COUNTRY_CODE = "country_code";
    public static String POSTAL_CODE = "postal_code";
    public static String CITY = "city";
    public static String REVIEW_COUNT = "review_count";
    public static String IS_CLOSED = "is_closed";
    public static String YELP_ID = "id";
    public static String DISTANCE = "distance";
    public static String IMAGE_URL = "image_url";
    public static String NAME = "name";
    public static String RATING = "rating";
    public static String CATEGORIES = "categories";
    public static String MOBILE_URL = "mobile_url";
    public static String URL = "url";
    public static String STREET_ADDRESS = "address";
    public static String DISPLAY_ADDRESS = "display_address";
    public static String LOCATION = "location";
    

    
    public static List<Business> getBusinessList(JSONObject input){
        List<Business> businessList = new ArrayList<Business>();
        JSONArray businessArray = Util.ga(input, "businesses");
        Log.i("BUSINESS LIST", "length: " + businessArray.length());
        for (int i = 0; i < businessArray.length(); i++){
            Business business = new Business();
            try {
                JSONObject businessObject = businessArray.getJSONObject(i);
                JSONObject locationObject = Util.go(businessObject, LOCATION);
                if (!Util.gs(businessObject, RATING_IMG_URL_LARGE).equals(null)){
                    business.ratingImageUrl = Util.gs(businessObject, RATING_IMG_URL_LARGE);
                }
                if (!Util.gs(businessObject, SNIPPET_TEXT).equals(null)){
                    business.snippetText = Util.gs(businessObject, SNIPPET_TEXT);
                }
                if (!Util.gs(businessObject, DISPLAY_PHONE).equals(null)){
                    business.displayPhone = Util.gs(businessObject, DISPLAY_PHONE);
                }
                if (!Util.gs(businessObject, PHONE).equals(null)){
                    business.phone = Util.gs(businessObject, PHONE);
                }
                if (!Util.gs(businessObject, CITY).equals(null)){
                    business.city = Util.gs(businessObject, CITY);
                }
                if (!Util.gs(businessObject, STATE_CODE).equals(null)){
                    business.state = Util.gs(businessObject, STATE_CODE);
                }
                if (!Util.gs(businessObject, COUNTRY_CODE).equals(null)){
                    business.country = Util.gs(businessObject, COUNTRY_CODE);
                }
                if (!Util.gs(businessObject, POSTAL_CODE).equals(null)){
                    business.zip = Util.gs(businessObject, POSTAL_CODE);
                }
                if (!Util.gs(businessObject, REVIEW_COUNT).equals(null)){
                    business.reviewCount = Util.gi(businessObject, REVIEW_COUNT);
                }
                if (!Util.gs(businessObject, IS_CLOSED).equals(null)){
                    business.isClosed = Util.gb(businessObject, IS_CLOSED);
                }
                if (!Util.gs(businessObject, YELP_ID).equals(null)){
                    business.yelpId = Util.gs(businessObject, YELP_ID);
                }
                if (!Util.gs(businessObject, DISTANCE).equals(null)){
                    Double distanceDouble= (Util.gd(businessObject, DISTANCE) / 1609.344);
                    business.distance = distanceDouble;
                }
                if (!Util.gs(businessObject, IMAGE_URL).equals(null)){
                    business.imageUrl = Util.gs(businessObject, IMAGE_URL);
                }
                if (!Util.gs(businessObject, NAME).equals(null)){
                    business.name = Util.gs(businessObject, NAME);
                }
                if (!Util.gs(businessObject, RATING).equals(null)){
                    business.rating = Util.gd(businessObject, RATING);
                }
                if (!Util.gs(businessObject, MOBILE_URL).equals(null)){
                    business.mobileUrl = Util.gs(businessObject, MOBILE_URL);
                }
                if (!Util.gs(businessObject, URL).equals(null)){
                    business.url = Util.gs(businessObject, URL);
                }
                if (Util.ga(businessObject, CATEGORIES).length() > 0){
                    business.categories = Util.getStringArrayList(Util.ga(businessObject, CATEGORIES));
                }
                if (Util.ga(locationObject, DISPLAY_ADDRESS).length() > 0){
                    business.streetAddress = Util.getStringFromArray(Util.ga(locationObject, DISPLAY_ADDRESS));
                } else {
                    business.streetAddress = "no street address";
                }
                businessList.add(business);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return businessList;
    }
}
