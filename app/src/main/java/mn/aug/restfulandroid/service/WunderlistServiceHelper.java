package mn.aug.restfulandroid.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timers;


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
	private static final String getTasksHashkey = "GET_TASKS";
    private static final String postTaskHashkey = "POST_TASK";
    private static final String loginHashkey = "LOGIN";
    private static final String putTaskHashkey = "PUT_TASK";
    private static final String deleteTaskHashkey = "DELETE_TASK";
    private static final String getTimersHashkey = "GET_TIMERS";

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
		pendingRequests.put(getTasksHashkey, requestId);

		ResultReceiver serviceCallback = new ResultReceiver(null){
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleResponse(resultCode, resultData, getTasksHashkey);
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

    public long getTimers(Long task_id) {

        long requestId = generateRequestID();
        pendingRequests.put(getTimersHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, getTimersHashkey);
            }
        };

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_GET);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TIMERS);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,task_id);
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }

    public long postTask(Task task) {

        long requestId = generateRequestID();
        pendingRequests.put(postTaskHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, postTaskHashkey);
            }
        };



        byte[] body= task.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_POST);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TASKS);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,task.getId());
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }

    public long putTask(Task task) {

        long requestId = generateRequestID();
        pendingRequests.put(putTaskHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, putTaskHashkey);
            }
        };



        byte[] body= task.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_PUT);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TASK);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,task.getId());
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }


    public long deleteTask(Task task) {

        long requestId = generateRequestID();
        pendingRequests.put(deleteTaskHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, deleteTaskHashkey);
            }
        };



        byte[] body= task.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_DELETE);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TASK);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,task.getId());
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
                handleResponse(resultCode, resultData, loginHashkey);
            }
        };

        String body_char= "email="+email+"&password="+password;
        byte[] body= body_char.getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_POST);
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


	private void handleResponse(int resultCode, Bundle resultData, String hashKey){

		Intent origIntent = (Intent)resultData.getParcelable(WunderlistService.ORIGINAL_INTENT_EXTRA);

		if(origIntent != null){
			long requestId = origIntent.getLongExtra(REQUEST_ID, 0);
            Timers timers = origIntent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);

			pendingRequests.remove(hashKey);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);
            resultBroadcast.putExtra(WunderlistService.RESOURCE_EXTRA, timers);

			ctx.sendBroadcast(resultBroadcast);
		}
	}




}
