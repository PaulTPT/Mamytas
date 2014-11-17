package mn.aug.restfulandroid.rest;

import android.content.Context;

import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Timer;

/**
 * Created by Paul on 17/11/2014.
 */
public class PutTimerRestMethod extends AbstractRestMethod<Timer> {
    private Context mContext;

    private String address="http://paultpt-wunderlist.rhcloud.com/";

    private static URI put_URI;

    private Map<String, List<String>> headers;
    private byte[] body;
    private long id=0;

    public PutTimerRestMethod(Context context, Map<String, List<String>> headers,byte[] body, long id){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
        this.id=id;
        address=address+id+"/timers";
        put_URI= URI.create(address);
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.PUT, put_URI, headers, body);
        return request;
    }

    @Override
    protected Timer parseResponseBody(String responseBody) throws Exception {

       return null;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
