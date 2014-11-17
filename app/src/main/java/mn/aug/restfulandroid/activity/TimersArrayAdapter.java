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

public class TimersArrayAdapter extends ArrayAdapter<Timer> {
    private final Context context;
    private final List<Timer> timerList;
    private final int layout;
    private final View.OnTouchListener listener;

    public TimersArrayAdapter(Context context, int layout, List<Timer> timerList, View.OnTouchListener _listener) {
        super(context, layout, timerList);
        this.listener = _listener;
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
        //if(this.listener != null)
           // rowView.setOnTouchListener(this.listener);
        return rowView;
    }


}