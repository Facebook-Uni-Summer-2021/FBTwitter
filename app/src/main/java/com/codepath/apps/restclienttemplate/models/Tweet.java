package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

//Required by Parceler
@Parcel
public class Tweet {
    private static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public long tweetId;
    public long likeCount;
    public long retweetCount;
    public boolean isRetweeted;
    public boolean isFavorited;
    public User user;
    public Entity entity;

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
        tweet.tweetId = Long.parseLong(jsonObject.getString("id_str"));
        //The body, or text, of the tweet
        tweet.body = jsonObject.getString("text");
        //When the tweet was created
        tweet.createdAt = jsonObject.getString("created_at");
        //These numbers are very much wrong, but i have no idea why as they are pulled
        // the same way
        tweet.likeCount = jsonObject.getLong("favorite_count");
        tweet.retweetCount = jsonObject.getLong("retweet_count");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");

        //Create a user from the JSON Object stored in the API's
        // JSON (as we do in Tweet, we need to convert the object
        // from the JSON into a model)
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        //Obtain the media from a tweet; entities are objects, what's in it
        // are arrays
        tweet.entity = Entity.fromJson(jsonObject.getJSONObject("entities"));
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

    //Implementation of the ParseRelativeData.java from the following
    //https://gist.github.com/nesquena/f786232f5ef72f6e10a7
    public static String getRelativeTimeAgo (String time) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf =
                new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(time).getTime();
            relativeDate =
                    DateUtils.getRelativeTimeSpanString(dateMillis,
                            System.currentTimeMillis(),
                            DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        return relativeDate;
    }

    public void unlike(TwitterClient client) {
        if (likeCount > 0) {
            likeCount--;
        }

        client.unlikeTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "unlike tweet");
                isFavorited = false;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onUnlikeFailure: " + response, throwable);
            }
        });
    }

    public void like(TwitterClient client) {
        likeCount++;

        client.likeTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "like tweet");
                isFavorited = true;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onLikeFailure: " + response, throwable);
            }
        });
    }
}
