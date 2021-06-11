package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/*
Some notes on CRUD:
Create = POST
Read = GET
Update = PUT
Delete = DELETE
 */

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";

    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout srlTweets;

    TwitterClient client;
    RecyclerView rvTweets;
    TweetsAdapter adapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Log.i(TAG, "onCreate");

        //Initialize SwipeRefreshListener and set on listener
        srlTweets = findViewById(R.id.srlTweets);
        srlTweets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });

        //Set a lovely animation for the refresh
        srlTweets.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Create a client that contains tweets pulled
        // from method getRestClient()
        client = TwitterApp.getRestClient(this);

        //Find RecyclerView
        rvTweets = findViewById(R.id.rvTweets);
        //Init list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        //Set layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();
    }

    /**
     * Creates a menu of existing menu items.
     * @param menu The menu.
     * @return An inflated menu with desired items.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu; adds items to menu if items are present
        //Requires id and menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles if an item is clicked in the menu. Alternatively, you
     * can attach a method to a menu item's xml using
     * the android:onClick.
     * @param item The item being clicked.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mCompose) {
            //Navigate to compose activity
            Intent intent = new Intent(TimelineActivity.this,
                    ComposeActivity.class);
            //We want to update the timeline with the newly composed
            // tweet without manually reloading; we use
            // startActivityForResult to update our timeline;
            // requires override method onActivityResult
            //startActivity(intent);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
            //True means item was successful
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Get data from intent, in this case the tweet object
            Tweet tweet =
                    Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Update RecView with new tweet
            tweets.add(0, tweet);
            //Updated adapter
            adapter.notifyItemInserted(0);
            //Scroll to top to view composed tweet
            rvTweets.smoothScrollToPosition(0);
        }
    }

    private void populateHomeTimeline() {
        //Actually get the information we defined in TwitterApp
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onPopulateTimelineSuccess: " + json.toString());
                JSONArray results = json.jsonArray;
                try {
                    //Clear the adapter to ensure no errors occur
                    adapter.clear();
                    tweets.addAll(Tweet.fromJsonArray(results));
                    //Is this necessary, since we call for Notify?
                    //adapter.addAll(tweets);
                    adapter.notifyDataSetChanged();
                    //Signal refresh is complete if a refresh was brought here
                    srlTweets.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception: " + e);
                }
            }

            //It is IMPERATIVE to include String response to check for rate limit
            @Override
            public void onFailure(int statusCode, Headers headers,
                                  String response, Throwable throwable) {
                Log.e(TAG, "onFailure: " + response , throwable);
            }
        });
    }
}

/*
Notes:
The First/Second episode says to check declaration of intent link,
    but this is to be done in the developer portal (I did not see this navigation
    in the video, and had to search through dev settings in Twitter).
The Pull To Refresh guide says to use an adapter.addAll() custom method,
    yet we already implement a dataSetChanged method from adapter...is
    important (they seem to do the same thing, unless it's better memory)?
Getting a full tweet requires the premium API version. Do we need to worry
    about applying for this upgrade?
 */