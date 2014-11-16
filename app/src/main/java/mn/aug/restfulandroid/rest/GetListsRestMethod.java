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

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Lists;
import mn.aug.restfulandroid.rest.resource.Listw;

public class GetListsRestMethod extends AbstractRestMethod<Lists>{

    private Context mContext;

    private static final URI LISTS_URI = URI.create("http://paultpt-wunderlist.rhcloud.com/me/lists");

    private Map<String, List<String>> headers;

    public GetListsRestMethod(Context context, Map<String, List<String>> headers){
        mContext = context.getApplicationContext();
        this.headers = headers;
    }

    @Override
    protected Request buildRequest() {

        Request request = new Request(Method.GET, LISTS_URI, headers, null);
        return request;
    }

    @Override
    protected Lists parseResponseBody(String responseBody) throws Exception {

        List<Listw> lists= null;

        if (responseBody!= null && responseBody.length()>0) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                lists = mapper.readValue(responseBody, new TypeReference<List<Listw>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Lists(lists);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

}
