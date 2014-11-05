package mn.aug.restfulandroid.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


/**
 * Twitter API
 *
 * @author jeremyhaberman
 */
public class WunderlistServiceHelper {

	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String tasksHashkey = "TASKS";
    private static final String loginHashkey = "LOGIN";

	private static Object lock = new Object();

	private static WunderlistServiceHelper instance;

	//TODO: refactor the key
	private Map<String,Long> pendingRequests = new HashMap<String,Long>();
	private Context ctx;

	private WunderlistServiceHelper(Context ctx){
		this.ctx = ctx.getApplicationContext();
	}

	public static WunderlistServiceHelper getInstance(Context ctx){
		synchronized (lock) {
			if(instance == null){
				instance = new WunderlistServiceHelper(ctx);
			}
		}

		return instance;		
	}

	public long getTasks() {

		long requestId = generateRequestID();
		pendingRequests.put(tasksHashkey, requestId);

		ResultReceiver serviceCallback = new ResultReceiver(null){
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleGetTasksResponse(resultCode, resultData);
			}
		};

		Intent intent = new Intent(this.ctx, WunderlistService.class);
		intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_GET);
		intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TASKS);
		intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
		intent.putExtra(REQUEST_ID, requestId);

		this.ctx.startService(intent);
		
		return requestId;		
	}


    public long login(String email,String password) {

        long requestId = generateRequestID();
        pendingRequests.put(loginHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleLoginResponse(resultCode, resultData);
            }
        };

        String body_char= "email="+email+"&password="+password;
        byte[] body= body_char.getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_PUT);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_LOGIN);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }


	private long generateRequestID() {
		long requestId = UUID.randomUUID().getLeastSignificantBits();
		return requestId;
	}

	public boolean isRequestPending(long requestId){
		return this.pendingRequests.containsValue(requestId);
	}


	private void handleGetTasksResponse(int resultCode, Bundle resultData){

		Intent origIntent = (Intent)resultData.getParcelable(WunderlistService.ORIGINAL_INTENT_EXTRA);

		if(origIntent != null){
			long requestId = origIntent.getLongExtra(REQUEST_ID, 0);

			pendingRequests.remove(tasksHashkey);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

			ctx.sendBroadcast(resultBroadcast);
		}
	}

    //TODO
    private void handleLoginResponse(int resultCode, Bundle resultData){

        Intent origIntent = (Intent)resultData.getParcelable(WunderlistService.ORIGINAL_INTENT_EXTRA);

        if(origIntent != null){
            long requestId = origIntent.getLongExtra(REQUEST_ID, 0);

            pendingRequests.remove(tasksHashkey);

            Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
            resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
            resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

            ctx.sendBroadcast(resultBroadcast);
        }
    }

}
