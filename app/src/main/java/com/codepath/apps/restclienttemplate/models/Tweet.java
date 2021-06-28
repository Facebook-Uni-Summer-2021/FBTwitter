package com.codepath.apps.restclienttemplate.models;

import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

/**
 * Represents a tweet!!!!!!!!!!!!!!!!!
 */
//Required by Parceler
@Parcel
@androidx.room.Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    private static final String TAG = "Tweet";

    @ColumnInfo
    @PrimaryKey
    public long tweetId;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long likeCount;

    @ColumnInfo
    public long retweetCount;

    @ColumnInfo
    public boolean isRetweeted;

    @ColumnInfo
    public boolean isFavorited;

    @ColumnInfo
    public long userId;

    //Perhaps create a variable for the single media url?

    //Remove @Ignore later
    @Ignore
    public boolean hasRetweet;

    @Ignore
    public User user;

    @Ignore
    public Tweet retweet;

//    @Ignore
//    public Entity entity;

    @ColumnInfo
    public String mediaUrl;

    //Required by Parceler
    public Tweet() {
    }

    /**
     * Create a tweet as per the json fields/attributes (refer
     * to the JSON in the API docs for the attribute names).
     *
     * @param jsonObject A JSON Object representing a tweet.
     * @return A tweet model.
     */
    public static Tweet fromJson(JSONObject jsonObject)
            throws JSONException {
        Tweet tweet = new Tweet();
        // jsonObject.getJSONObject("retweeted_status").getString("id_str");
        //To handle an error where the timeline would not update correctly for retweeted
        // tweets because the IDs were different, we checked if a tweet was retweeted;
        // if the tweet was, we got the original tweet's information to accurately
        // like/unlike and retweet/unretweet it
        if (jsonObject.has("retweeted_status")) {
            Log.i(TAG, "Retweet found");
            JSONObject retweetedStatus = jsonObject.getJSONObject("retweeted_status");
            tweet.retweet = fromJson(retweetedStatus);
            tweet.hasRetweet = true;
//            tweet.tweetId = retweetedStatus.getLong("id");
//            tweet.likeCount = retweetedStatus.getLong("favorite_count");
//            tweet.isFavorited = retweetedStatus.getBoolean("favorited");
//            tweet.retweetCount = retweetedStatus.getLong("retweet_count");
//            tweet.isRetweeted = retweetedStatus.getBoolean("retweeted");
//            Log.e(TAG, "Retweet: ID: " + tweet.tweetId + ", Favorite: " + tweet.isFavorited);
        } else {
            tweet.tweetId = jsonObject.getLong("id");
            tweet.likeCount = jsonObject.getLong("favorite_count");
            tweet.isFavorited = jsonObject.getBoolean("favorited");
            tweet.retweetCount = jsonObject.getLong("retweet_count");
            tweet.isRetweeted = jsonObject.getBoolean("retweeted");
            tweet.hasRetweet = false;
            //Log.e(TAG, "Normal: ID: " + tweet.tweetId + ", Favorite: " + tweet.isFavorited);
        }

        //The body, or text, of the tweet
        tweet.body = jsonObject.getString("text");
        //When the tweet was created
        tweet.createdAt = jsonObject.getString("created_at");

        //Create a user from the JSON Object stored in the API's
        // JSON (as we do in Tweet, we need to convert the object
        // from the JSON into a model)
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;

        //Obtain the media from a tweet; entities are objects, what's in it
        // are arrays
        //tweet.entity = Entity.fromJson(jsonObject.getJSONObject("entities"));
        //tweet.mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getString(0);
        //tweet.test = jsonObject.getJSONObject("entities");
        //Log.e(TAG, tweet.user.screenName + ": " + jsonObject.getJSONObject("entities").getJSONArray("media"));
        //JSONObject test1 = jsonObject.getJSONObject("entities");
        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            Log.e(TAG, "Object: " + media);
            //JSONObject url = media.getJSONObject(0);
            Log.e(TAG, "URL: " + media.getJSONObject(0).getString("media_url_https"));
            tweet.mediaUrl = media.getJSONObject(0).getString("media_url_https");
        } else
            tweet.mediaUrl = "";
//        Log.i(TAG, "here: " + test1.toString());
//        Log.e(TAG, "REEE" + tweet.entity.medias.get(0).mediaUrl);
        return tweet;
    }

    /**
     * Creates a list of tweet objects from a JSON array (call
     * this when obtaining JSON array directly from API/source).
     *
     * @param jsonArray A JSON array of tweets.
     * @return A list of tweets converted to its model.
     */
    public static List<Tweet> fromJsonArray(JSONArray jsonArray)
            throws JSONException {
        List<Tweet> tweets = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    //Implementation of the ParseRelativeData.java from the following
    //https://gist.github.com/nesquena/f786232f5ef72f6e10a7
    public static String getRelativeTimeAgo(String time) {
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

    /**
     * Updates the like status of a tweet both locally and in the API.
     *
     * @param client The TwitterClient.
     */
    public void unlike(final TwitterClient client) {
        //This is done so the timeline is accurately depicted
        //I included the retweet if available to like the correct tweet
        if (hasRetweet) {
            if (retweet.likeCount > 0)
                retweet.likeCount--;
        }

        if (likeCount > 0)
            likeCount--;

        isFavorited = false;
        client.unlikeTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "unlike tweet");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onUnlikeFailure: " + response, throwable);
            }
        });

        if (hasRetweet) {
            client.unlikeTweet(retweet.tweetId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "unlike retweet");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onUnlikeFailure retweet: " + response, throwable);
                }
            });
        }
    }

    /**
     * Updates the like status of a tweet both locally and in the API.
     *
     * @param client The TwitterClient.
     */
    public void like(final TwitterClient client) {
        //likeCount++;
        if (hasRetweet)
            retweet.likeCount++;

        likeCount++;

        isFavorited = true;
        client.likeTweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "like tweet");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onLikeFailure: " + response, throwable);
            }
        });

        if (hasRetweet) {
            client.likeTweet(retweet.tweetId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "like retweet");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onLikeFailure retweet: " + response, throwable);
                }
            });
        }
    }

    /**
     * Updates the retweet status of a tweet both locally and in the API.
     *
     * @param client The TwitterClient.
     */
    public void unRetweet(final TwitterClient client) {
        if (hasRetweet) {
            if (retweet.retweetCount > 0)
                retweet.retweetCount--;
        }
        if (retweetCount > 0)
            retweetCount--;

        isRetweeted = false;
        client.unretweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "unretweet tweet");
                if (hasRetweet) {
                    client.unretweet(retweet.tweetId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "unretweet retweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onUnretweetFailure retweet: " + response, throwable);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onUnretweetFailure: " + response, throwable);
            }
        });
    }

    /**
     * Updates the retweet status of a tweet both locally and in the API.
     *
     * @param client The TwitterClient.
     */
    public void retweet(final TwitterClient client) {
        if (hasRetweet)
            retweet.retweetCount++;

        retweetCount++;

        isRetweeted = true;
        client.retweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "retweet tweet");
                if (hasRetweet) {
                    client.retweet(retweet.tweetId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "retweet retweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onRetweetFailure retweet: " + response, throwable);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onRetweetFailure: " + response, throwable);
            }
        });
    }
}
