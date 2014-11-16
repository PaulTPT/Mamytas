package mn.aug.restfulandroid.rest.resource;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;


/**
 * Facade representing the Tasks data
 *
 * @author hashbrown
 *
 */
public class Lists implements Resource {
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="LISTS";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);

    private List<Listw> lists;

    @JsonCreator
    public Lists(List<Listw> lists) {
        this.lists=lists;
    }

    public List<Listw> getLists() {

        return lists;
    }

}
