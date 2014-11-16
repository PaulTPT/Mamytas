package mn.aug.restfulandroid.rest.resource;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mn.aug.restfulandroid.util.Logger;

/**
 * Created by Paul on 09/11/2014.
 */
public class Listw implements Resource,TaskList,Parcelable {


    public static final String LIST_ID_EXTRA ="list_id";
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="LIST";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);



    private long id = 0;
    private String title = "null";
    private int position=0;

    public Listw() {

    }

    @JsonCreator
    public Listw(@JsonProperty("id") long id,@JsonProperty("title") String title) {
        super();
        this.id = id;
        this.title = title;
        Logger.debug("list",toString());
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "&title=" + title;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeInt(position);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Listw createFromParcel(Parcel in) {
            return new Listw(in);
        }

        public Listw[] newArray(int size) {
            return new Listw[size];
        }
    };

    // Parcelling part
    public Listw(Parcel in) {
        this.id=in.readLong();
        this.title=in.readString();
        this.position=in.readInt();

    }



}
