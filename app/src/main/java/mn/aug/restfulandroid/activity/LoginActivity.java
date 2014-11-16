package mn.aug.restfulandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
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

    private IntentFilter filter;

    private AuthorizationManager mOAuthManager;

    private Button mButtonLogin;
    private EditText mEMailText;
    private EditText mPasswordText;
    private String name = null;
    private String password = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentResId(R.layout.login);
        super.onCreate(savedInstanceState);

        mOAuthManager = AuthorizationManager.getInstance(this);
        mEMailText = (EditText) findViewById(R.id.email);
        mPasswordText = (EditText) findViewById(R.id.password);

        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mButtonLogin.setVisibility(View.INVISIBLE);
                name = mEMailText.getText().toString();
                password = mPasswordText.getText().toString();
                loggin(name, password);
            }
        });



        requestReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                long resultRequestId = intent
                        .getLongExtra(WunderlistServiceHelper.EXTRA_REQUEST_ID, 0);

                Logger.debug(TAG, "Received intent " + intent.getAction() + ", request ID "
                        + resultRequestId);

                if (resultRequestId == requestId) {

                    Logger.debug(TAG, "Result is for our request ID");


                    int resultCode = intent.getIntExtra(WunderlistServiceHelper.EXTRA_RESULT_CODE, 0);

                    Logger.debug(TAG, "Result code = " + resultCode);

                    if (resultCode == 200) {
                        stopRefreshing();
                        Logger.debug(TAG, "Loggin Succesfull");
                        showToast("Login Succesfull !");



                        startHomeActivity();


                    } else if (resultCode == 401) {
                        showToast("Login failed... Try again");
                        mButtonLogin.setVisibility(View.VISIBLE);

                    } else {
                        showToast("The connexion with the server failed");
                        mButtonLogin.setVisibility(View.VISIBLE);

                    }
                } else {
                    Logger.debug(TAG, "Result is NOT for our request ID");
                }

            }
        };

        filter = new IntentFilter(WunderlistServiceHelper.ACTION_REQUEST_RESULT);

        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.logout).setVisible(false);
        setRefreshingItem(menu.findItem(R.id.refresh));
        return true;
    }

    @Override
    protected void refresh() {

    }

    private void startHomeActivity() {
        Intent startHomeActivity = new Intent(this, ListsActivity.class);
        startActivity(startHomeActivity);
        finish();
    }

    /**
     * Authorizes app for use with Wunderlist.
     */
    void loggin(String name, String password) {


        //Get name and password and retrieve token

        requestId = mWunderlistServiceHelper.login(name, password);
        startRefreshing();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(requestReceiver, filter);

        if (mOAuthManager.loggedIn()) {
            startHomeActivity();
        } else {
            mButtonLogin.setVisibility(View.VISIBLE);
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





}
