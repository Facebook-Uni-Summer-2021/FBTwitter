package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.codepath.apps.restclienttemplate.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.DetailTweetActivity;
import com.codepath.apps.restclienttemplate.DetailUserActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    /*
    The following are steps to creating an adapter:
    Step 1. Create a ViewHolder public class that extends
        RecyclerView.ViewHolder and define the widget variables
        using itemView
    Step 2. Extend adapter class with
        RecyclerView.Adapter<[AdapterClass].ViewHolder> (implement methods)
    Step 4. Define context and items list in adapter, then generate constructor
    Step 5. Inflate view in onCreateViewHolder with
        "View view = LayoutInflater.from(context).inflate(R.layout.item_movie,
        parent, false);" and return as new ViewHolder
    Step 6. In onBindViewHolder, create an item from the list of items
        (Item item = items.get(index)) and populate widget variables
        through holder.bind(item) (fix error by creating new method
        "bind()" in ViewHolder)
    Step 7. Bind appropriate item model information to widget variables
        from ViewHolder
    Step 8. Set getItemCount to size of items list

    onCreateViewHolder - inflates an xml layout and return as ViewHolder
    onBindViewHolder - populates data into view through ViewHolder
    getItemCount - returns total items of items list
    */
    private static final String TAG = "TweetsAdapter";

    TwitterClient client;
    FragmentManager fm;
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets, FragmentManager fm) {
        this.context = context;
        this.tweets = tweets;
        this.fm = fm;
        client = TwitterApp.getRestClient(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate a view to be displayed
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get data at position
        Tweet tweet = tweets.get(position);
        //Bind tweet with holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /*
    Helper methods to clear or items from a list
     */

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll (List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        TextView tvBody;
        TextView tvTweetLikeCount;
        TextView tvTweetRetweetCount;
        ImageView ivProfileImage;
        ImageView ivMedia;
        ImageView ivTweetLike;
        ImageView ivTweetRetweet;
        ImageView ivTweetReply;

        RelativeLayout rlTweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //An activity does not exist for the adapter, or so it SEEMS
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvTweetLikeCount = itemView.findViewById(R.id.tvTweetLikeCount);
            tvTweetRetweetCount = itemView.findViewById(R.id.tvTweetRetweetCount);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivTweetLike = itemView.findViewById(R.id.ivTweetLike);
            ivTweetRetweet = itemView.findViewById(R.id.ivTweetRetweet);
            ivTweetReply = itemView.findViewById(R.id.ivTweetReply);
            rlTweet = itemView.findViewById(R.id.rlTweet);
        }

        public void bind(final Tweet tweet) {
            tvScreenName.setText("@" + tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            tvBody.setText(tweet.body);
            tvTweetLikeCount.setText(String.valueOf(tweet.likeCount));
            tvTweetRetweetCount.setText(String.valueOf(tweet.retweetCount));

            //Use Glide for images with URLs
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);

            List<Media> medias = tweet.entity.medias;
//            Media media = tweet.entity.medias.get(0);
//            Log.i(TAG, tweet.user.screenName + "media: " + medias.get(0).mediaUrl);
//            Log.i(TAG, "Avatar: " + tweet.user.profileImageUrl);
//            Log.i(TAG, "Tweet: " + tweet.body);
//            if (tweet.entity.medias != null) {
//                //Temporarily hard code size of media image, found in API
//                // of media (should include as attribute)
////                ivMedia.getLayoutParams().height = 150;
////                ivMedia.getLayoutParams().width = 150;
//                //ivMedia.setLayoutParams();
//                if (medias.get(0).mediaUrl.substring(0, 3).compareTo("http") == 0) {
//                    String secureUrl = "https" + medias.get(0).mediaUrl.substring(4, medias.get(0).mediaUrl.length() - 1);
//                    Log.i(TAG, "Secured: " + secureUrl);
//                }
//                Glide.with(context).load(medias.get(0).mediaUrl).into(ivMedia);
//            }

            Glide.with(context).load(medias.get(0).mediaUrl).into(ivMedia);
//            try {
//                ivMedia.getLayoutParams().height = 150;
//                ivMedia.getLayoutParams().width = 150;
//                Log.e(TAG, medias.get(0).mediaUrl.substring(0, 4));
//
//
//            } catch (NullPointerException e) {
//                Log.i(TAG, "No medias available");
//            }

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClickAvatar");
                    //Create detailed activity to view followers and following
                }
            });

            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClickTweet");
                    //Create detailed tweet with like/dislike
                    Intent intent = new Intent(context, DetailTweetActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
            });

            if (tweet.isFavorited) {
                ivTweetLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart));
            } else {
                ivTweetLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart_stroke));
            }

            if (tweet.isRetweeted) {
                ivTweetRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet));
            } else {
                ivTweetRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet_stroke));
            }

            ivTweetLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isFavorited) {
                        tweet.unlike(client);
                        ivTweetLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart_stroke));
                        tvTweetLikeCount.setText(Long.toString(tweet.likeCount));
                    } else {
                        tweet.like(client);
                        ivTweetLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart));
                        tvTweetLikeCount.setText(Long.toString(tweet.likeCount));
                    }
                }
            });

            ivTweetRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isRetweeted) {
                        client.unlikeTweet(tweet.tweetId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivTweetRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet_stroke));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Error unreweeting: " + response, throwable);
                            }
                        });
                    } else {
                        client.retweet(tweet.tweetId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivTweetRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "Error reweeting: " + response, throwable);
                            }
                        });
                    }
                }
            });

            ivTweetReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Reply to tweet: " + tweet.tweetId);

                    //FragmentManager fm = getSupportFragmentManager();
                    ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("DetailReply", context, tweet);
                    composeTweetFragment.show(fm, "fragment_compose_tweet");
                    //Handle dismissal HEERREEE
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "View Profile");
                    Intent intent = new Intent(context, DetailUserActivity.class);
                    intent.putExtra("user", Parcels.wrap(tweet.user));
//                context.startActivity(intent);
//                intent.putExtra("userId", tweet.user.id);
//                intent.putExtra("userImage", tweet.user.profileImageUrl);
                    context.startActivity(intent);
                }
            });

        }
    }
}
