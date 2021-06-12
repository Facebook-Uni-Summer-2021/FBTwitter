package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Parcel
public class Media {
    private static final String TAG = "Media";

    public String displayUrl;
    public String expandedUrl;
    public String mediaUrl;

    //Required by Parceler
    public Media () {}

    public static Media fromJson (JSONObject jsonObject)
            throws JSONException {
        Media media = new Media();

        media.displayUrl = secureString(jsonObject.getString("display_url"));
        media.expandedUrl = secureString(jsonObject.getString("expanded_url"));
        media.mediaUrl = secureString(jsonObject.getString("media_url"));

        return media;
    }

    private static String secureString (String str) {
        if (str.substring(0, 4).compareTo("http") == 0 && str.substring(0, 5).compareTo("https") != 0) {
            Log.i(TAG, "Previous: " + str);
            str = "https" + str.substring(4);
            Log.i(TAG, "Secured: " + str);
        } else if (str.substring(0, 5).compareTo("https") == 0) {
            //Do nothing
        }
        return str;
    }

    public static List<Media> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Media> medias = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            medias.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return medias;
    }

    public static List<Media> nullArray() {
        List<Media> medias = new ArrayList<>();

        medias.add(new Media());

        return medias;
    }

//    public String getDisplayUrl() {
//        return displayUrl;
//    }
//
//    public String getExpandedUrl() {
//        return expandedUrl;
//    }
//
//    public String getMediaUrl() {
//        return mediaUrl;
//    }
}
