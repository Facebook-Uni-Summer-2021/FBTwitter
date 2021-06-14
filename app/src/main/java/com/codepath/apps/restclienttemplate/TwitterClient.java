package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	//public static final BaseApi REST_API_INSTANCE = FlickrApi.instance(FlickrApi.FlickrPerm.WRITE); // Change this
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	//public static final String REST_URL = "https://api.flickr.com/services"; // Change this, base API URL
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties
	//BuildConfig

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";
	//MAKE SURE TO INCLUDE "intent://" IN DEVELOPER PORTAL

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	// This is where we get information from an API (WE DONT PULL YET!!!
	//  We just set it up for what we want timeline to contain)
	//For rest APIs, performing individual GET, POST, PUT, etc
	// should be done in this class
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		//The API URL used for getting some data list
		//If URL doesnt exist, throws error that states as such
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		//Gets particular data from the apiUrl, depending on what data is
		// available at that link; read the docs to see what is required
		// when pulling information!!!
		RequestParams params = new RequestParams();
		//For Twitter, count requires an int to specify amount to pull
		params.put("count", "25");//Represents count of tweets to pull
		params.put("since_id", 1);//Represents newest tweets; increase for newer-newest
		//params.put("max_id", 0);//Represents oldest tweets; opposite of since_id
		client.get(apiUrl, params, handler);
	}

	//New getHomeTimeline to be called when we reach end of original getTimeline
	public void getHomeTimeline (long maxId,
								 JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

	public void publishTweet (String tweetContent,
							JsonHttpResponseHandler handler) {
		//apiUrl represents endpoint in API
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		//Previously, we got information from endpoint;
		// this time we will post
		//client.get(apiUrl, params, handler);
		client.post(apiUrl, params, "", handler);
	}

	public void likeTweet (long tweetId,
						   JsonHttpResponseHandler handler) {
		//REMEMBER: API URL is the link for RESTful actions
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		params.put("include_entities", true);//Maybe not needed
		//"body" is required, but not explained
		client.post(apiUrl, params, "", handler);
	}

	public void unlikeTweet (long tweetId,
						   JsonHttpResponseHandler handler) {
		//REMEMBER: API URL is the link for RESTful actions
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		params.put("include_entities", true);//Maybe not needed
		//"body" is required, but not explained
		client.post(apiUrl, params, "", handler);
	}

	public void retweet (long tweetId,
							 JsonHttpResponseHandler handler) {
		//REMEMBER: API URL is the link for RESTful actions
		String apiUrl = getApiUrl("statuses/retweet/" + tweetId + ".json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		//"body" is required, but not explained
		client.post(apiUrl, params, "", handler);
	}

	public void unretweet (long tweetId,
							 JsonHttpResponseHandler handler) {
		//REMEMBER: API URL is the link for RESTful actions
		String apiUrl = getApiUrl("statuses/unretweet/" + tweetId + ".json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		//"body" is required, but not explained
		client.post(apiUrl, params, "", handler);
	}


	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
