package mn.aug.restfulandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulListActivity;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timer;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistService;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.DateHelper;
import mn.aug.restfulandroid.util.Logger;

public class TaskActivity extends RESTfulListActivity {

    private static final String TAG = TaskActivity.class.getSimpleName();

    private static final String UPDATED_TIME = "updated_time";
    private static final String TASK_ID = "task_id";
    private static final String LIST_ID = "list_id";
    private static final String START_TIME = "start_time";
    private static final String TIMER = "timer";
    private TasksDBAccess tasksDBAccess = new TasksDBAccess(this);
    private OwnershipDBAccess ownershipDBAccess;
    private Context context;
    private Long requestId_get_timers = 0L;
    private Long requestId_post_timer = 0L;

    private BroadcastReceiver requestReceiver;
    private Button btnEditTask;
    private ImageButton startStopWork;
    private Timer timer;


    private TextView newWorkTimer;
    private Handler customHandler = new Handler();
    private long updatedTime = 0L;
    private long startTime = 0L;

    private Task tache;
    private WunderlistServiceHelper mWunderlistServiceHelper;

    long task_id;
    long list_id;

    // Listener du bouton StartStop
    private OnClickListener play = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //android:src="@drawable/ic_play_arrow_black_24dp"
            if (startStopWork.getTag().toString().equals("Start")) {
                Logger.debug("Start"," button");
                if (store_thread != null) {
                    store_thread.interrupt();
                }
                if (timerThread != null) {
                    timerThread.interrupt();
                }
                startTime =System.currentTimeMillis();
                timer = new Timer(AuthorizationManager.getInstance(context).getUser(), String.valueOf(0L), String.valueOf(startTime), task_id);
                ownershipDBAccess.open();
                timer = ownershipDBAccess.storeTimer(timer);
                Logger.debug("timer", String.valueOf(timer.getOwnership_id()));
                ownershipDBAccess.close();
                requestId_post_timer = mWunderlistServiceHelper.postTimer(timer);
                startStopWork.setTag("Stop");
                startStopWork.setImageResource(R.drawable.ic_stop_white_24dp);
                startStopWork.setBackground(getResources().getDrawable(R.drawable.button_round_stop));

            } else {
                Logger.debug("stop"," button");
                if (store_thread != null) {
                    store_thread.interrupt();
                }
                if (timerThread != null) {
                    timerThread.interrupt();
                }
                startStopWork.setTag("Start");
                startStopWork.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                startStopWork.setBackground(getResources().getDrawable(R.drawable.button_round_play_green));
                timer.setTimer(String.valueOf(updatedTime));
                ownershipDBAccess.open();
                ownershipDBAccess.updateTimer(timer);
                ownershipDBAccess.setStatus(timer.getOwnership_id(), "updated");
                ownershipDBAccess.close();
                mWunderlistServiceHelper.putTimer(timer);
                requestId_get_timers = mWunderlistServiceHelper.getTimers(task_id);

            }
        }
    };

    private Runnable timer_update_runnable = new Runnable() {
        private int secs = 0;
        private int mins = 0;
        private int dix_seconds = 0;


        public void run() {
            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            dix_seconds = (int) (updatedTime % 1000) / 100;
            newWorkTimer.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%01d", dix_seconds));
        }
    };


    private Runnable actualize_runnable = new Runnable() {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                if (timer != null) {
                    try {
                        updatedTime = System.currentTimeMillis() - startTime;
                        customHandler.post(timer_update_runnable);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();

                    }

                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }

        }
    };


    private Runnable store_runnable = new Runnable() {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }

                if (timer != null && startStopWork.getTag().toString().equals("Stop")) {
                    timer.setTimer(String.valueOf(updatedTime));
                    ownershipDBAccess.open();
                    ownershipDBAccess.updateTimer(timer);
                    ownershipDBAccess.close();
                    mWunderlistServiceHelper.putTimer(timer);
                }

            }
        }
    };
    private Thread timerThread;
    private Thread store_thread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.tache_view);
        super.onCreate(savedInstanceState);
        context = this;
        ownershipDBAccess = new OwnershipDBAccess(context);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);

        Intent i = getIntent();
        // getting attached intent data
        task_id = i.getLongExtra(Task.TASK_ID_EXTRA, 0);
        list_id = i.getLongExtra(Listw.LIST_ID_EXTRA, 0);
        // displaying selected product name
        tasksDBAccess.open();
        Logger.debug("task_id", String.valueOf(task_id));
        tache = tasksDBAccess.retrieveTodo(task_id);
        tasksDBAccess.close();


        TextView dueDate = (TextView) findViewById(R.id.dueDate);
        dueDate.setText(tache.getDue_date());
        TextView txtProduct = (TextView) findViewById(R.id.taskName);
        txtProduct.setText(tache.getTitle());

        newWorkTimer = (TextView) findViewById(R.id.newWorkTimer);
        startStopWork = (ImageButton) findViewById(R.id.startWork);
        startStopWork.setOnClickListener(play);

        // view products click event
        btnEditTask = (Button) findViewById(R.id.btnEditTask);
        btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new task activity
                Intent i = new Intent(getApplicationContext(), TaskEditor.class);
                i.putExtra(Task.TASK_ID_EXTRA, task_id);
                i.putExtra(Listw.LIST_ID_EXTRA, list_id);
                startActivity(i);
            }
        });

        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);


    }

    @Override
    protected void refresh() {
        if (requestId_get_timers == 0L)
            requestId_get_timers = mWunderlistServiceHelper.getTimers(task_id);

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * 1. Register for broadcast from WunderlistServiceHelper
		 * 2. See if we've already made a request. a. If so, check the status.
		 * b. If not, make the request (already coded below).
		 */

        IntentFilter filter = new IntentFilter(WunderlistServiceHelper.ACTION_REQUEST_RESULT);

        requestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long resultRequestId = intent.getLongExtra(WunderlistServiceHelper.EXTRA_REQUEST_ID, 0);

                Logger.debug(TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId);

                if (resultRequestId == requestId_get_timers) {

                    Logger.debug(TAG, "Result is for our request ID");

                    int resultCode = intent.getIntExtra(WunderlistServiceHelper.EXTRA_RESULT_CODE, 0);

                    Logger.debug(TAG, "Result code = " + resultCode);

                    if (resultCode == 200) {
                        Logger.debug(TAG, "Updating UI with new data");
                        Timers timers = (Timers) intent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);
                        List<Timer> timersList = timers.getTimers();

                        if (timersList != null && !timersList.isEmpty()) {
                            Logger.debug(TAG, "On a bien reçu " + timersList.size() + " timers de la tâche " + resultRequestId);
                            String user = AuthorizationManager.getInstance(context).getUser();
                            ArrayAdapter<Timer> adapter = new TimersArrayAdapter(context, R.layout.list_timer_item, timersList);
                            setListAdapter(adapter);

                            Date parsedTimeStamp = null, firstDate = null, lastDate = null;
                            int totalWorkTime = 0;
                            for (Timer timer_list : timersList) {
                                if (timer_list.getTimer() != null && !timer_list.getTimer().isEmpty())
                                   totalWorkTime +=(int) (Integer.valueOf(timer_list.getTimer())/60000);
                                if (timer_list.getTimer_start() != null) {

                                    try {
                                        parsedTimeStamp = new Date(Long.parseLong(timer_list.getTimer_start()));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    if (parsedTimeStamp != null) {
                                        if (firstDate == null)
                                            firstDate = new Date(parsedTimeStamp.getTime());
                                        else if (parsedTimeStamp.getTime() < firstDate.getTime())
                                            firstDate =  new Date(parsedTimeStamp.getTime());
                                        if (lastDate == null)
                                            lastDate = (Date) new Date(parsedTimeStamp.getTime());
                                        else if (parsedTimeStamp.getTime() > lastDate.getTime())
                                            lastDate = (Date) new Date(parsedTimeStamp.getTime());
                                    }
                                }
                            }

                            TextView totalTimeSpent = (TextView) findViewById(R.id.totalTimeSpent);
                            totalTimeSpent.setText(totalWorkTime + " min");
                            TextView totalWorkFirstDate = (TextView) findViewById(R.id.totalWorkFirstDate);
                            TextView totalWorkLastDate = (TextView) findViewById(R.id.totalWorkLastDate);
                            if (firstDate != null) {
                                if (firstDate.getTime() != lastDate.getTime()) {
                                    totalWorkFirstDate.setText("du " + DateHelper.dateFormat.format(firstDate.getTime()));
                                    totalWorkLastDate.setText(" au " + DateHelper.dateFormat.format(lastDate.getTime()));
                                } else {
                                    totalWorkLastDate.setText("le " + DateHelper.dateFormat.format(lastDate.getTime()));
                                    totalWorkFirstDate.setText("");
                                }
                            }
                        } else showToast("Vous n'avez pas encore travaillé sur cette tâche");
                        requestId_get_timers = 0L;
                        stopRefreshing();
                    } else if (resultCode == 401) {
                        showToast("Your session has expired");
                        logoutAndFinish();
                    }
                } else if (resultRequestId == requestId_post_timer) {
                    int resultCode = intent.getIntExtra(WunderlistServiceHelper.EXTRA_RESULT_CODE, 0);

                    if (resultCode == 200) {

                        timer = (Timer) intent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);
                        Logger.debug("timer id", String.valueOf(timer.getOwnership_id()));
                        ownershipDBAccess.open();
                        ownershipDBAccess.setStatus(timer.getOwnership_id(), OwnershipDBAccess.CURRENT);
                        Logger.debug("state written",ownershipDBAccess.getStatus(timer.getOwnership_id()));
                        ownershipDBAccess.close();
                        store_thread = new Thread(store_runnable);
                        store_thread.start();
                        timerThread = new Thread(actualize_runnable);
                        timerThread.start();
                    }

                } else {
                    Logger.debug(TAG, "Result is NOT for our request ID");
                }

            }
        };



        registerReceiver(requestReceiver, filter);


        ownershipDBAccess.open();
        Timer currentTimer = ownershipDBAccess.getCurrentTimer(task_id);

        ownershipDBAccess.close();


        if (currentTimer != null) {
            timer = currentTimer;
            Logger.debug("timer retrieved", timer.getTimer());
            try {
                startTime = Long.parseLong(timer.getTimer_start());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            startStopWork.setTag("Stop");
            startStopWork.setImageResource(R.drawable.ic_stop_white_24dp);
            startStopWork.setBackground(getResources().getDrawable(R.drawable.button_round_stop));

            if (timerThread != null) {
                timerThread.interrupt();
                customHandler.removeCallbacks(timerThread);
            }

            if (store_thread != null) {
                store_thread.interrupt();
                customHandler.removeCallbacks(store_thread);
            }
            timerThread = new Thread(actualize_runnable);
            timerThread.start();
            store_thread = new Thread(store_runnable);
            store_thread.start();
        }

        requestId_get_timers = mWunderlistServiceHelper.getTimers(task_id);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister for broadcast
        if (requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e) {
                Logger.error(TAG, e.getLocalizedMessage(), e);
            }
        }


        if (store_thread != null) {
            store_thread.interrupt();
        }
        if (timerThread != null) {
            timerThread.interrupt();
        }

    }


    private void showToast(String message) {
        if (!isFinishing()) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        setRefreshingItem(menu.findItem(R.id.refresh));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout:
                logoutAndFinish();
                break;
            case R.id.refresh:
                startRefreshing();
                refresh();
                break;
        }
        return false;
    }


    protected void logoutAndFinish() {
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerThread != null) {
            timerThread.interrupt();
            customHandler.removeCallbacks(timerThread);
        }

        if (store_thread != null) {
            store_thread.interrupt();
            customHandler.removeCallbacks(store_thread);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putLong(UPDATED_TIME, updatedTime);
        savedInstanceState.putLong(START_TIME, startTime);
        savedInstanceState.putParcelable(TIMER, timer);
        savedInstanceState.putLong(TASK_ID, task_id);
        savedInstanceState.putLong(LIST_ID, list_id);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        updatedTime = savedInstanceState.getLong(UPDATED_TIME);
        startTime = savedInstanceState.getLong(START_TIME);
        timer = (Timer) savedInstanceState.getParcelable(TIMER);
        task_id=savedInstanceState.getLong(TASK_ID);
        list_id=savedInstanceState.getLong(LIST_ID);

    }
}