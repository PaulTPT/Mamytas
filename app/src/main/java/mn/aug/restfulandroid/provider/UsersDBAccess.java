package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Paul on 07/11/2014.
 */
public class UsersDBAccess {


    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public UsersDBAccess(Context context) {
        //On créer la BDD et sa table
        myHelper = new ProviderDbHelper(context);
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
     * Change a user's password
     *
     * @param name     Name of the user
     * @param password New password
     * @return Whether it was successful
     */
    public boolean updateUser(String name, String password) {

        if (userIsInDB(name)) {

            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.USERS_PASSWORD, password);
                bdd.update(ProviderDbHelper.TABLE_USERS, values, ProviderDbHelper.USERS_NAME + " = '" + name+"'", null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
        return false;


    }

    /**
     * Delete a user
     *
     * @param name NAme of the user
     * @return Whether it was successful
     */
    public boolean deleteUser(String name) {
        if (userIsInDB(name)) {
            try {
                bdd.delete(ProviderDbHelper.TABLE_USERS, ProviderDbHelper.USERS_NAME + " = '" + name+"'", null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;

    }


    /**
     * Add a user
     *
     * @param name     The user's name
     * @param password The user's password
     * @return Whether it was successful
     */
    public boolean addUser(String name, String password) {

        if (!userIsInDB(name)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.USERS_NAME, name);
                values.put(ProviderDbHelper.USERS_PASSWORD, password);
                bdd.insert(ProviderDbHelper.TABLE_USERS, null, values);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;


    }

    /**
     * Check if the user is known in the database and if its password is correct
     *
     * @param name     Name of the user
     * @param password Password to check
     * @return Whether the user successfully logged-in
     */
    public boolean validateUser(String name, String password) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_USERS, new String[]{ProviderDbHelper.USERS_NAME}, ProviderDbHelper.USERS_NAME + " ='" + name + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (c.getCount() == 0)
            return false;
        else {
            c.moveToFirst();
            String DBPassword = c.getString(0);
            return password.equals(DBPassword);


        }


    }

    public boolean userIsInDB(String name) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_USERS, new String[]{ProviderDbHelper.USERS_NAME}, ProviderDbHelper.USERS_NAME + " ='" + name + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }


    public boolean setStatus(String name, String state) {

        if (userIsInDB(name)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.USERS_STATE, state);
            bdd.update(ProviderDbHelper.TABLE_USERS, values, ProviderDbHelper.USERS_NAME + " = '" + name+"'", null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;


    }


}
