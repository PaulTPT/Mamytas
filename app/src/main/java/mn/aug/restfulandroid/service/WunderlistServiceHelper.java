package mn.aug.restfulandroid.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timer;


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
    private static final String putTaskHashkey = "PUT_TASK";
    private static final String deleteTaskHashkey = "DELETE_TASK";
    private static final String getListsHashkey = "GET_LISTS";
    private static final String postListHashkey = "POST_LIST";
    private static final String putListHashkey = "PUT_LIST";
    private static final String deleteListHashkey = "DELETE_LIST";
    private static final String loginHashkey = "LOGIN";
    private static final String getTimersHashkey = "GET_TIMERS";
    private static final String postTimerHashkey = "POST_TIMER";
    private static final String putTimerHashkey = "PUT_TIMER";
    private static final String shareHashkey = "share";

	private static Object lock = new Object();

	private static WunderlistServiceHelper instance;

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

	public long getTasks(Long list_id) {

		long requestId = generateRequestID();
		pendingRequests.put(getTasksHashkey, requestId);

		ResultReceiver serviceCallback = new ResultReceiver(null){
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleResponse(resultCode, resultData, getTasksHashkey);
			}
		};
        String body_text="list_id="+list_id;
        byte[] body= body_text.getBytes();
		Intent intent = new Intent(this.ctx, WunderlistService.class);
		intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_GET);
		intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TASKS);
		intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,list_id);
		intent.putExtra(REQUEST_ID, requestId);

		this.ctx.startService(intent);

		return requestId;
	}

    public long getLists() {

        long requestId = generateRequestID();
        pendingRequests.put(getListsHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, getListsHashkey);
            }
        };

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_GET);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_LISTS);
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

    public long postTimer(Timer timer) {

        long requestId = generateRequestID();
        pendingRequests.put(postTimerHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, postTimerHashkey);
            }
        };



        byte[] body= timer.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_POST);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TIMERS);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,timer.getTask_id());
        intent.putExtra(WunderlistService.INFO_EXTRA_2,timer.getOwnership_id());
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

    public long postList(Listw list) {

        long requestId = generateRequestID();
        pendingRequests.put(postListHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, postListHashkey);
            }
        };



        byte[] body= list.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_POST);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_LISTS);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,list.getId());
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


    public long putTimer(Timer timer) {

        long requestId = generateRequestID();
        pendingRequests.put(putTimerHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, putTimerHashkey);
            }
        };



        byte[] body= timer.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_PUT);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_TIMERS);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,timer.getTask_id());
        intent.putExtra(WunderlistService.INFO_EXTRA_2,timer.getOwnership_id());
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }

    public long putList(Listw list) {

        long requestId = generateRequestID();
        pendingRequests.put(putListHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, putListHashkey);
            }
        };



        byte[] body=list.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_PUT);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_LIST);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,list.getId());
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
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,task.getId());
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);

        return requestId;
    }


    public long deleteList(Listw list) {

        long requestId = generateRequestID();
        pendingRequests.put(deleteListHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, deleteListHashkey);
            }
        };



        byte[] body= list.toString().getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_DELETE);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_LIST);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,list.getId());
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

    public long shareList(long id, String recipient) {

        long requestId = generateRequestID();
        pendingRequests.put(shareHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData,shareHashkey);
            }
        };



        byte[] body= ("recipient="+recipient).getBytes();

        Intent intent = new Intent(this.ctx, WunderlistService.class);
        intent.putExtra(WunderlistService.METHOD_EXTRA, WunderlistService.METHOD_POST);
        intent.putExtra(WunderlistService.RESOURCE_TYPE_EXTRA, WunderlistService.RESOURCE_TYPE_SHARE);
        intent.putExtra(WunderlistService.BODY_EXTRA, body);
        intent.putExtra(WunderlistService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(WunderlistService.INFO_EXTRA,id);
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
            Parcelable resource= origIntent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);

			pendingRequests.remove(hashKey);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);
            resultBroadcast.putExtra(WunderlistService.RESOURCE_EXTRA, resource);

			ctx.sendBroadcast(resultBroadcast);
		}
	}



}
