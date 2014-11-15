package mn.aug.restfulandroid.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import mn.aug.restfulandroid.R;

public abstract class RESTfulActivity extends Activity {

    private int mContentResId;
    private MenuItem refreshingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(mContentResId);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Mamytas");
      //  actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
               //| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.show();

    }
    protected abstract void refresh();

    protected void setContentResId(int id) {
        mContentResId = id;
    }

    protected void startRefreshing() {
        refreshingItem.setActionView(R.layout.progress_bar);
        refreshingItem.expandActionView();

    }

    protected void stopRefreshing() {
        refreshingItem.collapseActionView();
        refreshingItem.setActionView(null);

    }

    protected void setRefreshingItem(MenuItem item){
        this.refreshingItem=item;
    }

}
