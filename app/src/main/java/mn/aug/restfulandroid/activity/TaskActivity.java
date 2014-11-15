package mn.aug.restfulandroid.activity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.rest.resource.Timer;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistService;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class TaskActivity extends ListActivity {

    private static final String TAG = TaskActivity.class.getSimpleName();

    private TasksDBAccess tasksDBAccess=new TasksDBAccess(this);

    private Long requestId;
    private BroadcastReceiver requestReceiver;
    private Button startStopWork;

    private WunderlistServiceHelper mWunderlistServiceHelper;

    long task_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tache_view);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        // getting attached intent data
        task_id = i.getLongExtra("task_id",0);

        // displaying selected product name
        tasksDBAccess.open();
        Logger.debug("task_id",String.valueOf(task_id));
        Task tache = tasksDBAccess.retrieveTodo(task_id);
        tasksDBAccess.close();

        TextView txtProduct = (TextView) findViewById(R.id.taskName);
        txtProduct.setText(tache.getTitle());

        startStopWork = (Button)findViewById(R.id.startWork);
        startStopWork.setOnClickListener(play);

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
                        Logger.debug(TAG, "On a bien reçu " + timersList.size() + " timers de la tâche " + resultRequestId);
                        String user = AuthorizationManager.getInstance(context).getUser();

                        ArrayAdapter<Timer> adapter = new MyTimersArrayAdapter(context, R.layout.list_item, timersList);
                        setListAdapter(adapter);

                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date parsedTimeStamp = null, firstDate = null, lastDate = null;
                        int totalWorkTime = 0;
                        for (Timer timer : timersList){
                            if (timer.getTimer() != null && !timer.getTimer().isEmpty()) totalWorkTime += Integer.valueOf(timer.getTimer());
                            try {
                                parsedTimeStamp = dateTimeFormat.parse("2014-08-22 15:02");
                            } catch (ParseException e) {e.printStackTrace();}
                            if (parsedTimeStamp != null){
                                if (firstDate == null) firstDate = (Date) parsedTimeStamp.clone();
                                else if(parsedTimeStamp.getTime() < firstDate.getTime()) firstDate = (Date) parsedTimeStamp.clone();
                                if (lastDate == null) lastDate = (Date) parsedTimeStamp.clone();
                                else if(parsedTimeStamp.getTime() > lastDate.getTime()) lastDate = (Date) parsedTimeStamp.clone();
                            }
                        }

                        TextView totalStartDate = (TextView) findViewById(R.id.totalWorkFirstDate);
                        TextView totalEndDate = (TextView) findViewById(R.id.totalWorkLastDate);
                        if (firstDate != null){
                            if (firstDate.getTime() != lastDate.getTime()){
                                totalStartDate.setText("du "+String.valueOf(dateFormat.format(lastDate.getTime())));
                                totalEndDate.setText("au "+String.valueOf(dateFormat.format(firstDate.getTime())));
                            }else
                                totalStartDate.setText("le "+String.valueOf(dateFormat.format(lastDate.getTime())));
                        }
                    }  else if(resultCode==401){
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
        if (requestId == null) requestId = mWunderlistServiceHelper.getTimers(task_id); // Si la requestId n'existe plus c'est un fail on relance.
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout:
                logoutAndFinish();
                break;
            case R.id.about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                break;
        }
        return false;
    }

    // Listener du bouton de la megafonction.
    private OnClickListener play = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView startStopBtn = (TextView) findViewById(R.id.startWork);

            if (startStopWork.getText().toString().equals("Start")){
                startStopBtn.setText("Stop");
                startStopBtn.setBackground(getResources().getDrawable(R.drawable.button_stop));
            }else{
                startStopBtn.setText("Start");
                startStopBtn.setBackground(getResources().getDrawable(R.drawable.button_play));
            }
        }
    };

    protected void logoutAndFinish(){
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }

}