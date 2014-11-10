package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mn.aug.restfulandroid.rest.resource.Listw;

/**
 * Created by Paul on 09/11/2014.
 */
public class ListsDBAccess {


    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public ListsDBAccess(Context context) {
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
     * Store a new list into the database
     *
     * @param list The list to be stored
     * @return The list stored with its ID
     */
    public boolean storeList(Listw list) {

        if (!ListIsInDB(list)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.LISTS_ID, list.getId());
            values.put(ProviderDbHelper.LISTS_TITLE, list.getTitle());
            bdd.insert(ProviderDbHelper.TABLE_LISTS, null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    /**
     * Update a list
     *
     * @param list The list to be updated
     * @return Whether the update was successful
     */
    public boolean updateList(Listw list) {


        if (ListIsInDB(list)) try {
            ContentValues values = new ContentValues();
            values.put(ProviderDbHelper.LISTS_TITLE, list.getTitle());
            bdd.update(ProviderDbHelper.TABLE_LISTS, values, ProviderDbHelper.LISTS_ID + " = '" + list.getId()+"'", null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    /**
     * Retrieve a list from its id
     *
     * @param listID The Id of the list to retrieve
     * @return The list corresponding to the ID
     */
    public Listw retrieveList(int listID) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_TITLE},
                    ProviderDbHelper.LISTS_ID + " ='" + listID + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            String title = c.getString(0);
            return new Listw(listID, title);
        }
    }

    /**
     * Delete a list from its ID
     *
     * @param listID The ID of the list to be deleted
     * @return Whether it was successful or not
     */
    public boolean deleteList(int listID) {

        if (ListIsInDB(retrieveList(listID))) {
            try {
                bdd.delete(ProviderDbHelper.TABLE_LISTS, ProviderDbHelper.LISTS_ID + " = '" + listID+"'", null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }


    public boolean ListIsInDB(Listw list) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_TITLE}, ProviderDbHelper.LISTS_ID + " ='" + list.getId() + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }

    public boolean setStatus(Listw list, String state) {

        if (ListIsInDB(list)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.LISTS_STATE, state);
                bdd.update(ProviderDbHelper.TABLE_LISTS, values, ProviderDbHelper.LISTS_ID + " = '" + list.getId()+"'", null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }


}
