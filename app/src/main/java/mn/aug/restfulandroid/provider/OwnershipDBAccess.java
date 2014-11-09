package mn.aug.restfulandroid.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import mn.aug.restfulandroid.rest.resource.Reminder;

/**
 * Created by Paul on 09/11/2014.
 */
public class OwnershipDBAccess {


    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public OwnershipDBAccess(Context context){
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
     * Add a task to the user's ownership list
     *
     * @param name
     *            Name of the user
     * @param todo
     *            The task
     * @return Whether it was successful
     */
    public static Task addTask(String name, Task todo) {
        checkDB();
        String requete = "INSERT INTO OWNERSHIP (type,owner) VALUES ( 'TASK','"
                + name + "')";
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            todo.setId(id);
            stmt.close();

            Statement stmt2 = connec.createStatement();
            String requete2 = "UPDATE OWNERSHIP SET EFFECTIVE_ID= " + id
                    + " WHERE ID= " + id;
            stmt2.executeUpdate(requete2);
            stmt2.close();

            TasksDatabase.storeTodo(todo);
            return todo;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add an existing task to the user's ownership list
     *
     * @param name
     *            Name of the user
     * @param todo
     *            The task
     * @return Whether it was successful
     */
    public static Task addSharedTask(String name, Task todo) {
        checkDB();
        String requete = "INSERT INTO OWNERSHIP (type,owner,effective_id) VALUES ( 'TASK','"
                + name + "'," + todo.getId() + ")";
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete);
            stmt.close();
            return todo;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add a list to the user's ownership list
     *
     * @param name
     *            Name of the user
     * @param list
     *            The list
     * @return Whether it was successful
     */
    public static Listw addList(String name, Listw list) {
        checkDB();
        String requete = "INSERT INTO OWNERSHIP (type,owner) VALUES ( 'LIST','"
                + name + "')";
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            list.setId(id);
            stmt.close();

            Statement stmt2 = connec.createStatement();
            String requete2 = "UPDATE OWNERSHIP SET EFFECTIVE_ID= " + id
                    + " WHERE ID= " + id;
            stmt2.executeUpdate(requete2);
            stmt2.close();

            ListsDatabase.storeList(list);
            return list;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Add a list to an other user's ownership list
     *
     * @param name
     *            Name of the user
     * @param list
     *            The list
     * @return Whether it was successful
     */
    public static Listw shareListwithUser(String name, Listw list) {
        checkDB();
        String requete = "INSERT INTO OWNERSHIP (type,owner,effective_id) VALUES ( 'LIST','"
                + name + "', " + list.getId() + " )";
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete);
            stmt.close();

            for (int taskId : TasksDatabase.retrieveTodosFromList(list.getId())) {
                Task task = TasksDatabase.retrieveTodo(taskId);
                OwnershipDatabase.addSharedTask(name, task);

            }

            return list;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Remove a task from the user's ownership list
     *
     * @param todoID
     *            ID of the task
     * @return Whether it was successful
     */
    public static boolean removeTask(int todoID) {
        checkDB();

        String requete = "DELETE FROM OWNERSHIP WHERE EFFECTIVE_ID= " + todoID;
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete);
            stmt.close();
            TasksDatabase.deleteTodo(todoID);
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove all the tasks related to a list
     *
     * @param listID
     *            ID of the list
     * @return Whether it was successful
     */
    public static boolean removeListTasks(int listID) {
        checkDB();
        List<Integer> list = TasksDatabase.retrieveTodosFromList(listID);
        TasksDatabase.deleteTodosFromList(listID);
        String requete = "DELETE FROM OWNERSHIP WHERE EFFECTIVE_ID= ";
        try {
            for (int id : list) {
                Statement stmt = connec.createStatement();
                stmt.executeUpdate(requete + id);
                stmt.close();
            }
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a list from the user's ownership list
     *
     * @param listID
     *            ID of the list
     *
     * @param user
     *            The user which wants to delete the list
     *
     * @return Whether it was successful
     */
    public static boolean removeListFromUser(String user, int listID) {
        checkDB();

        String requete = "DELETE FROM OWNERSHIP WHERE EFFECTIVE_ID= " + listID
                + " AND OWNER= '" + user + "'";
        String requete2 = "SELECT ID FROM OWNERSHIP WHERE EFFECTIVE_ID= "
                + listID;
        try {
            Statement stmt = connec.createStatement();
            stmt.executeUpdate(requete);
            stmt.close();

            // Do an other user still own the list ?
            Statement stmt2 = connec.createStatement();
            ResultSet res = stmt2.executeQuery(requete2);
            if (!res.isBeforeFirst()) { // No one still owns the list
                ListsDatabase.deleteList(listID); // We completely delete it
                removeListTasks(listID);

            }
            stmt2.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all the IDs of the tasks owned by the user
     *
     * @param user
     *            Name of the user
     * @return List of the Ids
     */
    public static List<Integer> getTasksIds(String user) {
        checkDB();

        String requete = "SELECT EFFECTIVE_ID FROM OWNERSHIP WHERE TYPE = 'TASK' AND  OWNER= '"
                + user + "'";
        ResultSet res;
        List<Integer> list = new ArrayList<Integer>();
        try {
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()) {
                list.add(res.getInt(1));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }

    /**
     * Get all the IDs of the lists owned by the user
     *
     * @param user
     *            Name of the user
     * @return List of the Ids
     */
    public static List<Integer> getListsIds(String user) {
        checkDB();

        String requete = "SELECT EFFECTIVE_ID FROM OWNERSHIP WHERE TYPE = 'LIST' AND  OWNER= '"
                + user + "'";
        ResultSet res;
        List<Integer> list = new ArrayList<Integer>();
        try {
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()) {
                list.add(res.getInt(1));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }

    /**
     * Does the user own this task ?
     *
     * @param user
     *            Name of the user
     * @param id
     *            Id of the task
     * @return Whether the user owns the task
     */
    public static boolean userOwnsTask(String user, int id) {
        List<Integer> list = getTasksIds(user);
        return list.contains(id);

    }

    /**
     * Does the user own this list ?
     *
     * @param user
     *            Name of the user
     * @param id
     *            Id of the list
     * @return Whether the user owns the list
     */
    public static boolean userOwnsList(String user, int id) {
        List<Integer> list = getListsIds(user);
        return list.contains(id);

    }

    /**
     * Get all the tasks owned by a user
     *
     * @param user
     *            The name of the user
     * @return A list of all the tasks owned by the user
     */
    public static List<Task> getTodos(String user) {

        List<Task> list = new ArrayList<Task>();

        for (int id : getTasksIds(user)) {
            list.add(TasksDatabase.retrieveTodo(id));

        }

        return list;
    }

    /**
     * Get all the lists owned by a user
     *
     * @param user
     *            The name of the user
     * @return A list of all the lists owned by the user
     */
    public static List<Listw> getLists(String user) {

        List<Listw> list = new ArrayList<Listw>();

        for (int id : getListsIds(user)) {
            list.add(ListsDatabase.retrieveList(id));

        }

        return list;
    }

    /**
     * Is what corresponds to this Id a task ?
     *
     * @param id
     *            The id of the list/task
     * @return Whether it is a task (If it is not a task, it is a list ...)
     */
    public static boolean isTask(int id) {
        checkDB();

        String requete = "SELECT TYPE FROM OWNERSHIP WHERE EFFECTIVE_ID= ";
        ResultSet res;
        try {
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            res.next();
            if (res.getString(1).equals("TODO")) {
                stmt.close();
                return true;
            } else {
                stmt.close();
                return false;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Get all the owners of a list
     *
     * @param list_id id of the list
     * @return The list of owners
     */
    public static ArrayList<String> getListOwners(int list_id) {
        ArrayList<String> owners = new ArrayList<String>();

        checkDB();

        String requete = "SELECT OWNER FROM OWNERSHIP WHERE EFFECTIVE_ID ="
                + list_id;
        ResultSet res;
        try {
            Statement stmt = connec.createStatement();
            res = stmt.executeQuery(requete);
            while (res.next()) {
                owners.add(res.getString(1));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return owners;
    }


}
