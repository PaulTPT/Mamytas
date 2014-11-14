package mn.aug.restfulandroid.rest.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Paul on 09/11/2014.
 */
public class Listw implements Resource,TaskList {

    private long id = 0;
    private String title = "null";

    public Listw() {

    }

    @JsonCreator
    public Listw(@JsonProperty("id") long id,@JsonProperty("title") String title) {
        super();
        this.id = id;
        this.title = title;
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
}
