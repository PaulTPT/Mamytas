package mn.aug.restfulandroid.rest;

import android.content.Context;

import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Listw;


/**
 * Created by Paul on 11/11/2014.
 */
public class ShareListRestMethod extends AbstractRestMethod<Listw> {

    private Context mContext;

    private String address="http://paultpt-wunderlist.rhcloud.com/";

    private static URI put_URI;

    private Map<String, List<String>> headers;
    private byte[] body;
    private long id=0;

    public ShareListRestMethod(Context context, Map<String, List<String>> headers,byte[] body, long id){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
        this.id=id;
        address=address+id+"/shares";
        put_URI= URI.create(address);
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(RestMethodFactory.Method.POST, put_URI, headers, body);
        return request;
    }

    @Override
    protected Listw parseResponseBody(String responseBody) throws Exception {

        return null;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}