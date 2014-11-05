package mn.aug.restfulandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulActivity;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class LoginActivity extends RESTfulActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Long requestId;
    private BroadcastReceiver requestReceiver;

    private WunderlistServiceHelper mWunderlistServiceHelper;

	private AuthorizationManager mOAuthManager;

	private Button mButtonLogin;
    private EditText mEMailText;
    private EditText mPasswordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentResId(R.layout.login);

		super.onCreate(savedInstanceState);

		mOAuthManager = AuthorizationManager.getInstance(this);
        mEMailText=(EditText) findViewById(R.id.email);
        mPasswordText=(EditText) findViewById(R.id.password);

		mButtonLogin = (Button) findViewById(R.id.button_login);
		mButtonLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mButtonLogin.setVisibility(View.INVISIBLE);
                loggin(mEMailText.getText().toString(),mPasswordText.getText().toString());
			}
		});

	}
	private void startHomeActivity() {
		Intent startHomeActivity = new Intent(this, TasksActivity.class);
		startActivity(startHomeActivity);
		finish();
	}

	/**
	 * Authorizes app for use with Wunderlist.
	 */
	void loggin(String email,String password ) {
		//Get name and password and retrieve token




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

                        Logger.debug(TAG, "Loggin Succesfull");

						showToast("Login Succesfull !");
                        startHomeActivity();


                    } else {
                        showToast(getString(R.string.error_occurred));
                        mButtonLogin.setVisibility(View.VISIBLE);
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
            requestId = mWunderlistServiceHelper.login(email,password);
        } else if (mWunderlistServiceHelper.isRequestPending(requestId)) {
            setRefreshing(true);
        } else {
            setRefreshing(false);
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check to see if we're resuming after having authenticated with
		// Twitter
		if (getIntent().getData() != null) {


			if (mOAuthManager.loggedIn()) {
				startHomeActivity();
			} else {
				Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
				mButtonLogin.setVisibility(View.VISIBLE);
			}
		} else {
			// No Intent with a callback Uri was found, so let's just see if
			// we've already logged in, and if so start the Home activity
			if (mOAuthManager.loggedIn()) {
				startHomeActivity();
			} else {
				mButtonLogin.setVisibility(View.VISIBLE);
			}
		}
	}



	@Override
	protected void refresh() {
		// n/a
	}


    private void showToast(String message) {
        if (!isFinishing()) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    protected void onPause(){
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




}
