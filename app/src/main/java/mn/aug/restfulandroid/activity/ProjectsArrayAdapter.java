package mn.aug.restfulandroid.activity;

/**
 * Created by Paul on 13/11/2014.
 */

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.util.Logger;

public class ProjectsArrayAdapter extends ArrayAdapter<Listw> {
    private final Context contextProjects;
    private final List<Listw> lists;
    private final int layout;

    public ProjectsActivity.OnTouchListener listener;

    private long projectID;
    private int pos;

    public ProjectsArrayAdapter(Context contextProjects, int layout, List<Listw> lists, ProjectsActivity.OnTouchListener _listener) {
        super(contextProjects, layout, lists);
        this.contextProjects = contextProjects;
        this.lists=lists;
        this.layout=layout;
        this.listener = _listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder holder = null;
        View row = convertView;
        holder = null;
        //projectID = lists.get(position).getId();
        this.pos = position;

        if(holder == null) {
            if(row==null) {
                LayoutInflater inflater = ((Activity) contextProjects).getLayoutInflater();
                row = inflater.inflate(layout, parent, false);
            }
            holder = new RowHolder();

            holder.name = (TextView)row.findViewById(R.id.name);
            holder.front = (RelativeLayout)row.findViewById(R.id.front);
            holder.btnEditTask = (Button)row.findViewById(R.id.editBtn);
            row.setTag(holder);
        } else
            holder = (RowHolder)row.getTag();
        holder.name.setText(lists.get(position).getTitle());
        holder.projectID = lists.get(position).getId();
        projectID = lists.get(position).getId();
        Logger.debug("projectID","projectID:"+position);
                Logger.debug("position","position:"+position);
        holder.front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching new Activity on selecting single List Item
                Intent i = new Intent((Activity) contextProjects, TasksActivity.class);
                // sending data to new activity
                Logger.debug("projectID","projectID:"+projectID);
                i.putExtra("list_id", projectID);
                contextProjects.startActivity(i);
            }
        });
        holder.btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new task activity
                Intent i = new Intent((Activity) contextProjects, ProjectEditor.class);
                i.putExtra(Listw.LIST_ID_EXTRA, projectID);
                contextProjects.startActivity(i);
            }
        });

        if(this.listener != null){
            this.listener.setPosition(position);
            row.setOnTouchListener(this.listener);
        }

        return row;
    }

    static class RowHolder{
        TextView name;
        public RelativeLayout front;
        public Button btnEditTask;
        public long projectID;
    }

}