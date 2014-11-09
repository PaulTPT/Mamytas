package mn.aug.restfulandroid.rest.resource;

import com.fasterxml.jackson.annotation.JsonCreator;

import mn.aug.restfulandroid.R;

/**
 * Created by Paul on 09/11/2014.
 */
public class Reminder implements Resource
{

        int id = 0;
        int task_id = 0;
        String date = null;
        String owner = "null";

        public Reminder() {

        };

        @JsonCreator
        public Reminder(int id, int task_id, String date, String owner) {
            super();
            this.id = id;
            this.task_id = task_id;
            this.date = date;
            this.owner = owner;
        }



        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

    }
