package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Timer;
import mn.aug.restfulandroid.util.Logger;

/**
 * Created by Paul on 09/11/2014.
 */
public class OwnershipDBAccess {


    private Context context = null;
    private SQLiteDatabase bdd;
    private ProviderDbHelper myHelper;
    private TasksDBAccess tasksDBAccess;
    private ListsDBAccess listsDBAccess;

    public OwnershipDBAccess(Context context) {
        //On créer la BDD et sa table
        myHelper = new ProviderDbHelper(context);
        this.context = context;
        tasksDBAccess = new TasksDBAccess(context);
        listsDBAccess = new ListsDBAccess(context);
    }


    public void open() {
        //on ouvre la BDD en écriture
        bdd = myHelper.openBDD();
    }

    public void close() {
        //on ferme l'accès à la BDD
        myHelper.closeBDD();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }


    /**
     * Add a task to the user's ownership list
     *
     * @param name Name of the user
     * @param todo The task
     * @return Whether it was successful
     */
    public boolean addTask(String name, Task todo) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "TASKS");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, todo.getId());
            values.put(ProviderDbHelper.OWNERSHIP_ID, todo.getId());
            bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);

            tasksDBAccess.open();
            tasksDBAccess.storeTodo(todo);
            tasksDBAccess.setStatus(todo.getId(), "new");
            tasksDBAccess.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Add a task to the user's ownership list
     *
     * @param name Name of the user
     * @param todo The task
     * @return Whether it was successful
     */
    public Task addTaskGetID(String name, Task todo) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "TASKS");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            long id = bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);
            todo.setId(id);

            values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, id);
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP, values, ProviderDbHelper.OWNERSHIP_ID + " = '" + id + "'", null);
            tasksDBAccess.open();
            tasksDBAccess.storeTodo(todo);
            tasksDBAccess.close();
            return todo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add an existing task to the user's ownership list
     *
     * @param name Name of the user
     * @param todo The task
     * @return Whether it was successful
     */
    public Task addSharedTask(String name, Task todo) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "TASKS");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, todo.getId());
            bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);
            return todo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



    /**
     * Add a task to the user's ownership list
     *
     * @param name Name of the user
     * @param list The list
     * @return Whether it was successful
     */
    public boolean addList(String name, Listw list) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "LIST");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, list.getId());
            values.put(ProviderDbHelper.OWNERSHIP_ID, list.getId());
            bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);

            listsDBAccess.open();
            listsDBAccess.storeList(list);
            listsDBAccess.setStatus(list.getId(),"new");
            listsDBAccess.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Add a list to the user's ownership list
     *
     * @param name Name of the user
     * @param list The list
     * @return Whether it was successful
     */
    public Listw addListGetId(String name, Listw list) {


        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "LIST");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            long id = bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);
            list.setId((int) id);

            values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, id);
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP, values, ProviderDbHelper.OWNERSHIP_ID + " = '" + id + "'", null);
            listsDBAccess.open();
            listsDBAccess.storeList(list);
            listsDBAccess.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add a list to an other user's ownership list
     *
     * @param name Name of the user
     * @param list The list
     * @return Whether it was successful
     */
    public Listw shareListwithUser(String name, Listw list) {


        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "LIST");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER, name);
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID, list.getId());
            bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);

            for (int taskId : tasksDBAccess.retrieveTodosFromList(list.getId())) {
                Task task = tasksDBAccess.retrieveTodo(taskId);
                addSharedTask(name, task);
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Remove a task from the user's ownership list
     *
     * @param todoID ID of the task
     * @return Whether it was successful
     */
    public boolean removeTask(long todoID) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_OWNERSHIP, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " = '" + todoID + "'", null);
            tasksDBAccess.open();
            tasksDBAccess.deleteTodo(todoID);
            tasksDBAccess.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove all the tasks related to a list
     *
     * @param listID ID of the list
     * @return Whether it was successful
     */
    public boolean removeListTasks(long listID) {

        tasksDBAccess.open();
        List<Integer> list = tasksDBAccess.retrieveTodosFromList(listID);
        tasksDBAccess.deleteTodosFromList(listID);
        tasksDBAccess.close();

        try {
            for (int id : list) {
                bdd.delete(ProviderDbHelper.TABLE_OWNERSHIP, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " = '" + id + "'", null);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Remove a list from the user's ownership list
     *
     * @param listID ID of the list
     * @param user   The user which wants to delete the list
     * @return Whether it was successful
     */
    public boolean removeListFromUser(String user, long listID) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_OWNERSHIP, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " = '" + listID + "'" +
                    " AND " + ProviderDbHelper.OWNERSHIP_OWNER + " = '" + user + "'", null);

            Cursor c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_ID}, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + listID + "'", null, null, null, null);

            if (c.getCount() == 0) {
                listsDBAccess.open();
                listsDBAccess.deleteList(listID);
                listsDBAccess.close();
                removeListTasks(listID);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    /**
     * Get all the IDs of the tasks owned by the user
     *
     * @param user Name of the user
     * @return List of the Ids
     */
    public List<Long> getTasksIds(String user) {

        List<Long> list = new ArrayList<Long>();
        Cursor c;

        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID}, ProviderDbHelper.OWNERSHIP_TYPE + " = " + "'TASKS'" +
                    " AND " + ProviderDbHelper.OWNERSHIP_OWNER + " = '" + user + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            do {
                list.add(c.getLong(0));
            } while (c.moveToNext());
            return list;

        }

    }

    /**
     * Get all the IDs of the lists owned by the user
     *
     * @param user Name of the user
     * @return List of the Ids
     */
    public List<Long> getListsIds(String user) {


        List<Long> list = new ArrayList<Long>();
        Cursor c;

        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID}, ProviderDbHelper.OWNERSHIP_TYPE + " = " + "'LIST'" +
                    " AND " + ProviderDbHelper.OWNERSHIP_OWNER + " = '" + user + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            do {
                list.add(c.getLong(0));
            } while (c.moveToNext());
            return list;

        }

    }

    /**
     * Does the user own this task ?
     *
     * @param user Name of the user
     * @param id   Id of the task
     * @return Whether the user owns the task
     */
    public boolean userOwnsTask(String user, long id) {
        List<Long> list = getTasksIds(user);
        if (list!= null) {
            return list.contains(id);
        }else{
            return  false;
        }

    }

    /**
     * Does the user own this list ?
     *
     * @param user Name of the user
     * @param id   Id of the list
     * @return Whether the user owns the list
     */
    public boolean userOwnsList(String user, long id) {
        List<Long> list = getListsIds(user);
        return list.contains(id);

    }

    /**
     * Get all the tasks owned by a user
     *
     * @param user The name of the user
     * @return A list of all the tasks owned by the user
     */
    public List<Task> getTodos(String user) {

        List<Task> list = new ArrayList<Task>();

        List<Long> ids = getTasksIds(user);

        if (ids != null) {
            tasksDBAccess.open();
            for (long id : ids) {
                Logger.debug("id", String.valueOf(id));
                list.add(tasksDBAccess.retrieveTodo(id));
            }
            tasksDBAccess.close();
        }

        return list;
    }


    /**
     * Get all the lists owned by a user
     *
     * @param user The name of the user
     * @return A list of all the lists owned by the user
     */
    public List<Listw> getLists(String user) {

        List<Listw> list = new ArrayList<Listw>();
        List<Long> ids = getListsIds(user);

        if (ids != null) {
            listsDBAccess.open();
            for (long id : ids) {
                list.add(listsDBAccess.retrieveList(id));
            }
            listsDBAccess.close();
        }

        return list;
    }

    /**
     * Is what corresponds to this Id a task ?
     *
     * @param id The id of the list/task
     * @return Whether it is a task (If it is not a task, it is a list ...)
     */
    public boolean isTask(long id) {

        Cursor c;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_TYPE}, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " = '" + id + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        int count = c.getCount();
        if (c.getCount() == 0)
            return false;
        else {
            c.moveToFirst();
            return c.getString(0).equals("TODO");
        }

    }

    /**
     * Get all the owners of a list
     *
     * @param list_id id of the list
     * @return The list of owners
     */
    public ArrayList<String> getListOwners(long list_id) {
        ArrayList<String> owners = new ArrayList<String>();

        Cursor c;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_OWNER}, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " = '" + list_id + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return owners;
        else {
            c.moveToFirst();
            do {
                owners.add(c.getString(0));
            } while (c.moveToNext());
            return owners;
        }

    }

    public boolean setStatus(long id, String state) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_STATE, state);
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP, values, ProviderDbHelper.OWNERSHIP_ID + " = '" + id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public String getStatus(long id) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_STATE},
                    ProviderDbHelper.OWNERSHIP_ID + " ='" + id + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            return c.getString(0);


        }

    }

    public String getTimer(long id, String owner) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_TIMER},
                    ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + id + "' AND " + ProviderDbHelper.OWNERSHIP_OWNER + " ='" + owner + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            return c.getString(0);
        }

    }

    public boolean setTimer(long id, String owner, String timer) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TIMER, timer);
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP, values, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + id + "' AND " + ProviderDbHelper.OWNERSHIP_OWNER + " ='" + owner + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public String getTimer_Start(long id, String owner) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_OWNERSHIP, new String[]{ProviderDbHelper.OWNERSHIP_TIMER_START},
                    ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + id + "' AND " + ProviderDbHelper.OWNERSHIP_OWNER + " ='" + owner + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            return c.getString(0);
        }

    }

    public boolean setTimer_Start(long id, String owner, String timer_start) {

        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TIMER_START, timer_start);
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP, values, ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + id + "' AND " + ProviderDbHelper.OWNERSHIP_OWNER + " ='" + owner + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    public Timer storeTimer(Timer timer){
        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "TASKS");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER,timer.getName());
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID,timer.getTask_id());
            values.put(ProviderDbHelper.OWNERSHIP_TIMER,timer.getTimer());
            values.put(ProviderDbHelper.OWNERSHIP_TIMER_START,timer.getTimer_start());
            long id = bdd.insert(ProviderDbHelper.TABLE_OWNERSHIP, null, values);
            timer.setOwnership_id(id);
            return timer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Timer updateTimer(Timer timer){
        try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.OWNERSHIP_TYPE, "TASKS");
            values.put(ProviderDbHelper.OWNERSHIP_OWNER,timer.getName());
            values.put(ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID,timer.getTask_id());
            values.put(ProviderDbHelper.OWNERSHIP_TIMER,timer.getTimer());
            values.put(ProviderDbHelper.OWNERSHIP_TIMER_START,timer.getTimer_start());
            bdd.update(ProviderDbHelper.TABLE_OWNERSHIP,values,ProviderDbHelper.OWNERSHIP_EFFECTIVE_ID + " ='" + timer.getOwnership_id()+  "'",null);
            return timer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Remove a task from the user's ownership list
     *
     * @return Whether it was successful
     */
    public boolean deleteTimer(long timer_id) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_OWNERSHIP, ProviderDbHelper.OWNERSHIP_ID+ " = '" + timer_id + "'", null);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}