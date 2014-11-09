package mn.aug.restfulandroid.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mn.aug.restfulandroid.rest.resource.Task;

/**
 * Created by Paul on 09/11/2014.
 */
public class TasksDBAccess {



    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public TasksDBAccess(Context context){
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
     * Store a new task into the database
     *
     * @param todo
     *            The task to be stored
     * @return The task stored with its ID
     */
    public  boolean storeTodo(Task todo) {
        
        
        


        String requete = "INSERT INTO TODOS(ID,TITLE,DUEDATE,LISTID) VALUES ("
                + todo.getId() + ",'" + todo.getTitle() + "','"
                + todo.getDueDate() + "'," + todo.getList_id() + ")";
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
     * Update a task
     *
     * @param todo
     *            The task to be updated
     * @return Whether the update was successful
     */
    public  boolean updateTodo(Task todo) {
        checkDB();

        String requete = "UPDATE TODOS SET TITLE = '" + todo.getTitle()
                + "' , DUEDATE = '" + todo.getDueDate() + "', LISTID = "
                + todo.getList_id() + " WHERE ID = " + todo.getId();
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
     * Retrieve a task from its id
     *
     * @param todoID
     *            The Id of the task to retrieve
     * @return The task corresponding to the ID
     */
    public  Task retrieveTodo(int todoID) {
        checkDB();

        Task todo = new Task();

        try {
            String requete = "SELECT ID, TITLE, DUEDATE, LISTID FROM TODOS WHERE ID= "
                    + todoID;
            ResultSet res;
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            res.next();
            todo = new Task(res.getInt(1), res.getString(2), res.getString(3),
                    res.getInt(4));
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return todo;
    }

    /**
     * Delete a task from its ID
     *
     * @param todoID
     *            The ID of the task to be deleted
     * @return Whether it was successful or not
     */
    public  boolean deleteTodo(int todoID) {
        checkDB();

        String requete = "DELETE FROM TODOS WHERE ID= " + todoID;
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
     * Delete all the tasks of a list
     *
     * @param list_ID
     *            The ID of the liqt to be deleted
     * @return Whether it was successful or not
     */
    public  boolean deleteTodosFromList(int list_ID) {
        checkDB();

        String requete = "DELETE FROM TODOS WHERE LISTID= " + list_ID;
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
     * Retrieve tasks related to a list
     *
     * @param listID
     *            The Id of the list
     * @return The tasks ids corresponding to the list
     */
    public  List<Integer> retrieveTodosFromList(int listID) {
        checkDB();

        List<Integer>list =new ArrayList<Integer>();

        try {
            String requete = "SELECT ID FROM TODOS WHERE LISTID= "
                    + listID;
            ResultSet res;
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()){
                list.add(res.getInt(1));
            }
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return list;
    }


    private boolean TaskIsInDB(Task task){

        Cursor c = bdd.query(ProviderDbHelper.TABLE_TODOS, new String[] {ProviderDbHelper.TODOS_TITLE}, ProviderDbHelper.TODOS_ID + " LIKE \"" + task.getId() +"\"", null, null, null, null);
        return c.getCount() != 0;
    }




}
