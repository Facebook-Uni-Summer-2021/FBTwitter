package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class DetailTweetActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivityTweet";
    public static final int RESULT_CODE = 55;

    TwitterClient client;

    TextView tvScreenName;
    TextView tvName;
    TextView tvTime;
    TextView tvBody;
    TextView tvLikeCount;
    TextView tvRetweetCount;
    ImageView ivProfileImage;
    ImageView ivMedia;
    ImageView ivRetweet;
    ImageView ivLike;


    //RelativeLayout rlTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);

        final Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        client = TwitterApp.getRestClient(this);

        //Log.i(TAG, tweet.toString());

        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvBody = findViewById(R.id.tvBody);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);
        //rlTweet = findViewById(R.id.rlTweet);
        ivRetweet = findViewById(R.id.ivRetweet);
        ivLike = findViewById(R.id.ivLike);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);

        tvScreenName.setText(tweet.user.screenName);
        tvName.setText("@" + tweet.user.name);
        tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        tvBody.setText(tweet.body);
        tvLikeCount.setText(String.valueOf(tweet.likeCount));
        tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

        //It seems tweets are not updated at any speed at all; I have no idea why it
        // is not correctly saving?
        if (tweet.isFavorited) {
            ivLike.setImageDrawable(getDrawable(android.R.drawable.star_big_on));
        } else {
            ivLike.setImageDrawable(getDrawable(android.R.drawable.star_big_off));
        }

        //I need a drawable to switch to
        if (tweet.isRetweeted) {
            ivRetweet.setBackgroundColor(Color.parseColor("#ACEDAE"));
        } else {
            ivRetweet.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //Use Glide for images with URLs
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);
        List<Media> medias = tweet.entity.medias;
        Glide.with(this).load(medias.get(0).mediaUrl).into(ivMedia);

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Like");
                if (tweet.isFavorited) {
                    client.unlikeTweet(tweet.tweetId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "unlike tweet");
                            //Refresh tweets
                            ivLike.setImageDrawable(getDrawable(android.R.drawable.star_big_off));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onUnlikeFailure: " + response, throwable);
                        }
                    });
                } else {
                    client.likeTweet(tweet.tweetId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "like tweet");
                            //Refresh tweets
                            ivLike.setImageDrawable(getDrawable(android.R.drawable.star_big_on));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onLikeFailure: " + response, throwable);
                        }
                    });
                }
            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClick Retweet");
            }
        });
    }

//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        Log.i(TAG, "onDetach");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i(TAG, "onStop");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i(TAG, "onPause");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        //Intent intent = new Intent();
        setResult(RESULT_CODE);
        finish();
    }
}