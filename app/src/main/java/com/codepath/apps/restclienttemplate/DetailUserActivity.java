package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.adapters.UsersAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class DetailUserActivity extends AppCompatActivity {
    private static final String TAG = "DetailUserActivity";

    User user;

    TextView tvDetailName;
    TextView tvDetailScreenName;
    TextView tvDesc;
    Button btnFollowing;
    Button btnFollowers;
    ImageView ivDetailProfile;

    TwitterClient client;
    UsersAdapter followerAdapter;
    UsersAdapter followingAdapter;
    RecyclerView rvFollower;
    RecyclerView rvFollowing;
    Context context;
    List<User> following;
    List<User> followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        context = getApplication();
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        //long userid = getIntent().getLongExtra("userId", 0);

        followers = new ArrayList<>();
        following = new ArrayList<>();

        client = TwitterApp.getRestClient(context);
        getFollowers(user.id);
    }

    private void getFollowers (final long userId) {
        client.getFollowers(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.i(TAG, "Json followers: " + json);
                    JSONArray results = json.jsonObject.getJSONArray("users");
                    Log.i(TAG, "Json followers (results): " + results.getJSONObject(0));
                    followers.addAll(User.fromUserJson(results));

                } catch (JSONException e) {
                    Log.e(TAG, "Json exception: " + e);
                }
                getFollowing(userId);

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "GetFollowers failed: " + response, throwable);
            }
        });
    }

    private void getFollowing (final long userId) {
        client.getFriends(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.i(TAG, "Json followering: " + json);
                    JSONArray results = json.jsonObject.getJSONArray("users");
                    Log.i(TAG, "Json followers (results): " + results.getJSONObject(0));
                    following.addAll(User.fromUserJson(results));

                } catch (JSONException e) {
                    Log.e(TAG, "Json exception: " + e);
                }
                create();

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "GetFollowing failed: " + response, throwable);
            }
        });
    }

    private void create () {
        //A bunch of notes
        Log.e(TAG, "inCreate");
        try {
            for (int i = 0; i < followers.size(); i++) {
                Log.i(TAG, "Follower: " + followers.get(i).screenName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        try {
            for (int i = 0; i < following.size(); i++) {
                Log.i(TAG, "Following: " + following.get(i).screenName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        //Find RecyclerView
        rvFollower = findViewById(R.id.rvFollowers);
        rvFollowing = findViewById(R.id.rvFollowing);

        //Set layout manager and adapter
        followerAdapter = new UsersAdapter(this, followers);
        followingAdapter = new UsersAdapter(this, following);
        LinearLayoutManager followerManager =
                new LinearLayoutManager(this);
        LinearLayoutManager followingManager =
                new LinearLayoutManager(this);
        rvFollower.setLayoutManager(followerManager);
        rvFollowing.setLayoutManager(followingManager);
        rvFollower.setAdapter(followerAdapter);
        rvFollowing.setAdapter(followingAdapter);

        tvDesc = findViewById(R.id.tvDesc);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailScreenName = findViewById(R.id.tvDetailScreenName);
        ivDetailProfile = findViewById(R.id.ivDetailProfile);
        btnFollowers = findViewById(R.id.btnFollowers);
        btnFollowing = findViewById(R.id.btnFollowing);

        tvDesc.setText(user.desc);
        tvDetailName.setText(user.name);
        tvDetailScreenName.setText(user.screenName);
        Glide.with(context).load(user.profileImageUrl).into(ivDetailProfile);

        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFollowing.setTextColor(Color.parseColor("#47C4E6"));
                btnFollowers.setTextColor(Color.BLACK);
                rvFollower.setVisibility(View.INVISIBLE);
                rvFollowing.setVisibility(View.VISIBLE);
            }
        });

        btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFollowers.setTextColor(Color.parseColor("#47C4E6"));
                btnFollowing.setTextColor(Color.BLACK);
                rvFollower.setVisibility(View.VISIBLE);
                rvFollowing.setVisibility(View.INVISIBLE);
            }
        });

    }
}