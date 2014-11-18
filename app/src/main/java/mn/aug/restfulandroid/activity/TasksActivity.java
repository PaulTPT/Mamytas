package mn.aug.restfulandroid.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulActivity;
import mn.aug.restfulandroid.activity.base.UndoBarController;
import mn.aug.restfulandroid.provider.ListsDBAccess;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistService;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class TasksActivity extends RESTfulActivity implements UndoBarController.UndoListener {

    private static final String TAG = TasksActivity.class.getSimpleName();
    private Button newTask;

    private UndoBarController mUndoBarController;
    private Long requestId = 0L;
    private BroadcastReceiver requestReceiver;

    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;
    private ListsDBAccess listsDBAccess;
    private Long list_id;
    private TextView project_name_view;

    private ListView tasksListView;
    private TasksArrayAdapter adapter;
    private List<Task> tasks;
    private Context context = this;
    private OnTouchListener gestureListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.list_tasks);
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        list_id = i.getLongExtra(Listw.LIST_ID_EXTRA, 0L);
        mUndoBarController = new UndoBarController(findViewById(R.id.actionBar), this);
        ownershipDBAccess = new OwnershipDBAccess(this);
        tasksDBAccess = new TasksDBAccess(this);
        listsDBAccess=new ListsDBAccess(this);
        listsDBAccess.open();
        String project_name = listsDBAccess.retrieveList(list_id).getTitle();
        listsDBAccess.close();
        project_name_view=(TextView) findViewById(R.id.projectName);
        project_name_view.setText("Taches du projet "+project_name);

        tasks = new ArrayList<Task>();
        tasksListView = (ListView)findViewById(R.id.list);
        gestureListener = new OnTouchListener();
        adapter = new TasksArrayAdapter(this, R.layout.list_task_item, tasks, gestureListener);
        tasksListView.setAdapter(adapter);

        // view products click event
        newTask = (Button) findViewById(R.id.create);
        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new task activity
                Intent i = new Intent(getApplicationContext(), TaskEditor.class);
                i.putExtra(Listw.LIST_ID_EXTRA, list_id);
                startActivity(i);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();

