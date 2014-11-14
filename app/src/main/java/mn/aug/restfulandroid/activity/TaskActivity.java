package mn.aug.restfulandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import mn.aug.restfulandroid.R;

public class TaskActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tache_view);

        TextView txtProduct = (TextView) findViewById(R.id.taskName);

        Intent i = getIntent();
        // getting attached intent data
        Long task_id = i.getLongExtra("task_id",0);
        // displaying selected product name
        txtProduct.setText(String.valueOf(task_id));

    }
}