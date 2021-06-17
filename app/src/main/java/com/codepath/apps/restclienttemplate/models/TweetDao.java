package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//Data Access Object (Dao)
@Dao
public interface TweetDao {
    //Represents combination of info
    //IMPORTANT! THis doesnt seem to automatically get everything it should. Ask question?
    @Query("SELECT Tweet.body AS tweet_body, " +
            "Tweet.createdAt AS tweet_createdAt, " +
            "Tweet.tweetId AS tweet_tweetId, " +
            "User.screenName AS user_screenName," +
            "User.profileImageUrl AS user_profileImageUrl, " +
            "User.*" +
            "FROM Tweet INNER JOIN User " +
            "ON Tweet.userId = User.id " +
            "ORDER BY Tweet.createdAt DESC LIMIT 20")
    //Gives us SQL table
    List<TweetWithUser> recentItems();

    //Can take any number of tweets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    //Insert all models
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);

}
