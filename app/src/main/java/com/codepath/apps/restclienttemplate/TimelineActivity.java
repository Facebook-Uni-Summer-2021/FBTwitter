package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
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

/**
 * The full user timeline.
 */
public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";

    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout srlTweets;
    private EndlessRecyclerViewScrollListener scrollListener;

    FragmentManager fm;
    TwitterClient client;
    TweetDao tweetDao;
    RecyclerView rvTweets;
    TweetsAdapter adapter;
    List<Tweet> tweets;
    ProgressBar pb;

    boolean isCreated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //Custom toolbar for lovely design animations (see AndroidManifest)
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#07CCD9"));
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();
        Log.i(TAG, "onCreate");
        isCreated = true;

        //Progress bar
        pb = findViewById(R.id.pbTweetLoading);
        pb.setVisibility(ProgressBar.INVISIBLE);

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

        //Define Dao
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();


        //Find RecyclerView
        rvTweets = findViewById(R.id.rvTweets);
        //Init list of tweets and adapter
        tweets = new ArrayList<>();

        //Set layout manager and adapter
        adapter = new TweetsAdapter(this, tweets, fm);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);

        //Handle loading additional tweets
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Toast.makeText(TimelineActivity.this, "Loading tweets", Toast.LENGTH_SHORT).show();
                int current = totalItemsCount - page;
                Log.i(TAG, "onLoadMore: " + page + "at " + tweets.get(current).body);
                Log.i(TAG, "onLoadMore: " + "id: " + tweets.get(current).tweetId);
                long id = tweets.get(current).tweetId - 1;
                client.getHomeTimeline(id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onPopulateTimelineSuccess loadMore: " + json.toString());
                        JSONArray results = json.jsonArray;
                        try {
                            //Append current tweet list with older tweets
                            tweets.addAll(Tweet.fromJsonArray(results));
                            //Is this necessary, since we call for Notify?
                            //adapter.addAll(tweets);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Json exception: " + e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: " + response, throwable);
                    }
                });
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.setAdapter(adapter);

        //Query for existing tweets in DB;
        // run on background thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from DB");
                List<TweetWithUser> tweetWithUsers =
                        tweetDao.recentItems();

                //Get tweets from TweetWithUser
                List<Tweet> tweetsFromDB =
                        TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });

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
        Log.e(TAG, "onCreateMenu");
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
            /*
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
             */
            //FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("ComposeTweet", this, null);
            composeTweetFragment.show(fm, "fragment_compose_tweet");

            composeTweetFragment.setComposeListener(new ComposeTweetFragment.ComposeListener() {
                @Override
                public void onDialogReady(String title) {
                    Log.i(TAG, "Title: " + title);
                }

                @Override
                public void onTweetLoaded(Tweet tweet) {
                    //Pass activity if I add replies here
                    Log.i(TAG, "Load tweet into recycler");
//                    //Get data from intent, in this case the tweet object
//                    Tweet tweet =
//                            Parcels.unwrap(data.getParcelableExtra("tweet"));
                    //Update RecView with new tweet
                    tweets.add(0, tweet);
                    //Updated adapter
                    adapter.notifyItemInserted(0);
                    //Scroll to top to view composed tweet
                    rvTweets.smoothScrollToPosition(0);
                }
            });


        } else if (item.getItemId() == R.id.mSignOut) {
            //Logout!!! CHECK THIS OUT!
            client.clearAccessToken();
            // go back to the login activity
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Handled original ComposeActivity, which is no longer included

            //Get data from intent, in this case the tweet object
            Tweet tweet =
                    Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Update RecView with new tweet
            tweets.add(0, tweet);
            //Updated adapter
            adapter.notifyItemInserted(0);
            //Scroll to top to view composed tweet
            rvTweets.smoothScrollToPosition(0);
        } else if (requestCode == 55 && resultCode == RESULT_OK) {
            //Handles any updates made to tweets by DetailTweetActivity to ensure
            // RecyclerView is updated appropriately.

            Log.e(TAG, "onExitTweetDetail");
            // update the tweets array
            Tweet modifiedTweet =
                    Parcels.unwrap(data.getParcelableExtra("modifiedTweet"));
            for (int i = 0; i < tweets.size(); i++) {
                //Find the old tweet (before update) and replace it
                // with new tweet (unwrapped from DetailTweetActivity)
                if(tweets.get(i).tweetId == modifiedTweet.tweetId) {
                    tweets.remove(i);
                    tweets.add(i, modifiedTweet);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    private void populateHomeTimeline() {
        //Actually get the information we defined in TwitterApp
        //Progress bar
        pb.setVisibility(ProgressBar.VISIBLE);

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onPopulateTimelineSuccess json: " + json.toString());
                JSONArray results = json.jsonArray;
                try {
                    //Tweet DB
                    final List<Tweet> tweetsFromNetwork =
                            Tweet.fromJsonArray(results);

                    //Clear the adapter to ensure no errors occur
                    tweets.clear();
                    adapter.clear();
                    tweets.addAll(tweetsFromNetwork);
                    //Is this necessary, since we call for Notify?
                    //adapter.addAll(tweets);
                    adapter.notifyDataSetChanged();
                    //Signal refresh is complete if a refresh was brought here
                    srlTweets.setRefreshing(false);

                    //Progress bar
                    pb.setVisibility(ProgressBar.INVISIBLE);

                    //Query for existing tweets in DB;
                    // run on background thread
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into DB");
                            //Insert users first for foreign key
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            //Insert tweets last
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));

                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception: " + e);
                }
            }

            //It is IMPERATIVE to include String response to check for rate limit
            @Override
            public void onFailure(int statusCode, Headers headers,
                                  String response, Throwable throwable) {
                Log.e(TAG, "onFailure: " + response , throwable);
                //Toast.makeText(TimelineActivity.this, "Rate limit reached! Please wait 15 minutes.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        Log.i(TAG, "ENTERING ACTIVITY");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
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
Trying to use Glide while processing the image causes the RecView to misassign
    the image to the wrong item; what exactly causes this? MAKE SURE TO DO
    URL WORK BEFORE ASSIGNING URL TO GLIDE
For viewbinding, MAKE SURE TO REBUILD WHEN CHANGES ARE MADE (see error)

Notes on Persistence
ORM - Object Relation Mapper
    Ex. Room; helps get data from SQLite by mapping
    data to Java Objects
 */