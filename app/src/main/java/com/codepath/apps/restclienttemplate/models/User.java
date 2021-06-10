package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

//Since user is included in Tweet, User needs Parceler annotations
@Parcel
public class User {
    private static final String TAG = "User";

    public String name;
    public String screenName;//The "handle"?
    public String profileImageUrl;

    //Required by Parceler
    public User () {}

    public static User fromJson (JSONObject jsonObject)
            throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl =
                jsonObject.getString("profile_image_url_https");
        return user;
    }
}
