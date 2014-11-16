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
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.Logger;

public class TaskEditor extends Activity {

    private  EditText taskDueDate;
    private  EditText taskName;
    private  Button button;
    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;
    private Context context;
    private Boolean edit=false;
    private Task task=null;
    private long list_id=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        ownershipDBAccess = new OwnershipDBAccess(this);
        tasksDBAccess = new TasksDBAccess(this);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.context=this;
        Intent i = getIntent();
        Long task_id= i.getLongExtra(Task.TASK_ID_EXTRA,0L);

        // Edit Text
        taskName = (EditText) findViewById(R.id.inputTaskName);
        taskDueDate = (EditText) findViewById(R.id.inputTaskDueDate);

        if(task_id!=0){
            tasksDBAccess.open();
            task= tasksDBAccess.retrieveTodo(task_id);
            tasksDBAccess.close();
            taskName.setText(task.getTitle());
            taskDueDate.setText(task.getDue_date());
            button.setText("Modifier la tâche");
            edit=true;
        }



        // Create button
        button = (Button) findViewById(R.id.btnCreateTask);

        // button click event
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.debug("show", "Creating Product "+taskName.getText().toString()+" with due date: "+taskDueDate.getText().toString());
                showToast("Creating Product "+taskName.getText().toString()+" with due date: "+taskDueDate.getText().toString());
                if(!edit) {
                    ownershipDBAccess.open();
                    task = ownershipDBAccess.addTaskGetID(AuthorizationManager.getInstance(context).getUser(), new Task(taskName.getText().toString(), taskDueDate.getText().toString(), list_id));
                    ownershipDBAccess.close();
                    mWunderlistServiceHelper.postTask(task);
                }else{
                    task.setTitle(taskName.getText().toString());
                    task.setDue_date(taskDueDate.getText().toString());
                    tasksDBAccess.open();
                    tasksDBAccess.updateTodo(task);
                    tasksDBAccess.close();
                    mWunderlistServiceHelper.putTask(task);
                }
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