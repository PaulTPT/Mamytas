package mn.aug.restfulandroid.rest.resource;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Paul on 10/11/2014.
 */
public class Timer implements Resource,Parcelable {
    private String name=null;
    private String timer=null;
    private String timer_start=null;
    private Long ownership_id=null;
    private Long task_id=null;


    /**
     * @param name
     * @param timer
     */
    @JsonCreator
    public Timer(@JsonProperty("name") String name,@JsonProperty("timer") String timer,@JsonProperty("timer_start") String timer_start,@JsonProperty("ownership_id") Long ownership_id,@JsonProperty("task_id") Long task_id) {
        this.name = name;
        this.timer = timer;
        this.timer_start = timer_start;
        this.ownership_id=ownership_id;
        this.task_id=task_id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getTimer_start() {
        return timer_start;
    }

    public void setTimer_start(String timer_start) {
        this.timer_start = timer_start;
    }

    public Long getOwnership_id() {
        return ownership_id;
    }

    public void setOwnership_id(Long ownership_id) {
        this.ownership_id= ownership_id;
    }

    public Long getTask_id() {
        return task_id;
    }

    public void setTask_id(Long task_id) {
        this.task_id= task_id;
    }






    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
      dest.writeString(timer);
      dest.writeString(timer_start);
      dest.writeLong(ownership_id);
      dest.writeLong(task_id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    // Parcelling part
    public Timer(Parcel in) {
        this.name=in.readString();
        this.timer=in.readString();
        this.timer_start=in.readString();
        this.ownership_id=in.readLong();
        this.task_id=in.readLong();
    }



}