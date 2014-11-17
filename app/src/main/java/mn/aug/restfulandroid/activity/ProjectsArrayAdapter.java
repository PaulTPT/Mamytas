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
            viewHolder.front.setTag(m);

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