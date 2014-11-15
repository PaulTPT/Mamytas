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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mn.aug.restfulandroid.R;
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


        RowHolder holder = null;
        View row = convertView;
        holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);

            holder = new RowHolder();

            holder.name = (TextView)row.findViewById(R.id.name);
            holder.button1=(Button)row.findViewById(R.id.swipe_button1);
            holder.button2=(Button)row.findViewById(R.id.swipe_button2);
            holder.button3=(Button)row.findViewById(R.id.swipe_button3);
            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }

        holder.name.setText(todos.get(position).getTitle());

        holder.button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        holder.button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 3 Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }


    static class RowHolder{

        TextView name;
        Button button1;
        Button button2;
        Button button3;
    }

}