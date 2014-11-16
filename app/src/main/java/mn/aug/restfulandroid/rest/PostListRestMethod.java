package mn.aug.restfulandroid.rest;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Listw;

/**
 * Created by Paul on 10/11/2014.
 */
public class PostListRestMethod extends AbstractRestMethod<Listw> {

    private Context mContext;

    private static final URI LIST_URI = URI.create("http://paultpt-wunderlist.rhcloud.com/me/lists");

    private Map<String, List<String>> headers;
    private byte[] body;

    public PostListRestMethod(Context context, Map<String, List<String>> headers,byte[] body){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.POST, LIST_URI, headers, body);
        return request;
    }

    @Override
    protected Listw parseResponseBody(String responseBody) throws Exception {

        Listw list=null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                list = mapper.readValue(responseBody, Listw.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}
