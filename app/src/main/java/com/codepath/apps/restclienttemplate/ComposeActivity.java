package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;

public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity";

    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

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
                    //Video suggests SnackBar; download Twitter
                    // to compare design
                    Toast.makeText(ComposeActivity.this,
                            "Tweet tweeted!",
                            Toast.LENGTH_SHORT).show();
                }

                //Make API call to tweet from Twitter
            }
        });
    }
}