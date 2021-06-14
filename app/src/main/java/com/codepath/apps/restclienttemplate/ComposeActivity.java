package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity";

    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComposeBinding binding = ActivityComposeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

//        etCompose = findViewById(R.id.etCompose);
//        btnTweet = findViewById(R.id.btnTweet);

        //On button press, compose tweet
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do error handling, as tweet needs to be greater
                // than 1 character and less than 280 characters
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    //If in anonymous class, ensure you do
                    // "[Class].this" instead of "this"
                    Toast.makeText(ComposeActivity.this,
                            "Please include at least one " +
                                    "character to tweet!",
                            Toast.LENGTH_LONG).show();
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    int extraChars = MAX_TWEET_LENGTH - tweetContent.length();
                    Toast.makeText(ComposeActivity.this,
                            "This tweet is too long, please " +
                                    "remove " + extraChars + " characters " +
                                    "from your tweet!",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Make API call to tweet from Twitter
                    //Video suggests SnackBar; download Twitter
                    // to compare design
                    Toast.makeText(ComposeActivity.this,
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

                                //Create new intent to pass information
                                // from child (current) intent to parent
                                // (original) intent
                                Intent intent = new Intent();
                                intent.putExtra("tweet",
                                        Parcels.wrap(tweet));
                                //Set OK result with specific intent
                                setResult(RESULT_OK, intent);
                                finish();
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