package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

        if (!ListIsInDB(list.getId())) try {
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


        if (ListIsInDB(list.getId())) try {
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
    public Listw retrieveList(long listID) {

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
    public boolean deleteList(long listID) {

        if (ListIsInDB(listID)) {
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


    public boolean ListIsInDB(long id) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_TITLE}, ProviderDbHelper.LISTS_ID + " ='" + id + "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }

    public boolean setStatus(long id, String state) {

        if (ListIsInDB(id)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.LISTS_STATE, state);
                bdd.update(ProviderDbHelper.TABLE_LISTS, values, ProviderDbHelper.LISTS_ID + " = '" + id+"'", null);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public String getStatus(long id) {

        if (ListIsInDB(id)) {
            Cursor c = null;
            try {
                c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_STATE},
                        ProviderDbHelper.LISTS_ID + " ='" +id + "'", null, null, null, null);
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
        return "not_existing";
    }

    public List<Long> retrieveAllLists(){

        List<Long> list = new ArrayList<Long>();

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_ID},null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return list;
        else {
            c.moveToFirst();
            do {
                long list_id = c.getLong(0);
                list.add(list_id);
            } while (c.moveToNext());
            return list;

        }

    }

    public List<Long> retrieveListsWithState(String state){

        List<Long> list = new ArrayList<Long>();

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[]{ProviderDbHelper.LISTS_ID},
                    ProviderDbHelper.LISTS_STATE + " ='" + state+ "'", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c.getCount() == 0)
            return null;
        else {
            c.moveToFirst();
            do {
                long list_id = c.getLong(0);
                list.add(list_id);
            } while (c.moveToNext());
            return list;

        }
    }


}
