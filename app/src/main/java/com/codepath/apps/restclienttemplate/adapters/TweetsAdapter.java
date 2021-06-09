package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

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

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        TextView tvBody;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }

        public void bind(Tweet tweet) {
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText("@" + tweet.user.name);
            tvTime.setText(tweet.createdAt);
            tvBody.setText(tweet.body);

            //Use Glide for images with URLs
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);
        }
    }
}
