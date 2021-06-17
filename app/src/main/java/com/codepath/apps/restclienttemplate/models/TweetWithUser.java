package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

//Potentially create TweetWithUserAndEntity?
public class TweetWithUser {
    //Emebedded flattens User object into object,
    // preserving encapsulation
    //"Put all of User in TweetWithUser"
    @Embedded(prefix = "user_")
    User user;

    //Prefix all attributes from Tweet with "tweet_" in case
    // certain attributes clash from User
    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < tweetWithUsers.size(); i++) {
            Tweet tweet = tweetWithUsers.get(i).tweet;
            //Gets user from tweetWithUser; do with Entity?
            tweet.user = tweetWithUsers.get(i).user;
            //tweet.entity = tweetWithUsers.get(i).entity;
            tweets.add(tweet);
        }
        return tweets;
    }

    //Join both tables
}
