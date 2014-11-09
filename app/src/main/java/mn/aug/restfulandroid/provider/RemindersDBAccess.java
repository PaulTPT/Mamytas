package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    /**
     * Store a new reminder into the database
     *
     * @param reminder The reminder to be stored
     * @return The reminder stored with its ID
     */
    public static Reminder storeReminder(Reminder reminder) {
        checkDB();

        String requete = "INSERT INTO REMINDERS (DATE,TASK_ID,OWNER) VALUES ('"
                + reminder.getDate() + "'," + reminder.getTask_id() + ",'"
                + reminder.getOwner() + "')";
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            reminder.setId(id);
            stmt.close();
            return reminder;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Retrieve the reminders relative to a task
     *
     * @param TaskID The id of the task
     * @return The reminders
     */
    public static List<Reminder> retrieveTaskReminders(int TaskID) {
        checkDB();
        List<Reminder> list = new ArrayList<Reminder>();

        try {
            String requete = "SELECT ID, DATE, OWWNER FROM REMINDERS WHERE TASK_ID= "
                    + TaskID;
            ResultSet res;
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()) {
                list.add(new Reminder(res.getInt(1), TaskID, res.getString(2),
                        res.getString(3)));
            }
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return list;
    }

    /**
     * Retrieve all the reminders
     *
     * @param user The user owning the reminders to retrieve
     * @return The reminders
     */
    public static List<Reminder> retrieveRemindersFromUser(String user) {
        checkDB();
        List<Reminder> list = new ArrayList<Reminder>();

        try {
            String requete = "SELECT ID,TASK_ID,DATE FROM REMINDERS WHERE OWNER='"
                    + user + "'";
            ResultSet res;
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()) {
                list.add(new Reminder(res.getInt(1), res.getInt(2), res
                        .getString(3), user));
            }
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return list;
    }

    /**
     * Delete a reminder from its ID
     *
     * @param reminderID The ID of the reminder to be deleted
     * @return Whether it was successful or not
     */
    public static boolean deleteReminder(int reminderID) {
        checkDB();

        String requete = "DELETE FROM REMINDERS WHERE ID= " + reminderID;
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete);
            stmt.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

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

    private boolean ReminderIsInDB(Reminder reminder) {

        Cursor c = bdd.query(ProviderDbHelper.TABLE_REMINDERS, new String[]{ProviderDbHelper.REMINDERS_OWNER}, ProviderDbHelper.REMINDERS_ID + " LIKE \"" + reminder.getId() + "\"", null, null, null, null);
        return c.getCount() != 0;
    }

    private boolean setStatus(Reminder reminder, String state) {

        if (ReminderIsInDB(reminder)) {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.REMINDERS_STATE, state);
            bdd.update(ProviderDbHelper.TABLE_REMINDERS, values, ProviderDbHelper.REMINDERS_ID + " = " + reminder.getId(), null);
            return true;
        }
        return false;
    }


}
