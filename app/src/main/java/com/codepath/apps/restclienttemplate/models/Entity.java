package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Potentially could be an object that stores all of the entities
 * in the Twitter API; original purpose to store media from a tweet
 */

@Parcel
public class Entity {
    private static final String TAG = "Entity";

    public List<Media> medias;

    //Required for Parceler
    public Entity () {}

    public static Entity fromJson (JSONObject jsonObject) {
        Entity entity = new Entity();

        try {
            entity.medias = Media.fromJsonArray(jsonObject.getJSONArray("media"));
        } catch (JSONException e) {
            Log.e(TAG, "Can't find media: " + e);
            entity.medias = Media.nullArray();
        }

//        entity.name = jsonObject.getString("name");
//        entity.screenName = jsonObject.getString("screen_name");
//        entity.profileImageUrl =
//                jsonObject.getString("profile_image_url_https");
        return entity;
    }

}
