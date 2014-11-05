package mn.aug.restfulandroid.rest.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Task implements Resource {

	private static final String ID_KEY = "id";
	private static final String TITLE_KEY = "title";
	private static final String DUE_DATE_KEY = "dueDate";
    private static final String LIST_ID_KEY = "list_id";

	private JSONObject task;

	public Task(JSONObject taskData) {
		this.task = taskData;
		JSONArray names = this.task.names();
		for (int i = 0; i < names.length(); i++) {
			try {
				System.out.println(names.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Long getId(){
		try {
			return this.task.getLong(ID_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getTitle() {
        try {
            return this.task.getString(TITLE_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDueDate() {
        try {
            return this.task.getString(DUE_DATE_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getListId() {
        try {
            return this.task.getLong(LIST_ID_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
