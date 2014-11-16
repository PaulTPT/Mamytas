package mn.aug.restfulandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulActivity;
import mn.aug.restfulandroid.activity.base.UndoBarController;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistService;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class ListsActivity extends RESTfulActivity implements UndoBarController.UndoListener {

    private static final String TAG = ListsActivity.class.getSimpleName();
    private Button newList;

    private UndoBarController mUndoBarController;
    private Long requestId=0L;
    private BroadcastReceiver requestReceiver;

    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;

    private  SwipeListView swipelistview;
    private  MyArrayAdapterList adapter;
    private  List<Listw> lists;
    private Context context=this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.home);
        super.onCreate(savedInstanceState);
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar), this);
        ownershipDBAccess = new OwnershipDBAccess(this);
        swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list);

        //These are the swipe listview settings. you can change these
        //setting as your requrement
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipelistview.setAnimationTime(50); // animarion time
        swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

        lists=new ArrayList<Listw>();
        adapter=new MyArrayAdapterList(this,R.layout.adapter,lists);
        swipelistview.setAdapter(adapter);

        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                Listw list=lists.get(position);
                list.setPosition(position);
                mUndoBarController.showUndoBar(
                        false,
                        "Liste supprimée",
                        list);
                lists.remove(position);
                adapter=new MyArrayAdapterList(context,R.layout.adapter,lists);
                swipelistview.setAdapter(adapter);

            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                // Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                //Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Listw item = (Listw) adapter.getItem(position);
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), TasksActivity.class);
                // sending data to new activity
                i.putExtra("list_id", item.getId());
                startActivity(i);

            }

            @Override
            public void onClickBackView(int position) {
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        // view products click event
        newList = (Button) findViewById(R.id.create);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new list activity
                //Intent i = new Intent(getApplicationContext(), ListEditor.class);
                //startActivity(i);
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

                Timers timers=(Timers) intent.getParcelableExtra(WunderlistService.RESOURCE_EXTRA);

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
                        ownershipDBAccess.open();
                        List<Listw> new_lists= ownershipDBAccess.getLists(user);
                        ownershipDBAccess.close();
                        lists=new_lists;
                        adapter=new MyArrayAdapterList(context,R.layout.adapter,lists);
                        swipelistview.setAdapter(adapter);
                        requestId=0L;


                    }  else if(resultCode==401){
                        showToast("Your session has expired");
                        logoutAndFinish();
                    }else{
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
            requestId = mWunderlistServiceHelper.getLists();
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
        requestId = mWunderlistServiceHelper.getLists();
    }

    protected void logoutAndFinish(){
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
        Listw list= (Listw) token;
        if (list!=null) {
            lists.add(list.getPosition(), list);
            adapter = new MyArrayAdapterList(context, R.layout.adapter, lists);
            swipelistview.setAdapter(adapter);
            mUndoBarController.clearUndoToken();
        }

    }

    @Override
    public void undoDisabled(Parcelable token) {
        Listw list= (Listw) token;
        if (list!=null){
            Logger.debug("Undo","undo disabled");
            mWunderlistServiceHelper.deleteList(list);
        }
    }
}
