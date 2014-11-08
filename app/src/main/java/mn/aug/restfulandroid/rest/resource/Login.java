package mn.aug.restfulandroid.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mn.aug.restfulandroid.provider.UsersDBAccess;
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
    private String name=null;

    @JsonCreator
    public Login(@JsonProperty("token") String token,@JsonProperty("name") String email) {
        this.token = token;
        this.name = email;
        Logger.debug("loggin",toString());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Login{" +
                "token='" + token + '\'' +
                ", email='" + name + '\'' +
                '}';
    }
}