package mn.aug.restfulandroid.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Facade representing the Tasks data
 * 
 * @author hashbrown
 * 
 */
public class Tasks implements Resource {
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="TASKS";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + PATH);

    private List<Task> tasks;

    @JsonCreator
    public Tasks(List<Task> tasks) {
        this.tasks=tasks;
    }

    public List<Task> getTasks() {

        return tasks;
    }

}
