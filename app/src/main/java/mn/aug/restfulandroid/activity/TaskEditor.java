package mn.aug.restfulandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class TaskEditor extends Activity {

    private  EditText taskDueDate;
    private  EditText taskName;
    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        ownershipDBAccess = new OwnershipDBAccess(this);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.context=this;
        Intent i = getIntent();
        String title= i.getStringExtra(Task.TITLE_EXTRA);
        String due_date =i.getStringExtra(Task.DUE_DATE_EXTRA);

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
                ownershipDBAccess.open();
                Task task =ownershipDBAccess.addTaskGetID(AuthorizationManager.getInstance(context).getUser(),new Task(taskName.getText().toString(),taskDueDate.getText().toString(),0));
                ownershipDBAccess.close();
                mWunderlistServiceHelper.postTask(task);
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