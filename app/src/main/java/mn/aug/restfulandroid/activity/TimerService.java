package mn.aug.restfulandroid.activity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;

import java.util.HashMap;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TimerService extends IntentService {


    public static final String SERVICE_CALLBACK = "wunderlist.SERVICE_CALLBACK";

    public static final String ORIGINAL_INTENT_EXTRA = "wunderlist.ORIGINAL_INTENT_EXTRA";

    private static final int REQUEST_INVALID = -1;


    private ResultReceiver mCallback;

    private Intent mOriginalRequestIntent;
    private static HashMap<Long,Time_storage> chronos = new HashMap<Long,Time_storage>();


    public TimerService() {
        super("TimerService");
    }

    @Override
    protected void onHandleIntent(Intent requestIntent) {

        mCallback = requestIntent.getParcelableExtra(SERVICE_CALLBACK);

        mOriginalRequestIntent = requestIntent;

        String action = mOriginalRequestIntent.getAction();

        if (action.equals(TimerServiceHelper.START_CHRONO)) {
            long task_id = mOriginalRequestIntent.getLongExtra(TimerServiceHelper.EXTRA_TASK_ID, 0L);
            long initial_timer = mOriginalRequestIntent.getLongExtra(TimerServiceHelper.EXTRA_INIT_VALUE, 0L);

            if (chronos.containsKey(task_id)) {
                chronos.remove(task_id);
            }
                Time_storage storage=new Time_storage(initial_timer, SystemClock.uptimeMillis());
                chronos.put(task_id,storage);

        } else if (action.equals(TimerServiceHelper.STOP_CHRONO)) {
            long task_id = mOriginalRequestIntent.getLongExtra(TimerServiceHelper.EXTRA_TASK_ID, 0L);
            if (chronos.containsKey(task_id)) {
                chronos.remove(task_id);
               }

        } else if (action.equals(TimerServiceHelper.GET_CHRONO)) {
            long task_id = mOriginalRequestIntent.getLongExtra(TimerServiceHelper.EXTRA_TASK_ID, 0L);
            if (chronos.containsKey(task_id)) {
                Time_storage storage = chronos.get(task_id);
                long initial_timer = storage.getChrono_init();
                long start_time = storage.getStart_time();
                long timeInMilliseconds = SystemClock.uptimeMillis() - start_time;
                long updatedTime = initial_timer + timeInMilliseconds;
                mCallback.send(1, getOriginalIntentBundleWithValue(updatedTime));
            }
             mCallback.send(0, getOriginalIntentBundle());
        }


    }




    protected Bundle getOriginalIntentBundle() {
        Bundle originalRequest = new Bundle();
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
        return originalRequest;
    }

    protected Bundle getOriginalIntentBundleWithValue(Long chrono_ms) {
        Bundle originalRequest = new Bundle();
        mOriginalRequestIntent.putExtra(TimerServiceHelper.EXTRA_RESULT, chrono_ms);
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
        return originalRequest;
    }



    private class Time_storage {
        private Long start_time;
        private Long chrono_init;

        private Time_storage(Long chrono_init, Long start_time) {
            this.chrono_init = chrono_init;
            this.start_time = start_time;
        }

        public Long getStart_time() {
            return start_time;
        }

        public void setStart_time(Long start_time) {
            this.start_time = start_time;
        }

        public Long getChrono_init() {
            return chrono_init;
        }

        public void setChrono_init(Long chrono_init) {
            this.chrono_init = chrono_init;
        }
    }


}


