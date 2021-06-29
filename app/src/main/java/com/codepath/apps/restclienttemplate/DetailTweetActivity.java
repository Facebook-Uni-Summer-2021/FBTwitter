package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

/**
 * Displays a detailed view of a tweet from the timeline
 */
public class DetailTweetActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivityTweet";

    TwitterClient client;

    //Widgets not in use in favor of ViewBinding
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

        //Get tweet from previous activity
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

        Log.i(TAG, "Current tweet: " + tweet.tweetId);
        Log.i(TAG, "Current tweet like status: " + tweet.isFavorited);
        Log.i(TAG, "Name: " + tweet.user.name);
        Log.i(TAG, "ScrenName: " + tweet.user.screenName);

        binding.tvScreenName.setText(tweet.user.screenName);
        binding.tvName.setText("@" + tweet.user.name);
        binding.tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        binding.tvBody.setText(tweet.body);


        if (tweet.hasRetweet) {
            binding.tvLikeCount.setText(String.valueOf(tweet.retweet.likeCount));
            binding.tvRetweetCount.setText(String.valueOf(tweet.retweet.retweetCount));
            //Use Glide for images with URLs
            Glide.with(this)
                    .load(tweet.retweet.user.profileImageUrl)
                    .into(binding.ivProfileImage);
            //Think about adding media here?
        } else {
            binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
            binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            //Use Glide for images with URLs
            Glide.with(this)
                    .load(tweet.user.profileImageUrl)
                    .into(binding.ivProfileImage);
        }

        //List<Media> medias = tweet.entity.medias;
//        Glide.with(this).load(medias.get(0).mediaUrl).into(binding.ivMedia);
        Glide.with(this).load(tweet.mediaUrl).into(binding.ivMedia);


        //Defines like and retweet image based on if it has been liked or retweeted
        if (tweet.hasRetweet) {
            if (tweet.retweet.isFavorited) {
                binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
            } else {
                binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
            }

            if (tweet.retweet.isRetweeted) {
                binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
            } else {
                binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
            }
        } else {
            if (tweet.isFavorited) {
                binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
            } else {
                binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
            }

            if (tweet.isRetweeted) {
                binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
            } else {
                binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
            }
        }

        //To deal with possible disconnect and to make it nicer, manually
        // change the values of the likeCount and ivLike to accurately display changes
        // to user before API update
        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Like");

                if (tweet.hasRetweet) {
                    if (tweet.retweet.isFavorited) {
                        //All work placed in Tweet model so work stays consistent
                        tweet.retweet.unlike(client);
                        binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
                    } else {
                        tweet.retweet.like(client);
                        binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
                    }
                    //Make sure to set count!!!!!
                    binding.tvLikeCount.setText(String.valueOf(tweet.retweet.likeCount));
                } else {
                    if (tweet.isFavorited) {
                        //All work placed in Tweet model so work stays consistent
                        tweet.unlike(client);
                        binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
                    } else {
                        tweet.like(client);
                        binding.ivLike.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
                    }
                    //Make sure to set count!!!!!
                    binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
                }
            }
        });

        //Same as ivLike handler
        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClick Retweet");

                if (tweet.hasRetweet) {
                    if (tweet.retweet.isRetweeted) {
                        tweet.retweet.unRetweet(client);
                        binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
                    } else {
                        tweet.retweet.retweet(client);
                        binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
                    }
                    binding.tvRetweetCount.setText(String.valueOf(tweet.retweet.retweetCount));
                } else {
                    if (tweet.isRetweeted) {
                        tweet.unRetweet(client);
                        binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
                    } else {
                        tweet.retweet(client);
                        binding.ivRetweet.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
                    }
                    binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                }
            }
        });

        //Create UserDetailActivity to view user information
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

        //Reply to a tweet (requires update in TimelineActivity WITHOUT sending back up)
        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REQUIRES "@screenname"
                Log.i(TAG, "Reply to tweet: " + tweet.tweetId);

                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("DetailReply", DetailTweetActivity.this, tweet);
                composeTweetFragment.show(fm, "fragment_compose_tweet");

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

    //To appropriately update the TimelineActivity, on completion
    // of this activity send back the changed tweet (liked, unliked, retweeted, unretweeted)
    @Override
    public void finish() {
        Intent i  = getIntent();
        i.putExtra("modifiedTweet", Parcels.wrap(tweet));
        setResult(RESULT_OK, i);
        super.finish();
    }
}