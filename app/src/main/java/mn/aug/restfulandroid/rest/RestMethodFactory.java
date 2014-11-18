package mn.aug.restfulandroid.rest;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import java.util.List;
import java.util.Map;

import mn.aug.restfulandroid.rest.resource.Lists;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Login;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.service.ShareProcessor;

public class RestMethodFactory {

    private static final int TASKS = 1;
    private static final int LOGIN = 2;
    private static final int TASK = 3;
    private static final int TIMERS = 4;
    private static final int LISTS = 5;
    private static final int LIST = 6;
    private static final int SHARE=7;
    private static RestMethodFactory instance;
    private static Object lock = new Object();
    private UriMatcher uriMatcher;
    private Context mContext;

    private RestMethodFactory(Context context) {
        mContext = context.getApplicationContext();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Tasks.AUTHORITY, Tasks.PATH, TASKS);
        uriMatcher.addURI(Login.AUTHORITY, Login.PATH, LOGIN);
        uriMatcher.addURI(Task.AUTHORITY, Task.PATH, TASK);
        uriMatcher.addURI(Timers.AUTHORITY, Timers.PATH, TIMERS);
        uriMatcher.addURI(Lists.AUTHORITY, Lists.PATH, LISTS);
        uriMatcher.addURI(Listw.AUTHORITY, Listw.PATH, LIST);
        uriMatcher.addURI(ShareProcessor.AUTHORITY, ShareProcessor.PATH, SHARE );

    }

    public static RestMethodFactory getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new RestMethodFactory(context);
            }
        }

        return instance;
    }

    public RestMethod getRestMethod(Uri resourceUri, Method method,
                                    Map<String, List<String>> headers, byte[] body, long id) {

        switch (uriMatcher.match(resourceUri)) {
            case TASKS:

                switch (method) {
                    case GET:
                        return new GetTasksRestMethod(mContext, headers, id);
                    case POST:
                        return new PostTaskRestMethod(mContext, headers,body);
                    default :
                        break;
                }

                break;

            case TASK:

                switch (method) {
                    case PUT:
                        return new PutTaskRestMethod(mContext, headers, body, id);
                    case DELETE:
                        return new DeleteTaskRestMethod(mContext, headers,body,id);
                    default :
                        break;
                }

                break;

            case LISTS:

                switch (method) {
                    case GET:
                        return new GetListsRestMethod(mContext, headers);
                    case POST:
                        return new PostListRestMethod(mContext, headers,body);
                    default :
                        break;
                }

                break;

            case SHARE:

                switch (method) {
                    case POST:
                        return new ShareListRestMethod(mContext, headers,body,id);
                    default :
                        break;
                }

                break;

            case LIST:

                switch (method) {
                    case PUT:
                        return new PutListRestMethod(mContext, headers, body, id);
                    case DELETE:
                        return new DeleteListRestMethod(mContext, headers,body,id);
                    default :
                        break;
                }

                break;


            case LOGIN:
                if (method == Method.POST) {
                    return new LoginRestMethod(mContext, headers, body);
                }
                break;

            case TIMERS:

                switch (method) {
                    case GET:
                        return new GetTimersRestMethod(mContext, headers,body,id);
                    case POST:
                        return new PostTimerRestMethod(mContext, headers,body,id);
                    case PUT:
                        return new PutTimerRestMethod(mContext, headers, body, id);
                    default :
                        break;
                }

                break;
        }

        return null;
    }

    public static enum Method {
        GET, POST, PUT, DELETE
    }

}
