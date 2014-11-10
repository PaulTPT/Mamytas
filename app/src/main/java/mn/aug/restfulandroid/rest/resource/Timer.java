package mn.aug.restfulandroid.rest.resource;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Paul on 10/11/2014.
 */
public class Timer {
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



}