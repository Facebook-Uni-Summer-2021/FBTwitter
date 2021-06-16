package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailTweetBinding;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class DetailTweetActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivityTweet";

    TwitterClient client;

    TextView tvScreenName;//
    TextView tvName;//
    TextView tvTime;//
    TextView tvBody;//
    TextView tvLikeCount;//
    TextView tvRetweetCount;//
    ImageView ivProfileImage;//
    ImageView ivMedia;//
    ImageView ivRetweet;//
    ImageView ivLike;//
    Tweet tweet;
    //tvTimeSepe
    //ImageView ivTimeSeparator;


    //RelativeLayout rlTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailTweetBinding binding = ActivityDetailTweetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //setContentView(R.layout.activity_detail_tweet);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        client = TwitterApp.getRestClient(this);

        //Log.i(TAG, tweet.toString());

//        tvScreenName = findViewById(R.id.tvScreenName);
//        tvName = findViewById(R.id.tvName);
//        tvTime = findViewById(R.id.tvTime);
//        tvBody = findViewById(R.id.tvBody);
//        ivProfileImage = findViewById(R.id.ivProfileImage);
//        ivMedia = findViewById(R.id.ivMedia);
//        //rlTweet = findViewById(R.id.rlTweet);
//        ivRetweet = findViewById(R.id.ivRetweet);
//        ivLike = findViewById(R.id.ivLike);
//        tvLikeCount = findViewById(R.id.tvLikeCount);
//        tvRetweetCount = findViewById(R.id.tvRetweetCount);

        Log.e(TAG, "Current tweet: " + tweet.tweetId);
        Log.e(TAG, "Current tweet like status: " + tweet.isFavorited);
        Log.e(TAG, "Name: " + tweet.user.name);
        Log.e(TAG, "ScrenName: " + tweet.user.screenName);

        binding.tvScreenName.setText(tweet.user.screenName);
        binding.tvName.setText("@" + tweet.user.name);
        binding.tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        binding.tvBody.setText(tweet.body);
        binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
        binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

        //It seems tweets are not updated at any speed at all; I have no idea why it
        // is not correctly saving?
        if (tweet.isFavorited) {
            binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
        } else {
            binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
        }

        //I need a drawable to switch to
        if (tweet.isRetweeted) {
            binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
        } else {
            binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
        }

        //Use Glide for images with URLs
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(binding.ivProfileImage);
        List<Media> medias = tweet.entity.medias;
        Glide.with(this).load(medias.get(0).mediaUrl).into(binding.ivMedia);

        //Trying to like or retweet a retweeted tweet does not update the timeline
        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Like");

                if (tweet.isFavorited) {
                    tweet.unlike(client);
                    binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
                } else {
                    tweet.like(client);
                    binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
                }
                binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
            }
        });

        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClick Retweet");
                if (tweet.isRetweeted) {
                    tweet.unRetweet(client);
                    binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
                } else {
                    tweet.retweet(client);
                    binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
                }
                binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            }
        });

        binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "View Profile");
                Intent intent = new Intent(DetailTweetActivity.this, DetailUserActivity.class);
                intent.putExtra("user", Parcels.wrap(tweet.user));
//                context.startActivity(intent);
//                intent.putExtra("userId", tweet.user.id);
//                intent.putExtra("userImage", tweet.user.profileImageUrl);
                startActivity(intent);
            }
        });

        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REQUIRES "@screenname"
                Log.i(TAG, "Reply to tweet: " + tweet.tweetId);

                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("DetailReply", DetailTweetActivity.this, tweet);
                composeTweetFragment.show(fm, "fragment_compose_tweet");

//                //Compose message
//                String temp = "@" + tweet.user.screenName + " " + "Temp tweet content here!";
//                client.replyTweet(temp, tweet.tweetId, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        Log.i(TAG, "made a reply");
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                        Log.i(TAG, "Reply failed: " + response, throwable);
//                    }
//                });
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
    public void finish() {
        Intent i  = getIntent();
        i.putExtra("modifiedTweet", Parcels.wrap(tweet));
        setResult(RESULT_OK, i);
        super.finish();
    }
}