package mn.aug.restfulandroid.rest;

import java.util.List;
import java.util.Map;

import android.content.UriMatcher;
import android.net.Uri;
import android.content.Context;

import mn.aug.restfulandroid.rest.resource.Login;
import mn.aug.restfulandroid.rest.resource.Tasks;

public class RestMethodFactory {

	private static RestMethodFactory instance;
	private static Object lock = new Object();
	private UriMatcher uriMatcher;
	private Context mContext;

	private static final int TASKS= 1;
    private static final int LOGIN= 2;

	private RestMethodFactory(Context context) {
		mContext = context.getApplicationContext();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Tasks.AUTHORITY, Tasks.PATH, TASKS);
        uriMatcher.addURI(Login.AUTHORITY, Login.PATH, LOGIN );
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
			Map<String, List<String>> headers, byte[] body) {

		switch (uriMatcher.match(resourceUri)) {
		case TASKS:
                if (method == Method.GET) {
                    return new GetTasksRestMethod(mContext, headers);
                }
                break;

        case LOGIN:
            if (method == Method.PUT) {
                return new LoginRestMethod(mContext, headers,body);
            }
            break;
		}

		return null;
	}

	public static enum Method {
		GET, POST, PUT, DELETE
	}

}
