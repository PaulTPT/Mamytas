package mn.aug.restfulandroid.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;

import org.json.JSONArray;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetTasksRestMethod extends AbstractRestMethod<Tasks>{
	
	private Context mContext;
	
	private static final URI TASKS_URI = URI.create("http://paultpt-wunderlist.rhcloud.com/me/tasks");
	
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

        List<Task> tasks= null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                tasks = mapper.readValue(responseBody, new TypeReference<List<Task>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Tasks(tasks);
		
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

}
