package mn.aug.restfulandroid.rest.resource;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mn.aug.restfulandroid.util.Logger;


public class Task implements Resource, TaskList, Parcelable {

    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="ID";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);


    private long id;
    private String title;
    private String due_date;
    private long list_id;
    private String timer;

    @JsonCreator
    public Task(@JsonProperty("id") long id, @JsonProperty("title") String title, @JsonProperty("due_date") String due_date, @JsonProperty("list_id") long list_id, @JsonProperty("timer") String timer) {
        this.id = id;
        this.title = title;
        this.due_date = due_date;
        this.list_id = list_id;
        this.timer = timer;

        Logger.debug("task", toString());
    }


    public Task(long id, String title, String due_date, long list_id) {
        this.id = id;
        this.title = title;
        this.due_date = due_date;
        this.list_id = list_id;

        Logger.debug("task", toString());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public long getList_id() {
        return list_id;
    }

    public void setList_id(long list_id) {
        this.list_id = list_id;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "&title=" + title +
                "&due_date='" + due_date +
                "&list_id=" + list_id +
                "&timer='" + timer ;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    // Parcelling part
    public Task(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = Long.parseLong(data[0]);
        this.title = data[1];
        this.due_date = data[2];
        this.list_id = Long.parseLong(data[3]);
        this.timer = data[4];
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(this.id),
                this.title,
                this.due_date,
                String.valueOf(this.list_id),
                this.timer
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

}
