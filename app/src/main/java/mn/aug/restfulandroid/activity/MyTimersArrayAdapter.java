package mn.aug.restfulandroid.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
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
        TextView workUserName = (TextView) rowView.findViewById(R.id.workUserName);
        TextView workTimeSpent = (TextView) rowView.findViewById(R.id.workTimeSpent);
        TextView workDate = (TextView) rowView.findViewById(R.id.workFirstDate);
        workUserName.setText(timerList.get(position).getName()+": ");
        workTimeSpent.setText(timerList.get(position).getTimer() + "min");
        workDate.setText("le " + timerList.get(position).getTimer_start());
        return rowView;
    }
}