package mn.aug.restfulandroid.activity;

/**
 * Created by Paul on 13/11/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.util.Logger;

public class ProjectsArrayAdapter extends ArrayAdapter<Listw> {
    private final Context context;
    private final List<Listw> lists;
    private final int layout;

    private long projectID;

    public ProjectsArrayAdapter(Context context, int layout, List<Listw> lists) {
        super(context, layout, lists);
        this.context = context;
        this.lists=lists;
        this.layout=layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder holder = null;
        View row = convertView;
        holder = null;
        projectID = lists.get(position).getId();

        if(holder == null) {
            if(row==null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layout, parent, false);
            }
            holder = new RowHolder();

            holder.name = (TextView)row.findViewById(R.id.name);
            row.setTag(holder);
        } else
            holder = (RowHolder)row.getTag();
        holder.name.setText(lists.get(position).getTitle());
        Button btnEditTask = (Button) row.findViewById(R.id.editBtn);
        btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new task activity
                Intent i = new Intent((Activity) context, ProjectEditor.class);
                i.putExtra(Listw.LIST_ID_EXTRA, projectID);
                context.startActivity(i);
            }
        });

        return row;
    }

    static class RowHolder{ TextView name; }
}