package mn.aug.restfulandroid.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.net.Uri;

import mn.aug.restfulandroid.util.Logger;


/**
 * Facade representing the Tasks data
 *
 * @author Paul
 *
 */
public class Login implements Resource {
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="LOGIN";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);


    private String token=null;



    public Login(JSONObject json){


        try {
            this.token=json.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.debug("json", e.getMessage());
        }


    }

    public String getToken(){
        return token;
    }





}