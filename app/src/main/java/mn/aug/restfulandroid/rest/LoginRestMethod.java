package mn.aug.restfulandroid.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Login;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class LoginRestMethod extends AbstractRestMethod<Login>{

    private Context mContext;

    private static final URI LOGIN_URI = URI.create("http://wunderlist.berthier.cloudbees.net/login");

    private Map<String, List<String>> headers;
    private byte[] body;

    public LoginRestMethod(Context context, Map<String, List<String>> headers,byte[] body){
        mContext = context.getApplicationContext();
        this.headers = headers;
        this.body=body;
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(Method.POST, LOGIN_URI, headers, body);
        return request;
    }

    @Override
    protected Login parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);
        return new Login(json);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}
