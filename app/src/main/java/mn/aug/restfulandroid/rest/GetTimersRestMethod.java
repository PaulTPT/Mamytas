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

import mn.aug.restfulandroid.rest.resource.Timer;
import mn.aug.restfulandroid.rest.resource.Timers;

/**
 * Created by Paul on 11/11/2014.
 */
public class GetTimersRestMethod extends AbstractRestMethod<Timers> {

    private Context mContext;

    private String address="http://paultpt-wunderlist.rhcloud.com/";

    private static URI get_URI;

    private Map<String, List<String>> headers;
    private byte[] body;
    private long id=0;

    public GetTimersRestMethod(Context context, Map<String, List<String>> headers,byte[] body, long id){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
        this.id=id;
        address=address+ id +"/timers";
        get_URI= URI.create(address);
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.GET, get_URI, headers, body);
        return request;
    }

    @Override
    protected Timers parseResponseBody(String responseBody) throws Exception {

         List<Timer> timers=null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                timers = mapper.readValue(responseBody, new TypeReference<List<Timer>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Timers(timers);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}