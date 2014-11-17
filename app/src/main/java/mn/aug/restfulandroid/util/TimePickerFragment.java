package mn.aug.restfulandroid.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Antoine on 16/11/2014.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private EditText taskDueTime;

    public void setTaskDueTime(EditText taskDueTime){
        this.taskDueTime = taskDueTime;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        try {
            this.taskDueTime.setText(DateHelper.getTimeFromTime(hourOfDay + ":" + minute));
        } catch (ParseException e) { /* Erreur format qui n'arrivera pas ici normalement */}
    }
}