package mn.aug.restfulandroid.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Facade representing the Tasks data
 *
 * @author hashbrown
 *
 */
public class Timers implements Resource,Parcelable {
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="TIMERS";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);

    private List<Timer> timers;

    @JsonCreator
    public Timers(List<Timer> timers) {
        this.timers=timers;
    }

    public List<Timer> getTimers() {

        return timers;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeList(timers);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Timers createFromParcel(Parcel in) {
            return new Timers(in);
        }

        public Timers[] newArray(int size) {
            return new Timers[size];
        }
    };

    // Parcelling part
    public Timers(Parcel in) {
        in.readList(this.timers,Timers.class.getClassLoader());

    }


}
