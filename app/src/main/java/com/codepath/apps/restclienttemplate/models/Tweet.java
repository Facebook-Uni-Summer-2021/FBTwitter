package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.LinkedList;
import java.util.List;

//Required by Parceler
@Parcel
public class Tweet {
    private static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public User user;

    //Required by Parceler
    public Tweet () {}

    /**
     * Create a tweet as per the json fields/attributes (refer
     * to the JSON in the API docs for the attribute names).
     * @param jsonObject A JSON Object representing a tweet.
     * @return A tweet model.
     */
    public static Tweet fromJson(JSONObject jsonObject)
            throws JSONException {
        Tweet tweet = new Tweet();
        //The body, or text, of the tweet
        tweet.body = jsonObject.getString("text");
        //When the tweet was created
        tweet.createdAt = jsonObject.getString("created_at");
        //Create a user from the JSON Object stored in the API's
        // JSON (as we do in Tweet, we need to convert the object
        // from the JSON into a model)
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }

    /**
     * Creates a list of tweet objects from a JSON array (call
     * this when obtaining JSON array directly from API/source).
     * @param jsonArray A JSON array of tweets.
     * @return A list of tweets converted to its model.
     */
    public static List<Tweet> fromJsonArray (JSONArray jsonArray)
            throws JSONException {
        List<Tweet> tweets = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }
}
