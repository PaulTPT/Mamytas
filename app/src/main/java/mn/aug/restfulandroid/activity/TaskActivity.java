package mn.aug.restfulandroid.activity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import mn.aug.restfulandroid.R;
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

public class TaskActivity extends ListActivity {

    private static final String TAG = TaskActivity.class.getSimpleName();
    private TasksDBAccess tasksDBAccess = new TasksDBAccess(this);
    private Context context;
    private Long requestId = 0L;
    private Long requestId_timer = 0L;
    private BroadcastReceiver requestReceiver;
    private BroadcastReceiver requestReceiver_timer;
    private Button startStopWork, btnEditTask;

    private TextView newWorkTimer;
    private Handler customHandler= new Handler();
    private long updatedTime = 0L;

    private Task tache;
    private TimerServiceHelper mTimerServiceHelper;
    private WunderlistServiceHelper mWunderlistServiceHelper;

    long task_id;
    long list_id;

    // Listener du bouton StartStop
    private OnClickListener play = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (startStopWork.getText().toString().equals("Start")) {
                mTimerServiceHelper.startChrono(context,task_id,updatedTime);
                                startStopWork.setText("Stop");
                startStopWork.setBackground(getResources().getDrawable(R.drawable.button_stop));
            } else {
                mTimerServiceHelper.stopChrono(context, task_id);
                customHandler.removeCallbacks(timer_update_runnable);
                startStopWork.setText("Start");
                startStopWork.setBackground(getResources().getDrawable(R.drawable.button_play));
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

    private Runnable actualize_runnable=new Runnable() {

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                requestId_timer=mTimerServiceHelper.getChrono(context,list_id);
                try {
                    wait(50);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }

            }
        }
    };

    private Thread timerThread=new Thread(actualize_runnable);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tache_view);
        context=this;
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        mTimerServiceHelper= TimerServiceHelper.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        startStopWork = (Button) findViewById(R.id.startWork);
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

		/*
         * 1. Register for broadcast from WunderlistServiceHelper
		 * 2. See if we've already made a request. a. If so, check the status.
		 * b. If not, make the request (already coded below).
		 */
        requestId = mWunderlistServiceHelper.getTimers(task_id);
        IntentFilter filter = new IntentFilter(WunderlistServiceHelper.ACTION_REQUEST_RESULT);
        requestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long resultRequestId = intent.getLongExtra(WunderlistServiceHelper.EXTRA_REQUEST_ID, 0);

                Logger.debug(TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId);

                if (resultRequestId == requestId) {

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

                            ArrayAdapter<Timer> adapter = new MyTimersArrayAdapter(context, R.layout.list_timer_item, timersList);
                            setListAdapter(adapter);

                            Date parsedTimeStamp = null, firstDate = null, lastDate = null;
                            int totalWorkTime = 0;
                            for (Timer timer : timersList) {
                                if (timer.getTimer() != null && !timer.getTimer().isEmpty())
                                    totalWorkTime += Integer.valueOf(timer.getTimer());
                                if (timer.getTimer_start() != null) {
                                    try {
                                        parsedTimeStamp = DateHelper.dateTimeFormat.parse(timer.getTimer_start());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (parsedTimeStamp != null) {
                                        if (firstDate == null)
                                            firstDate = (Date) parsedTimeStamp.clone();
                                        else if (parsedTimeStamp.getTime() < firstDate.getTime())
                                            firstDate = (Date) parsedTimeStamp.clone();
                                        if (lastDate == null)
                                            lastDate = (Date) parsedTimeStamp.clone();
                                        else if (parsedTimeStamp.getTime() > lastDate.getTime())
                                            lastDate = (Date) parsedTimeStamp.clone();
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
                                    totalWorkLastDate.setText("au " + DateHelper.dateFormat.format(lastDate.getTime()));
                                } else {
                                    totalWorkLastDate.setText("le " + DateHelper.dateFormat.format(lastDate.getTime()));
                                    totalWorkFirstDate.setText("");
                                }
                            }
                        } else showToast("Vous n'avez pas encore travaillé sur cette tâche");
                        requestId = 0L;
                    } else if (resultCode == 401) {
                        showToast("Your session has expired");
                        logoutAndFinish();
                    }
                } else {
                    Logger.debug(TAG, "Result is NOT for our request ID");
                }

            }
        };
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.registerReceiver(requestReceiver, filter);
        if (requestId == 0L)
            requestId = mWunderlistServiceHelper.getTimers(task_id); // Si la requestId n'existe plus c'est un fail on relance.



        IntentFilter filter_timer = new IntentFilter(TimerServiceHelper.ACTION_REQUEST_RESULT);
        requestReceiver_timer = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                long resultRequestId = intent
                        .getLongExtra(TimerServiceHelper.EXTRA_REQUEST_ID, 0);
                int resultCode = intent.getIntExtra(TimerServiceHelper.EXTRA_RESULT_CODE, 0);

                if (resultRequestId == requestId) {
                    if (resultCode == 1) {
                        updatedTime = intent.getLongExtra(TimerServiceHelper.EXTRA_RESULT, 0);
                        customHandler.post(timer_update_runnable);
                    }
                }

            }
        };

        this.registerReceiver(requestReceiver_timer, filter_timer);

        timerThread.start();
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

        // Unregister for broadcast
        if (requestReceiver_timer != null) {
            try {
                this.unregisterReceiver(requestReceiver_timer);
            } catch (IllegalArgumentException e) {
                Logger.error(TAG, e.getLocalizedMessage(), e);
            }
        }

        if(timerThread!=null) {
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
        return true;
    }


    protected void logoutAndFinish() {
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }

    @Override
    public void finish() {
        super.finish();
        timerThread.interrupt();
        customHandler.removeCallbacks(timerThread);
    }
}