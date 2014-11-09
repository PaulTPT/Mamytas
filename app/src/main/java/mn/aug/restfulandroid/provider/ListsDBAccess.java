package mn.aug.restfulandroid.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.rest.resource.Reminder;

/**
 * Created by Paul on 09/11/2014.
 */
public class ListsDBAccess {


    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public ListsDBAccess(Context context){
        //On créer la BDD et sa table
        myHelper = new ProviderDbHelper(context);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = myHelper.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }



    /**
     * Store a new list into the database
     *
     * @param list
     *            The list to be stored
     * @return The list stored with its ID
     */
    public static boolean storeList(Listw list) {
        checkDB();

        String requete = "INSERT INTO LISTS(ID,TITLE) VALUES (" + list.getId()
                + ",'" + list.getTitle() + "')";
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

    /**
     * Update a list
     *
     * @param list
     *            The list to be updated
     * @return Whether the update was successful
     */
    public static boolean updateList(Listw list) {
        checkDB();

        String requete = "UPDATE LISTS SET TITLE= '" + list.getTitle()
                + "' WHERE ID = " + list.getId();
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

    /**
     * Retrieve a list from its id
     *
     * @param listID
     *            The Id of the list to retrieve
     * @return The list corresponding to the ID
     */
    public static Listw retrieveList(int listID) {
        checkDB();

        Listw list = new Listw();

        try {
            String requete = "SELECT ID, TITLE FROM LISTS WHERE ID= " + listID;
            ResultSet res;
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            res.next();
            list = new Listw(res.getInt(1), res.getString(2));
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return list;
    }

    /**
     * Delete a list from its ID
     *
     * @param listID
     *            The ID of the list to be deleted
     * @return Whether it was successful or not
     */
    public static boolean deleteList(int listID) {
        checkDB();

        String requete = "DELETE FROM LISTS WHERE ID= " + listID;
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


    private boolean ReminderIsInDB(Listw list){

        Cursor c = bdd.query(ProviderDbHelper.TABLE_LISTS, new String[] {ProviderDbHelper.LISTS_TITLE}, ProviderDbHelper.LISTS_ID + " LIKE \"" + list.getId() +"\"", null, null, null, null);
        return c.getCount() != 0;
    }




}
