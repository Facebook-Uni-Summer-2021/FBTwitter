package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//Since user is included in Tweet, User needs Parceler annotations
@Parcel
@Entity
public class User {
    private static final String TAG = "User";

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;//The "handle"?

    @ColumnInfo
    public String profileImageUrl;

    @ColumnInfo
    public String desc;

    @ColumnInfo
    public boolean isVerified;

//    public List<User> followers;
//    public List<User> friends;

    //Required by Parceler
    public User () {}

    public static User fromJson (JSONObject jsonObject)
            throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.id = jsonObject.getLong("id");
        user.desc = jsonObject.getString("description");
        user.isVerified = jsonObject.getBoolean("verified");
        user.profileImageUrl =
                jsonObject.getString("profile_image_url_https");
        return user;
    }

    public static List<User> fromUserJson (JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        if (jsonArray != null) {
            //Log.i(TAG, "what: " + jsonArray.getJSONObject(0));
            for (int i = 0; i < jsonArray.length(); i++) {
                users.add(fromJson(jsonArray.getJSONObject(i)));
                //Log.i(TAG, "item " + i + ": " + users.get(i));
            }
        } else {
            Log.e(TAG, "no array found");
        }

        return users;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }
}
