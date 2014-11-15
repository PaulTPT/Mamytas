package mn.aug.restfulandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulActivity;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.util.Logger;

public class TaskEditor extends RESTfulActivity {

    EditText taskDueDate;
    EditText taskName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
    }

    @Override
    protected void refresh() {
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();

        // Edit Text
        taskName = (EditText) findViewById(R.id.inputTaskName);
        taskDueDate = (EditText) findViewById(R.id.inputTaskDueDate);

        // Create button
        Button btnCreateTask = (Button) findViewById(R.id.btnCreateTask);

        // button click event
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.debug("show", "Creating Product "+taskName.getText().toString()+" with due date: "+taskDueDate.getText().toString());
                showToast("Creating Product "+taskName.getText().toString()+" with due date: "+taskDueDate.getText().toString());

                // successfully created product
                Intent i = new Intent(getApplicationContext(), TasksActivity.class);
                startActivity(i);

                // closing this screen
                finish();
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void logoutAndFinish(){
        AuthorizationManager.getInstance(this).logout();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();

    }


}