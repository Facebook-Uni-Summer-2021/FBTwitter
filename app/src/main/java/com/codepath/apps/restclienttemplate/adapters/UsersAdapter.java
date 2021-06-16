package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.DetailUserActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.util.List;

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
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private static final String TAG = "UsersAdapter";

    Context context;
    List<User> users;

    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvUserScreenName;
        TextView tvUserDesc;
        ImageView ivUserImage;
        ImageView ivUserVerified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserScreenName);
            tvUserScreenName = itemView.findViewById(R.id.tvUserName);
            tvUserDesc = itemView.findViewById(R.id.tvUserDesc);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            ivUserVerified = itemView.findViewById(R.id.ivUserVerified);
        }

        public void bind(final User user) {
            tvUserName.setText(user.name);
            tvUserScreenName.setText(user.screenName);
            tvUserDesc.setText(user.desc);
            //Glide set image
            Glide.with(context).load(user.profileImageUrl).into(ivUserImage);
            ivUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "View Profile");
                    Intent intent = new Intent(context, DetailUserActivity.class);
                    intent.putExtra("user", Parcels.wrap(user));
//                context.startActivity(intent);
//                intent.putExtra("userId", tweet.user.id);
//                intent.putExtra("userImage", tweet.user.profileImageUrl);
                    context.startActivity(intent);
                }
            });
        }
    }
}
