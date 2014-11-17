package mn.aug.restfulandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Paul on 16/11/2014.
 */
public class TimerServiceHelper {

    public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT_TIMER";
    public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    public static final String START_CHRONO = "mn.aug.restfulandroid.activity.action.START";
    public static final String STOP_CHRONO = "mn.aug.restfulandroid.activity.action.STOP";
    public static final String GET_CHRONO = "mn.aug.restfulandroid.activity.action.GET";

    private static final String startChronoHashkey = "start_chrono";
    private static final String stopChronoHashkey = "stop_chrono";
    private static final String getChronoHashkey= "get_chrono";


    public static final String EXTRA_TASK_ID = "mn.aug.restfulandroid.activity.extra.TASK_ID";
    public static final String EXTRA_INIT_VALUE = "mn.aug.restfulandroid.activity.extra.INIT_VALUE";
    public static final String EXTRA_RESULT = "mn.aug.restfulandroid.activity.extra.RESULT";

    private static Object lock = new Object();

    private static TimerServiceHelper instance;

    //TODO: refactor the key
    private Map<String,Long> pendingRequests = new HashMap<String,Long>();
    private Context ctx;

    private TimerServiceHelper(Context ctx){
        this.ctx = ctx.getApplicationContext();
    }

    public static TimerServiceHelper getInstance(Context ctx){
        synchronized (lock) {
            if(instance == null){
                instance = new TimerServiceHelper(ctx);
            }
        }

        return instance;
    }


    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    public void startChrono(Context context, Long task_id, Long initial_value) {

        long requestId = generateRequestID();
        pendingRequests.put(startChronoHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, startChronoHashkey);
            }
        };

        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(START_CHRONO);
        intent.putExtra(EXTRA_TASK_ID, task_id);
        intent.putExtra(EXTRA_INIT_VALUE, initial_value);
        intent.putExtra(TimerService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        context.startService(intent);

    }

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    public void stopChrono(Context context, Long task_id) {


        long requestId = generateRequestID();
        pendingRequests.put(stopChronoHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, stopChronoHashkey);
            }
        };



        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(STOP_CHRONO);
        intent.putExtra(EXTRA_TASK_ID, task_id);
        intent.putExtra(TimerService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        context.startService(intent);
    }

    public long getChrono(Context context, Long task_id) {

        long requestId = generateRequestID();
        pendingRequests.put(getChronoHashkey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData, getChronoHashkey);
            }
        };


        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(GET_CHRONO);
        intent.putExtra(EXTRA_TASK_ID, task_id);
        intent.putExtra(TimerService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        context.startService(intent);
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

        Intent origIntent = (Intent)resultData.getParcelable(TimerService.ORIGINAL_INTENT_EXTRA);
         if(origIntent != null){
            long requestId = origIntent.getLongExtra(EXTRA_REQUEST_ID, 0);
            long result = origIntent.getLongExtra(EXTRA_RESULT, 0);

            pendingRequests.remove(hashKey);

            Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
            resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
            resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);
            resultBroadcast.putExtra(EXTRA_RESULT,result);

            ctx.sendBroadcast(resultBroadcast);
        }
    }






}
