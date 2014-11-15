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

public class TaskEditor extends Activity {

    EditText taskDueDate;
    EditText taskName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

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
}