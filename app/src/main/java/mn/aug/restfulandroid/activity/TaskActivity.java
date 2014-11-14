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
        String task_name = i.getStringExtra("task_name");
        // displaying selected product name
        txtProduct.setText(task_name);

    }
}