package mn.aug.restfulandroid.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.service.WunderlistServiceHelper;
import mn.aug.restfulandroid.util.DateHelper;
import mn.aug.restfulandroid.util.DatePickerFragment;
import mn.aug.restfulandroid.util.Logger;
import mn.aug.restfulandroid.util.TimePickerFragment;

public class TaskEditor extends Activity {


    private  Button button;
    private EditText taskName, taskDueDate, taskDueTime ;
    private String toastVerb = "Création";
    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;
    private Context context;
    private Boolean edit=false;
    private Task task=null;
    private long task_id=0;
    private long list_id=0;
    private BroadcastReceiver requestReceiver;
    private int requestId=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        ownershipDBAccess = new OwnershipDBAccess(this);
        tasksDBAccess = new TasksDBAccess(this);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.context=this;
        Intent i = getIntent();
        task_id= i.getLongExtra(Task.TASK_ID_EXTRA,0L);
        list_id= i.getLongExtra(Listw.LIST_ID_EXTRA,0L);

        // Initialisation elem vue
        taskName = (EditText) findViewById(R.id.inputTaskName);
        taskDueDate = (EditText) findViewById(R.id.inputTaskDueDate);
        taskDueTime = (EditText) findViewById(R.id.inputTaskDueDateTime);
        button = (Button) findViewById(R.id.btnCreateTask);

        if(task_id!=0){
            tasksDBAccess.open();
            task= tasksDBAccess.retrieveTodo(task_id);
            tasksDBAccess.close();
            taskName.setText(task.getTitle());
            button.setText("Modifier la tâche");
            toastVerb = "Edition";
            try {
                taskDueDate.setText(DateHelper.getDateFromDate(task.getDue_date()));
                taskDueTime.setText(DateHelper.getTimeFromDateTime(task.getDue_date()));
            } catch (ParseException e) {showToast("Mauvais format pour la due date, heureusement que vous passez par là !");}
            edit=true;
        }else{
            button.setText("Ajouter la tache");
        }


        // button click event
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dueDate = null;
                try {
                    dueDate = DateHelper.getDateFromDate(taskDueDate.getText().toString());
                } catch (ParseException e) {showToast("Mauvais format pour la date");return;}
                try {
                    dueDate += " "+DateHelper.getTimeFromTime(taskDueTime.getText().toString());
                } catch (ParseException e) {showToast("Mauvais format pour l'heure");return;}

                Logger.debug("show", "Creating Product " + taskName.getText().toString() + " with due date: " + dueDate + " and list_id: "+list_id);
                showToast(toastVerb+" de la tâche " + taskName.getText().toString() + " avec pour échéance le : " + dueDate);
                if(!edit) {
                    ownershipDBAccess.open();
                    task = ownershipDBAccess.addTaskGetID(AuthorizationManager.getInstance(context).getUser(), new Task(taskName.getText().toString(), dueDate, list_id));
                    ownershipDBAccess.close();
                    mWunderlistServiceHelper.postTask(task);
                }else{
                    task.setTitle(taskName.getText().toString());
                    task.setDue_date(dueDate);
                    tasksDBAccess.open();
                    tasksDBAccess.updateTodo(task);
                    tasksDBAccess.close();
                    mWunderlistServiceHelper.putTask(task);
                }
                finish();
            }
        });



    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setTaskDueDate(taskDueDate);
        picker.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment picker = new TimePickerFragment();
        picker.setTaskDueTime(taskDueTime);
        picker.show(getFragmentManager(), "timePicker");
    }

    private void showToast(String message) {
        if (!isFinishing()) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}