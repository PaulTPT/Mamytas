package mn.aug.restfulandroid.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.rest.resource.Timer;

public class MyTimersArrayAdapter extends ArrayAdapter<Timer> {
    private final Context context;
    private final List<Timer> timerList;
    private final int layout;


    public MyTimersArrayAdapter(Context context,int layout, List<Timer> timerList ) {
        super(context, layout, timerList);
        this.context = context;
        this.timerList = timerList;
        this.layout=layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        textView.setText(timerList.get(position).getName()+": "+timerList.get(position).getTimer()+", le: "+timerList.get(position).getTimer_start());
        return rowView;
    }
}