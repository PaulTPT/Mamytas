package mn.aug.restfulandroid.rest;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Task;

/**
 * Created by Paul on 11/11/2014.
 */
public class DeleteTaskRestMethod extends AbstractRestMethod<Task> {

    private Context mContext;

    private String address="http://paultpt-wunderlist.rhcloud.com/";

    private static URI delete_URI;

    private Map<String, List<String>> headers;
    private byte[] body;
    private long id=0;

    public DeleteTaskRestMethod(Context context, Map<String, List<String>> headers,byte[] body, long id){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
        this.id=id;
        address=address+id;
        delete_URI= URI.create(address);
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.DELETE, delete_URI, headers, body);
        return request;
    }

    @Override
    protected Task parseResponseBody(String responseBody) throws Exception {

        return null;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}