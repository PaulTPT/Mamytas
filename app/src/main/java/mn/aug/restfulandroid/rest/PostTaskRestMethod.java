package mn.aug.restfulandroid.rest;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Login;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;

/**
 * Created by Paul on 10/11/2014.
 */
public class PostTaskRestMethod extends AbstractRestMethod<Task> {

    private Context mContext;

    private static final URI LOGIN_URI = URI.create("http://wunderlist.berthier.cloudbees.net/login");

    private Map<String, List<String>> headers;
    private byte[] body;

    public PostTaskRestMethod(Context context, Map<String, List<String>> headers,byte[] body){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.POST, LOGIN_URI, headers, body);
        return request;
    }

    @Override
    protected Task parseResponseBody(String responseBody) throws Exception {

        Task task=null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                task = mapper.readValue(responseBody, Task.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return task;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}
