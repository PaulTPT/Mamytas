package mn.aug.restfulandroid.activity;

/**
 * Created by Paul on 13/11/2014.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.rest.resource.Task;

public class MyArrayAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final List<Task> todos;
    private final int layout;


    public MyArrayAdapter(Context context,int layout, List<Task> todos ) {
        super(context, layout, todos);
        this.context = context;
        this.todos=todos;
        this.layout=layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        textView.setText(todos.get(position).getTitle());
        return rowView;
    }
}