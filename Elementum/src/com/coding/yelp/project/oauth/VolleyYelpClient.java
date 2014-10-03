package com.coding.yelp.project.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;

import org.json.JSONObject;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.coding.yelp.project.controllers.VolleyController;

public class VolleyYelpClient {

    /**
     * Setup the Yelp API OAuth credentials.
     * 
     * OAuth credentials are available from the developer site, under Manage API
     * access (version 2 API).
     * 
     */
    public VolleyYelpClient() {
        super();
    }

    /**
     * Search with term and location.
     * 
     * @param term
     *            Search term
     * @param latitude
     *            Latitude
     * @param longitude
     *            Longitude
     * @return JSON string response
     */
    public static void search(String term, int offset, String latitude,
            String longitude, Listener<JSONObject> responseSuccessListener,
            ErrorListener responseErrorListener) {

        String requestUrl = String.format(
                "http://api.yelp.com/v2/search?term=%s&offset=%d&ll=%s,%s",
                Uri.encode(term), offset, latitude + "", longitude + "");
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                YelpCredentials.CONSUMER_KEY, YelpCredentials.CONSUMER_SECRET);
        consumer.setMessageSigner(new HmacSha1MessageSigner());
        consumer.setTokenWithSecret(YelpCredentials.TOKEN,
                YelpCredentials.TOKEN_SECRET);
        consumer.setSendEmptyTokens(true);
        consumer.setSigningStrategy(new QueryStringSigningStrategy());
        String signedQuery = "";
        try {
            signedQuery = consumer.sign(requestUrl);
            Log.i("REQUEST", "request: " + signedQuery);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                signedQuery, null, responseSuccessListener,
                responseErrorListener);

        VolleyController.getInstance().addToRequestQueue(request);
    }
}