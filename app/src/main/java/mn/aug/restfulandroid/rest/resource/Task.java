package mn.aug.restfulandroid.rest.resource;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mn.aug.restfulandroid.util.Logger;


public class Task implements Resource, TaskList, Parcelable {

    public static final String TASK_ID_EXTRA ="task_id";
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="TASK";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);


    private long id=0;
    private String title;
    private String due_date;
    private long list_id;
    private int position=0;

    @JsonCreator
    public Task(@JsonProperty("id") long id, @JsonProperty("title") String title, @JsonProperty("due_date") String due_date, @JsonProperty("list_id") long list_id) {
        this.id = id;
        this.title = title;
        this.due_date = due_date;
        this.list_id = list_id;

        Logger.debug("task", toString());
    }

    public Task(String title,String due_date, long list_id) {
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "&title=" + title +
                "&due_date=" + due_date +
                "&list_id=" + list_id ;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(due_date);
        dest.writeLong(list_id);
        dest.writeInt(position);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    // Parcelling part
    public Task(Parcel in) {
        this.id=in.readLong();
        this.title=in.readString();
        this.due_date=in.readString();
        this.list_id=in.readLong();
        this.position=in.readInt();

    }



}
