package mn.aug.restfulandroid.rest.resource;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mn.aug.restfulandroid.util.Logger;


public class Task implements Resource, TaskList {

    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="TASK";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);


    private long id;
    private String title;
    private String due_date;
    private long list_id;
    private String timer;
    public String timer_start;

    @JsonCreator
    public Task(@JsonProperty("id") long id, @JsonProperty("title") String title, @JsonProperty("due_date") String due_date, @JsonProperty("list_id") long list_id, @JsonProperty("timer") String timer, @JsonProperty("timer_start") String timer_start) {
        this.id = id;
        this.title = title;
        this.due_date = due_date;
        this.list_id = list_id;
        this.timer = timer;
        this.timer_start = timer_start;

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

    public String getTimer_start() {
        return timer_start;
    }

    public void setTimer_start(String timer_start) {
        this.timer_start = timer_start;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "&title=" + title +
                "&due_date='" + due_date +
                "&list_id=" + list_id +
                "&timer='" + timer ;
    }



}
