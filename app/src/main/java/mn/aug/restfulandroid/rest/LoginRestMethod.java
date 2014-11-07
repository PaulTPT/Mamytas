package mn.aug.restfulandroid.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Login;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        Login login=null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                login = mapper.readValue(responseBody, Login.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return login;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}
