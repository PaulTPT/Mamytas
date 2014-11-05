package mn.aug.restfulandroid.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Tasks;

import org.json.JSONArray;

import android.content.Context;

public class GetTasksRestMethod extends AbstractRestMethod<Tasks>{
	
	private Context mContext;
	
	private static final URI TASKS_URI = URI.create("http://wunderlist.berthier.cloudbees.net/me/tasks");
	
	private Map<String, List<String>> headers;
	
	public GetTasksRestMethod(Context context, Map<String, List<String>> headers){
		mContext = context.getApplicationContext();
		this.headers = headers;
	}

	@Override
	protected Request buildRequest() {
		
		Request request = new Request(Method.GET, TASKS_URI, headers, null);
		return request;
	}

	@Override
	protected Tasks parseResponseBody(String responseBody) throws Exception {
		
		JSONArray json = new JSONArray(responseBody);
		return new Tasks(json);
		
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

}
