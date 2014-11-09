package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;

/**
 * Created by Paul on 09/11/2014.
 */
public class TasksDBAccess {


    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public TasksDBAccess(Context context) {
        //On créer la BDD et sa table
        myHelper = new ProviderDbHelper(context);
    }

    public void open() {
        //on ouvre la BDD en écriture
        bdd = myHelper.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }


    /**
     * Store a new task into the database
     *
     * @param todo The task to be stored
     * @return The task stored with its ID
     */
    public boolean storeTodo(Task todo) {

        if (!TodoIsInDB(todo)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.TODOS_ID, todo.getId());
            values.put(ProviderDbHelper.TODOS_TITLE, todo.getTitle());
            values.put(ProviderDbHelper.TODOS_DUE_DATE, todo.getDue_date());
            values.put(ProviderDbHelper.TODOS_LIST_ID, todo.getList_id());
            bdd.insert(ProviderDbHelper.TABLE_TODOS, null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }


    /**
     * Update a task
     *
     * @param todo The task to be updated
     * @return Whether the update was successful
     */
    public boolean updateTodo(Task todo) {

        if (TodoIsInDB(todo)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.TODOS_TITLE, todo.getTitle());
            values.put(ProviderDbHelper.TODOS_DUE_DATE, todo.getDue_date());
            values.put(ProviderDbHelper.TODOS_LIST_ID, todo.getList_id());
            bdd.update(ProviderDbHelper.TABLE_TODOS, values, ProviderDbHelper.TODOS_ID + " = " + todo.getId(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    /**
     * Retrieve a task from its id
     *
     * @param todoID The Id of the task to retrieve
     * @return The task corresponding to the ID
     */
    public Task retrieveTodo(int todoID) {


        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_TODOS, new String[]{ProviderDbHelper.TODOS_TITLE, ProviderDbHelper.TODOS_DUE_DATE, ProviderDbHelper.TODOS_LIST_ID},
                    ProviderDbHelper.TODOS_ID + " LIKE \"" + todoID + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            String title = c.getString(0);
            String due_date=c.getString(1);
            Long   list_id=c.getLong(2);
            return new Task(todoID,title,due_date,list_id);
        }
    }

    /**
     * Delete a task from its ID
     *
     * @param todoID The ID of the task to be deleted
     * @return Whether it was successful or not
     */
    public boolean deleteTodo(int todoID) {

        if (TodoIsInDB(retrieveTodo(todoID))) {
            try {
                bdd.delete(ProviderDbHelper.TABLE_TODOS, ProviderDbHelper.TODOS_ID + " = " + todoID, null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;

    }

    /**
     * Delete all the tasks of a list
     *
     * @param list_ID The ID of the list to be deleted
     * @return Whether it was successful or not
     */
    public boolean deleteTodosFromList(int list_ID) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_TODOS, ProviderDbHelper.TODOS_LIST_ID + " = " + list_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;


    }

    /**
     * Retrieve tasks related to a list
     *
     * @param listID The Id of the list
     * @return The tasks ids corresponding to the list
     */
    public List<Integer> retrieveTodosFromList(int listID) {

        List<Integer> list = new ArrayList<Integer>();

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_TODOS, new String[]{ProviderDbHelper.TODOS_ID},
                    ProviderDbHelper.TODOS_LIST_ID + " LIKE \"" + listID + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int count=c.getCount();
        if (c.getCount() == 0)
            return null;
        else {
            for(int i=0;i<count;i++) {
                int list_id = c.getInt(0);
                list.add(list_id);
            }
            return list;

        }

    }


    private boolean TodoIsInDB(Task task) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_TODOS, new String[]{ProviderDbHelper.TODOS_TITLE}, ProviderDbHelper.TODOS_ID + " LIKE \"" + task.getId() + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }

    private boolean setStatus(Task task, String state) {

        if (TodoIsInDB(task)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.TODOS_STATE, state);
                bdd.update(ProviderDbHelper.TABLE_TODOS, values, ProviderDbHelper.TODOS_ID + " = " + task.getId(), null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    }