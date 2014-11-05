package mn.aug.restfulandroid.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;


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

    private JSONArray tasksData;
    private List<Task> tasks;

    public Tasks(JSONArray tasksData) {
        this.tasksData = tasksData;
    }

    public List<Task> getTasks() {
        //lazy load
        if(tasks == null){
            tasks = new ArrayList<Task>();
            for (int i = 0; i < tasksData.length(); i++) {
                try {
                    Task task = new Task(tasksData.getJSONObject(i));
                    tasks.add(task);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return tasks;
    }

}
