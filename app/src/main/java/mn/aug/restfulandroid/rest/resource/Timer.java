package mn.aug.restfulandroid.rest.resource;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Paul on 10/11/2014.
 */
public class Timer implements Resource,Parcelable {
    private String name=null;
    private String timer=null;

    /**
     * @param name
     * @param timer
     */
    @JsonCreator
    public Timer(String name, String timer) {
        this.name = name;
        this.timer = timer;
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

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
      dest.writeString(timer);
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
    }



}