package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

/**
 * Creates a DialogFragment/Modal to represent composing a tweet based
 * on where the tweet is being composed.
 */
public class ComposeTweetFragment extends DialogFragment {
    private static final String TAG = "ComposeTweetFragment";
    private static final int MAX_TWEET_LENGTH = 280;
    private ComposeListener listener;

    private final String COMPOSE = "ComposeTweet";
    private final String DETAIL_REPLY = "DetailReply";
    private final String TIMELINE_REPLY = "TimelineReply";

    TwitterClient client;
    static Context context;
    static String activity;
    static Tweet tweet;

    private EditText etComposeModal;
    private Button btnModalTweet;

    //Create interface in Fragment to pass data
    public interface ComposeListener {
        //???
        public void onDialogReady (String title);
        //Load data
        public void onTweetLoaded (Tweet tweet);
    }

    //Set the listener
    public void setComposeListener (ComposeListener listener) {
        this.listener = listener;
    }

//    //Required by interface?
//    public ComposeTweetFragment () {
//        this.listener = null;
//    }

    //Required for DialogFragment
    public ComposeTweetFragment () {
    }

    /**
     * Used in place of constructor; defines/initializes the
     * compose fragment/modal.
     * @param activity Title of the activity the modal was created in.
     * @param context Context of activity.
     * @param tweet If a reply is made, which tweet to reply to. Can be null if not reply.
     * @return New Fragment
     */
    public static ComposeTweetFragment newInstance (String activity, Context context, Tweet tweet) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", activity);
        ComposeTweetFragment.activity = activity;
        frag.setArguments(args);
        ComposeTweetFragment.context = context;
        ComposeTweetFragment.tweet = tweet;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Use binding here?
        etComposeModal = view.findViewById(R.id.etComposeModal);
        btnModalTweet = view.findViewById(R.id.btnModalTweet);
        client = TwitterApp.getRestClient(context);

//        etCompose = findViewById(R.id.etCompose);
//        btnTweet = findViewById(R.id.btnTweet);

        //On button press, compose tweet
        btnModalTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do error handling, as tweet needs to be greater
                // than 1 character and less than 280 characters
                String tweetContent = etComposeModal.getText().toString();
                if (tweetContent.isEmpty()) {
                    //If in anonymous class, ensure you do
                    // "[Class].this" instead of "this"
                    Toast.makeText(context,
                            "Please include at least one " +
                                    "character to tweet!",
                            Toast.LENGTH_LONG).show();
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    int extraChars = MAX_TWEET_LENGTH - tweetContent.length();
                    Toast.makeText(context,
                            "This tweet is too long, please " +
                                    "remove " + extraChars + " characters " +
                                    "from your tweet!",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Make API call to tweet from Twitter
                    //Video suggests SnackBar; download Twitter
                    // to compare design
                    Toast.makeText(context,
                            "Tweet tweeted!",
                            Toast.LENGTH_SHORT).show();

                    if (activity.compareTo(COMPOSE) == 0) {
                        client.publishTweet(tweetContent,
                                new JsonHttpResponseHandler() {
                                    //We expect to post a tweet with the
                                    // appropriate model
                                    @Override
                                    public void onSuccess(int statusCode,
                                                          Headers headers,
                                                          JSON json) {
                                        Log.i(TAG, "onSuccess");
                                        try {
                                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                                            Log.i(TAG, "Published tweet: " +
                                                    tweet);
                                        /* Use
                                        //Create new intent to pass information
                                        // from child (current) intent to parent
                                        // (original) intent
                                        Intent intent = new Intent();
                                        intent.putExtra("tweet",
                                                Parcels.wrap(tweet));
                                        //Set OK result with specific intent
                                        context.setResult(RESULT_OK, intent);
                                        finish();
                                         */
                                            //Since we cannot pass intents in a DialogFragment,
                                            // create an interface to pass data to activity
                                            // where modal was created
                                            if (listener != null) {
                                                listener.onTweetLoaded(tweet);
                                            }
                                            dismiss();

                                        } catch (JSONException e) {
                                            Log.e(TAG, "onFailure: ", e);
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode,
                                                          Headers headers,
                                                          String response,
                                                          Throwable throwable) {
                                        Log.e(TAG, "onFailure: ", throwable);
                                    }
                                });
                    } else if (activity.compareTo(DETAIL_REPLY) == 0 || activity.compareTo(TIMELINE_REPLY) == 0) {
                        String replyTweet = "@" + tweet.user.screenName + " " + tweetContent;
                        client.replyTweet(replyTweet, tweet.tweetId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                //Make sure to place listeners!!!
                                Log.i(TAG, "OnSuccessful reply tweet");
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Log.i(TAG, "Published tweet: " + tweet);
                                    if (listener != null) {
                                        listener.onTweetLoaded(tweet);
                                    }
                                    dismiss();

                                } catch (JSONException e) {
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    }


                }
            }
        });

    }
}
