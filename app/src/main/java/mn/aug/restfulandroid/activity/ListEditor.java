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

public class ListEditor extends Activity {

    private EditText listName ;
    private Button btnCreateTask;
    private String toastVerb = "Creating";

    private WunderlistServiceHelper mWunderlistServiceHelper;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;
    private Context context;
    private Boolean edit=false;
    private Task list =null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list);
        ownershipDBAccess = new OwnershipDBAccess(this);
        tasksDBAccess = new TasksDBAccess(this);
        mWunderlistServiceHelper = WunderlistServiceHelper.getInstance(this);
        this.context=this;
        Intent i = getIntent();
        Long list_id= i.getLongExtra(Task.TASK_ID_EXTRA,0L);

        // Initialisation elem vue
        listName = (EditText) findViewById(R.id.inputListName);
        btnCreateTask = (Button) findViewById(R.id.btnCreateTask);

        if(list_id!=0){
            tasksDBAccess.open();
            list = tasksDBAccess.retrieveTodo(list_id);
            tasksDBAccess.close();
            listName.setText(list.getTitle());
            btnCreateTask.setText("Editer la liste");
            toastVerb = "Editing";
            edit = true;
        }else{
            btnCreateTask.setText("Ajouter la liste");
        }

        // Create button

        // button click event
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.debug("show", "Creating Product " + listName.getText().toString());
                showToast(toastVerb+" task " + listName.getText().toString());
                if(!edit) {
                    ownershipDBAccess.open();
                    list = ownershipDBAccess.addTaskGetID(AuthorizationManager.getInstance(context).getUser(), new Task(listName.getText().toString(), "", 0));
                    ownershipDBAccess.close();
                    mWunderlistServiceHelper.postTask(list);
                }else{
                    list.setTitle(listName.getText().toString());
                    tasksDBAccess.open();
                    tasksDBAccess.updateTodo(list);
                    tasksDBAccess.close();
                    mWunderlistServiceHelper.putTask(list);
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