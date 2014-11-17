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
    private final LayoutInflater inflator;

    public ProjectsActivity.OnTouchListener listener;

    public ProjectsArrayAdapter(Context contextProjects, int layout, List<Listw> lists, ProjectsActivity.OnTouchListener _listener) {
        super(contextProjects, layout, lists);
        this.contextProjects = contextProjects;
        this.lists=lists;
        this.layout=layout;
        this.listener = _listener;
        inflator = ((Activity) contextProjects).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(position > lists.size())
            return null;
        Listw m = lists.get(position);
        final RowHolder viewHolder = new RowHolder();
        RowHolder Holder = null;
        if (convertView == null) {

            view = inflator.inflate(layout, parent, false);

            view.setTag(viewHolder);

            viewHolder.name = (TextView)view.findViewById(R.id.name);
            viewHolder.front = (RelativeLayout)view.findViewById(R.id.front);
            viewHolder.btnEditTask = (Button)view.findViewById(R.id.editBtn);
            viewHolder.front.setTag(m);
            viewHolder.btnEditTask.setTag(m);

            viewHolder.position = position;

            Holder = viewHolder;
        } else {
            view = convertView;
            Holder = ((RowHolder) view.getTag());
        }

        if(this.listener != null)
            view.setOnTouchListener(this.listener);

        Holder.model = m;
        Holder.position = position;
        Holder.name.setText(m.getTitle());
        return view;

        /*
        RowHolder holder = null;
        View row = convertView;
        holder = null;

        if(holder == null) {
            if(row==null) {
                row = inflator.inflate(layout, parent, false);
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
        holder.position = position;

        if(this.listener != null){
            row.setOnTouchListener(this.listener);
        }//*/

        /*
        holder.front.setTag(1,position);
        holder.front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer)view.getTag(1);
                // Launching new Activity on selecting single List Item
                Intent i = new Intent((Activity) contextProjects, TasksActivity.class);
                // sending data to new activity
                Logger.debug("positionOnclick","position:"+position);
                i.putExtra("list_id", lists.get(position).getId());
                contextProjects.startActivity(i);
            }
        });
        holder.btnEditTask.setTag(1,position);
        holder.btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer)view.getTag(1);
                // Launching create new task activity
                Intent i = new Intent((Activity) contextProjects, ProjectEditor.class);
                i.putExtra(Listw.LIST_ID_EXTRA, lists.get(position).getId());
                contextProjects.startActivity(i);
            }
        });//*/

        //return row;
    }

    public static class RowHolder{
        public Listw model;

        public void RowHolder(){

        }

        TextView name;
        public RelativeLayout front;
        public Button btnEditTask;
        public long projectID;
        public int position;
    }

}