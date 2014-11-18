package mn.aug.restfulandroid.activity;

/**
 * Created by Paul on 13/11/2014.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;

public class TasksArrayAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final List<Task> tasks;
    private final int layout;
    private final LayoutInflater inflator;
    public TasksActivity.OnTouchListener listener;

    public TasksArrayAdapter(Context context, int layout, List<Task> tasks, TasksActivity.OnTouchListener _listener) {
        super(context, layout, tasks);
        this.context = context;
        this.tasks =tasks;
        this.layout=layout;
        this.listener = _listener;
        inflator = ((Activity) context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(position > tasks.size())
            return null;
        Task m = tasks.get(position);
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
        public Task model;
        public TextView name;
        public RelativeLayout front;
        public int position;
    }
}