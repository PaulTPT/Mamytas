package mn.aug.restfulandroid.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.rest.resource.Timer;

public class TimersArrayAdapter extends ArrayAdapter<Timer> {
    private final Context context;
    private final List<Timer> timerList;
    private final int layout;
    private final OnTouchListener listener;

    public TimersArrayAdapter(Context context, int layout, List<Timer> timerList) {
        super(context, layout, timerList);
        this.listener = new OnTouchListener();
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
        if(this.listener != null)
            rowView.setOnTouchListener(this.listener);
        return rowView;
    }

    public class OnTouchListener implements View.OnTouchListener {
        int initialX = 20;
        RelativeLayout front;
        TextView backBtn;

        public boolean onTouch(View view, MotionEvent event) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            backBtn = (TextView) view.findViewById(R.id.delete);
            front = (RelativeLayout) view.findViewById(R.id.front);
            int X = (int) event.getRawX();
            int offset = X - initialX;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                initialX = X;
                front.setTranslationX(0);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                front.setTranslationX(offset);

                if (offset < 0){
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_login_normal));
                    backBtn.setText("Editer");
                    backBtn.setGravity(Gravity.RIGHT);
                    //front.setBackgroundColor(0xffFF0000);
                }else{
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_stop_normal));
                    backBtn.setText("Retirer");
                    backBtn.setGravity(Gravity.LEFT);
                }
                if (offset > (int)width/2) {
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_stop_selected));
                } else if (offset < -(int)width/2) {
                    backBtn.setBackground(context.getResources().getDrawable(R.drawable.button_login_selected));
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ) {
                ValueAnimator animator = null;
                if (offset > (int)width/2 && event.getAction() != MotionEvent.ACTION_CANCEL) { // On supprime loulou
                    animator = ValueAnimator.ofInt(offset, width);
                } else if (offset < -(int)width/2 && event.getAction() != MotionEvent.ACTION_CANCEL) { // On redirige vers la page d'édition
                    animator = ValueAnimator.ofInt(offset, -width);
                } else{// Animate back if no action was performed.
                    animator = ValueAnimator.ofInt(X - initialX, 0);
                }
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        front.setTranslationX((Integer) valueAnimator.getAnimatedValue());
                    }
                });
                animator.setDuration(150);
                animator.start();
            }
            return true;
        }
    }
}