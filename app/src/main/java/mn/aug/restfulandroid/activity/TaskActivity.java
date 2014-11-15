package mn.aug.restfulandroid.activity;

import android.app.Activity;
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
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        Task todo = tasksDBAccess.retrieveTodo(task_id);
        tasksDBAccess.close();

        TextView txtProduct = (TextView) findViewById(R.id.taskName);
        txtProduct.setText(todo.getTitle());


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
                        String user = AuthorizationManager.getInstance(context).getUser();

                        Logger.debug(TAG, "On a bien reçu " + timers.getTimers().size() + " timers de la tâche " + resultRequestId);

                        ArrayAdapter<Timer> adapter = new MyTimersArrayAdapter(context, android.R.layout.simple_list_item_1, timersList);
                        setListAdapter(adapter);

                        //TextView txtProduct = (TextView) findViewById(R.id.taskName);
                        //txtProduct.setText(timers.getTimers().get(0).getName());

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


    protected void logoutAndFinish(){
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }

}