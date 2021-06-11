package com.codepath.apps.restclienttemplate.models;

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

        media.displayUrl =
                jsonObject.getString("display_url");
        media.expandedUrl =
                jsonObject.getString("expanded_url");
        media.mediaUrl =
                jsonObject.getString("media_url");

        return media;
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
