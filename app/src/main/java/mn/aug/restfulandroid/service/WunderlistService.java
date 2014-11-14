package mn.aug.restfulandroid.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

public class WunderlistService extends IntentService {

	public static final String METHOD_EXTRA = "wunderlist.METHOD_EXTRA";

	public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

	public static final String RESOURCE_TYPE_EXTRA = "wunderlist.RESOURCE_TYPE_EXTRA";

    public static final String RESOURCE_EXTRA = "wunderlist.RESOURCE_EXTRA";

    public static final String BODY_EXTRA = "wunderlist.BODY_EXTRA";

    public static final String INFO_EXTRA = "wunderlist.INFO_EXTRA";


	public static final int RESOURCE_TYPE_TASKS = 1;
    public static final int RESOURCE_TYPE_LOGIN = 2;
    public static final int RESOURCE_TYPE_TASK= 3;
    public static final int RESOURCE_TYPE_TIMERS= 4;

	public static final String SERVICE_CALLBACK = "wunderlist.SERVICE_CALLBACK";

	public static final String ORIGINAL_INTENT_EXTRA = "wunderlist.ORIGINAL_INTENT_EXTRA";

	private static final int REQUEST_INVALID = -1;

	private ResultReceiver mCallback;

	private Intent mOriginalRequestIntent;

	public WunderlistService() {
		super("WunderlistService");
	}

	@Override
	protected void onHandleIntent(Intent requestIntent) {

		mOriginalRequestIntent = requestIntent;

		// Get request data from Intent
        byte[] body= requestIntent.getByteArrayExtra(WunderlistService.BODY_EXTRA);
		String method = requestIntent.getStringExtra(WunderlistService.METHOD_EXTRA);
		int resourceType = requestIntent.getIntExtra(WunderlistService.RESOURCE_TYPE_EXTRA, -1);
        //Resource resource= (Resource) requestIntent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);
		mCallback = requestIntent.getParcelableExtra(WunderlistService.SERVICE_CALLBACK);

		switch (resourceType) {

		case RESOURCE_TYPE_TASKS:

			if (method.equalsIgnoreCase(METHOD_GET) ) {
				TasksProcessor processor = new TasksProcessor(getApplicationContext());
				processor.getTasks(makeProcessorCallback());
			}else if (method.equalsIgnoreCase(METHOD_POST) ) {

                long task_id = requestIntent.getLongExtra(WunderlistService.INFO_EXTRA,0);

                TasksProcessor processor = new TasksProcessor(getApplicationContext());
                processor.postTask(makeProcessorCallback(),task_id, body);
            }else{
				mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			}
			break;

            case RESOURCE_TYPE_TASK:

                if (method.equalsIgnoreCase(METHOD_DELETE) ) {
                    long task_id = requestIntent.getLongExtra(WunderlistService.INFO_EXTRA,0);

                    TaskProcessor processor = new TaskProcessor(getApplicationContext());
                    processor.deleteTask(makeProcessorCallback(), task_id);
                }else if (method.equalsIgnoreCase(METHOD_PUT) ) {

                    long task_id = requestIntent.getLongExtra(WunderlistService.INFO_EXTRA,0);

                    TaskProcessor processor = new TaskProcessor(getApplicationContext());
                    processor.putTask(makeProcessorCallback(), task_id, body);
                }else{
                    mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
                }
                break;

            case RESOURCE_TYPE_LOGIN:


                if (method.equalsIgnoreCase(METHOD_POST)) {
                    LoginProcessor processor = new LoginProcessor(getApplicationContext());
                    processor.getToken(makeProcessorCallback(),body);
                } else {
                    mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
                }
                break;

            case RESOURCE_TYPE_TIMERS:

                if (method.equalsIgnoreCase(METHOD_GET) ) {
                    long task_id = requestIntent.getLongExtra(WunderlistService.INFO_EXTRA,0);
                    TimersProcessor processor = new TimersProcessor(getApplicationContext());
                    processor.getTimers(makeProcessorCallback(),task_id);
                }else{
                    mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
                }
                break;
			
		default:
			mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			break;
		}

	}


	private ProcessorCallback makeProcessorCallback() {
		ProcessorCallback callback = new ProcessorCallback() {

			@Override
			public void send(int resultCode) {
				if (mCallback != null) {
                    mCallback.send(resultCode, getOriginalIntentBundle());
                }
			}

            @Override
            public void send(int resultCode, Parcelable resource) {
                if (mCallback != null) {
                    mCallback.send(resultCode, getOriginalIntentBundleWithResource(resource));
                }
            }
        };
		return callback;
	}


	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
		return originalRequest;
	}

    protected Bundle getOriginalIntentBundleWithResource(Parcelable resource) {
        Bundle originalRequest = new Bundle();
        mOriginalRequestIntent.putExtra(RESOURCE_EXTRA,resource);
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
        return originalRequest;
    }
}
