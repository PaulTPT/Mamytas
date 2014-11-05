package mn.aug.restfulandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulListActivity;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class TasksActivity extends RESTfulListActivity {

	private static final String TAG = TasksActivity.class.getSimpleName();

	private Long requestId;
	private BroadcastReceiver requestReceiver;

	private WunderlistServiceHelper mWunderlistServiceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentResId(R.layout.home);
		setRefreshable(true);

		super.onCreate(savedInstanceState);
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

				Logger.debug(TAG, "Received intent " + intent.getAction() + ", request ID "
						+ resultRequestId);

				if (resultRequestId == requestId) {

					Logger.debug(TAG, "Result is for our request ID");
					
					setRefreshing(false);

					int resultCode = intent.getIntExtra(WunderlistServiceHelper.EXTRA_RESULT_CODE, 0);

					Logger.debug(TAG, "Result code = " + resultCode);

					if (resultCode == 200) {

						Logger.debug(TAG, "Updating UI with new data");

/*
						String name = getNameFromContentProvider();
						showNameToast(name);
*/

					} else {
						showToast(getString(R.string.error_occurred));
					}
				} else {
					Logger.debug(TAG, "Result is NOT for our request ID");
				}

			}
		};

		mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
		this.registerReceiver(requestReceiver, filter);

		if (requestId == null) {
			setRefreshing(true);
			requestId = mWunderlistServiceHelper.getTasks();
		} else if (mWunderlistServiceHelper.isRequestPending(requestId)) {
			setRefreshing(true);
		} else {
			setRefreshing(false);
			/*name = getNameFromContentProvider();
			showNameToast(name);*/
		}

	}

/*	private String getNameFromContentProvider() {

		String name = null;

		Cursor cursor = getContentResolver().query(ProfileConstants.CONTENT_URI, null, null, null, null);

		if (cursor.moveToFirst()) {
			int index = cursor.getColumnIndexOrThrow(ProfileConstants.NAME);
			name = cursor.getString(index);
		}

		cursor.close();

		return name;
	}*/

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
			AuthorizationManager.getInstance(this).logout();
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			finish();
			break;
		case R.id.about:
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			break;
		}
		return false;
	}

	@Override
	protected void refresh() {
		requestId = mWunderlistServiceHelper.getTasks();
	}



}