/*		String name = getNameFromContentProvider();
        if (name != null) {
			showNameToast(name);
		}*/

		/*
         * 1. Register for broadcast from WunderlistServiceHelper
		 * 
		 * 2. See if we've already made a request. a. If so, check the status.
		 * b. If not, make the request (already coded below).
		 */

        IntentFilter filter = new IntentFilter(WunderlistServiceHelper.ACTION_REQUEST_RESULT);
        requestReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                long resultRequestId = intent
                        .getLongExtra(WunderlistServiceHelper.EXTRA_REQUEST_ID, 0);

                Timers timers = (Timers) intent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);

                Logger.debug(TAG, "Received intent " + intent.getAction() + ", request ID "
                        + resultRequestId);

                if (resultRequestId == requestId) {

                    Logger.debug(TAG, "Result is for our request ID");

                    int resultCode = intent.getIntExtra(WunderlistServiceHelper.EXTRA_RESULT_CODE, 0);

                    Logger.debug(TAG, "Result code = " + resultCode);

                    if (resultCode == 200) {
                        stopRefreshing();
                        Logger.debug(TAG, "Updating UI with new data");
                        String user = AuthorizationManager.getInstance(context).getUser();
                        tasksDBAccess.open();
                        List<Task> todos = tasksDBAccess.retrieveTodosInstancesFromList(list_id);
                        Logger.debug("list id :", String.valueOf(list_id));
                        tasksDBAccess.close();
                        tasks = todos;
                        adapter = new TasksArrayAdapter(context, R.layout.list_task_item, tasks, gestureListener);
                        tasksListView.setAdapter(adapter);
                        requestId = 0L;


                    } else if (resultCode == 401) {
                        showToast("Your session has expired");
                        logoutAndFinish();
                    } else {
                        showToast("Connexion to the server failed");
                        logoutAndFinish();
                    }
                } else {
                    Logger.debug(TAG, "Result is NOT for our request ID");
                }

            }
        };

        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.registerReceiver(requestReceiver, filter);

        if (requestId == 0) {
            requestId = mWunderlistServiceHelper.getTasks(list_id);
            startRefreshing();
        }

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
    protected void refresh() {
        requestId = mWunderlistServiceHelper.getTasks(list_id);
    }

    protected void logoutAndFinish() {
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    @Override
    public void onUndo(Parcelable token) {
        Task task = (Task) token;
        if (task != null) {
            tasks.add(task.getPosition(), task);
            adapter = new TasksArrayAdapter(context, R.layout.list_task_item, tasks, gestureListener);
            tasksListView.setAdapter(adapter);
            mUndoBarController.clearUndoToken();
        }

    }

    @Override
    public void undoDisabled(Parcelable token) {
        Task task = (Task) token;
        if (task != null) {
            Logger.debug("Undo", "undo disabled");
            mWunderlistServiceHelper.deleteTask(task);
        }
    }

    public class OnTouchListener implements View.OnTouchListener {
        int initialX = 0;
        RelativeLayout front;
        TextView backBtn;

        private TasksArrayAdapter.RowHolder holder;
        private int position;

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean onTouch(View view, MotionEvent event) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            int X = (int) event.getRawX();
            int offset = X - initialX;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                holder = ((TasksArrayAdapter.RowHolder) view.getTag());
                setPosition(holder.position);
                backBtn = (TextView) view.findViewById(R.id.delete);
                front = (RelativeLayout) view.findViewById(R.id.front);
                front.setBackgroundColor(0xFFE9E9F3);
                initialX = X;
                front.setTranslationX(0);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                front.setTranslationX(offset);

                if (offset < 0){
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_login_normal));
                    backBtn.setText("Editer");
                    backBtn.setGravity(Gravity.RIGHT);
                }else{
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_stop_normal));
                    backBtn.setText("Retirer");
                    backBtn.setGravity(Gravity.LEFT);
                }
                if (offset > (int)width/3) {
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_stop_selected));
                } else if (offset < -(int)width/3) {
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_login_selected));
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ) {
                ValueAnimator animator = null;
                if (offset > (int)width/3 && event.getAction() != MotionEvent.ACTION_CANCEL) { // On supprime loulou
                    animator = ValueAnimator.ofInt(offset, width);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            front.setTranslationX((Integer) valueAnimator.getAnimatedValue());
                        }
                    });
                    animator.setDuration(150);
                    animator.start();

                    Task task = tasks.get(position);
                    task.setPosition(position);
                    mUndoBarController.showUndoBar(
                            false,
                            "Tâche supprimée",
                            task);
                    tasks.remove(position);
                    adapter = new TasksArrayAdapter(context, R.layout.list_task_item, tasks, gestureListener);
                    tasksListView.setAdapter(adapter);
                } else if (offset < -(int)width/3 && event.getAction() != MotionEvent.ACTION_CANCEL) { // On redirige vers la page d'édition
                    animator = ValueAnimator.ofInt(offset, -width);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            front.setTranslationX((Integer) valueAnimator.getAnimatedValue());
                        }
                    });
                    animator.setDuration(150);
                    animator.start();

                    Task item = (Task) adapter.getItem(position);
                    // Launching new Activity on selecting single List Item
                    Intent i = new Intent(getApplicationContext(), TaskEditor.class);
                    // sending data to new activity
                    i.putExtra(Task.TASK_ID_EXTRA, item.getId());
                    i.putExtra(Listw.LIST_ID_EXTRA, item.getList_id());
                    startActivity(i);
                } else{// Animate back if no action was performed.
                    animator = ValueAnimator.ofInt(offset, 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            front.setTranslationX((Integer) valueAnimator.getAnimatedValue());
                        }
                    });
                    animator.setDuration(150);
                    animator.start();
                }
                if (Math.abs(offset)<2){
                    // Launching new Activity on selecting single List Item
                    Intent i = new Intent((Activity) context, TaskActivity.class);
                    // sending data to new activity
                    Task item = (Task) adapter.getItem(position);
                    i.putExtra(Task.TASK_ID_EXTRA, item.getId());
                    i.putExtra(Listw.LIST_ID_EXTRA, item.getList_id());
                    context.startActivity(i);
                }
                front.setBackgroundColor(0xFFF5F5FF);
            }
            return true;
        }
    }
}
