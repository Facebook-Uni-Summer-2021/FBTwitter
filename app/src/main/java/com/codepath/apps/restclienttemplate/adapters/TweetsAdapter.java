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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.codepath.apps.restclienttemplate.DetailTweetActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
        ImageView ivProfileImage;
        ImageView ivMedia;

        RelativeLayout rlTweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //An activity does not exist for the adapter, or so it SEEMS
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            rlTweet = itemView.findViewById(R.id.rlTweet);
        }

        public void bind(final Tweet tweet) {
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText("@" + tweet.user.name);
            tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            tvBody.setText(tweet.body);

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

        }
    }
}
