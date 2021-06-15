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

public class ComposeTweetFragment extends DialogFragment implements OnComposeActionListener {
    private static final String TAG = "ComposeTweetFragment";
    private static final int MAX_TWEET_LENGTH = 280;
    TwitterClient client;
    static Context context;
    Tweet tweet;

    private EditText etComposeModal;
    private Button btnModalTweet;

    //Interface for transferring data to activity
    public interface ComposeTweetActionListener {
        void onFinishedCompose(Tweet tweet);
    }

    @Override
    public boolean onComposeAction() {
        ComposeTweetActionListener listener = (ComposeTweetActionListener) getActivity();
        //listener.onFinishedCompose();
        return true;
    }

    //Required for DialogFragment
    public ComposeTweetFragment () {
    }

    //Use in place of constructor
    public static ComposeTweetFragment newInstance (String activity, Context context) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", activity);
        frag.setArguments(args);
        ComposeTweetFragment.context = context;
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
                }
            }
        });

    }
}
