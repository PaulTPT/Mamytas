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
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.rest.resource.Task;

public class TasksArrayAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final List<Task> todos;
    private final int layout;


    public TasksArrayAdapter(Context context, int layout, List<Task> todos) {
        super(context, layout, todos);
        this.context = context;
        this.todos=todos;
        this.layout=layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder holder = null;
        View row = convertView;

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
        holder.name.setText(todos.get(position).getTitle());
        return row;
    }

    static class RowHolder{ TextView name; }
}