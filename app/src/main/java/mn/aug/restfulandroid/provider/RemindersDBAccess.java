package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.rest.resource.Reminder;
import mn.aug.restfulandroid.rest.resource.Task;

/**
 * Created by Paul on 09/11/2014.
 */
public class RemindersDBAccess {

    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public RemindersDBAccess(Context context) {
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
     * Store a new reminder into the database
     *
     * @param reminder The reminder to be stored
     * @return The reminder stored with its ID
     */
    public  Reminder storeReminder(Reminder reminder) {

        if (!reminderIsInDB(reminder)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.REMINDERS_DATE, reminder.getDate());
                values.put(ProviderDbHelper.REMINDERS_TASK_ID, reminder.getTask_id());
                values.put(ProviderDbHelper.REMINDERS_OWNER, reminder.getOwner());
                long id= bdd.insert(ProviderDbHelper.TABLE_REMINDERS, null, values);
                reminder.setId((int) id);
                return reminder;
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
        }
        return null;

    }

    /**
     * Retrieve the reminders relative to a task
     *
     * @param taskID The id of the task
     * @return The reminders
     */
    public List<Reminder> retrieveTaskReminders(int taskID) {

        List<Reminder> list = new ArrayList<Reminder>();

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_REMINDERS, new String[]{ProviderDbHelper.REMINDERS_ID,ProviderDbHelper.REMINDERS_DATE,ProviderDbHelper.REMINDERS_OWNER},
                    ProviderDbHelper.REMINDERS_TASK_ID + " LIKE \"" + taskID + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int count=c.getCount();
        if (c.getCount() == 0)
            return null;
        else {
            for(int i=0;i<count;i++) {
                list.add(new Reminder(c.getInt(0), taskID, c.getString(1),
                        c.getString(2)));
            }
            return list;

        }

    }

    /**
     * Retrieve all the reminders
     *
     * @param user The user owning the reminders to retrieve
     * @return The reminders
     */
    public  List<Reminder> retrieveRemindersFromUser(String user) {

        List<Reminder> list = new ArrayList<Reminder>();


        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_REMINDERS, new String[]{ProviderDbHelper.REMINDERS_ID,ProviderDbHelper.REMINDERS_TASK_ID,ProviderDbHelper.REMINDERS_DATE},
                    ProviderDbHelper.REMINDERS_OWNER + " LIKE \"" + user + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int count=c.getCount();
        if (c.getCount() == 0)
            return null;
        else {
            for(int i=0;i<count;i++) {
                list.add(new Reminder(c.getInt(0), c.getInt(1),
                        c.getString(2),user));
            }
            return list;

        }

    }

    /**
     * Delete a reminder from its ID
     *
     * @param reminderID The ID of the reminder to be deleted
     * @return Whether it was successful or not
     */
    public  boolean deleteReminder(int reminderID) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_REMINDERS, ProviderDbHelper.REMINDERS_ID + " = " + reminderID, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    public boolean reminderIsInDB(Reminder reminder) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_REMINDERS, new String[]{ProviderDbHelper.REMINDERS_OWNER}, ProviderDbHelper.REMINDERS_ID + " LIKE \"" + reminder.getId() + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }

    public boolean setStatus(Reminder reminder, String state) {

        if (reminderIsInDB(reminder)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.REMINDERS_STATE, state);
            bdd.update(ProviderDbHelper.TABLE_REMINDERS, values, ProviderDbHelper.REMINDERS_ID + " = " + reminder.getId(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


}
