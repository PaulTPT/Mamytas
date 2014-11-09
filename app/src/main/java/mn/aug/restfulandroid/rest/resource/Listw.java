package mn.aug.restfulandroid.rest.resource;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Paul on 09/11/2014.
 */
public class Listw implements Resource,TaskList {

    private int id = 0;
    private String title = "null";

    public Listw() {

    }

    @JsonCreator
    public Listw(int id, String title) {
        super();
        this.id = id;
        this.title = title;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
