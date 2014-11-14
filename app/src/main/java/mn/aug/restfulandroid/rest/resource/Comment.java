package mn.aug.restfulandroid.rest.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Paul on 09/11/2014.
 */
public class Comment implements Resource {

    @JsonProperty("channel_id")
    int task_id = 0;
    String channel_type = "tasks";
    int id = 0;
    String text = "null";

    @JsonCreator
    public Comment(@JsonProperty("id") int id,@JsonProperty("task_id") int task_id,@JsonProperty("text") String text) {
        super();
        this.task_id = task_id;
        this.id = id;
        this.text = text;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
