package mn.aug.restfulandroid.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.rest.resource.Comment;

/**
 * Created by Paul on 09/11/2014.
 */
public class CommentsDBAccess {

    private SQLiteDatabase bdd;

    private ProviderDbHelper myHelper;

    public CommentsDBAccess(Context context) {
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
     * Store a new comment into the database
     *
     * @param comment The comment to be stored
     * @return The comment stored with its ID
     */
    public Comment storeComment(Comment comment) {

        if (!commentIsInDB(comment)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.COMMENTS_TEXT, comment.getText());
                values.put(ProviderDbHelper.COMMENTS_TASK_ID, comment.getTask_id());
                long id = bdd.insert(ProviderDbHelper.TABLE_COMMENTS, null, values);
                comment.setId((int) id);
                return comment;
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
        }
        return null;

    }

    /**
     * Retrieve the comments relative to a task
     *
     * @param taskID The id of the task
     * @return The comments
     */
    public List<Comment> retrieveTaskComments(int taskID) {

        List<Comment> list = new ArrayList<Comment>();


        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_COMMENTS, new String[]{ProviderDbHelper.COMMENTS_ID, ProviderDbHelper.COMMENTS_TEXT},
                    ProviderDbHelper.COMMENTS_TASK_ID + " LIKE \"" + taskID + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int count = c.getCount();
        if (c.getCount() == 0)
            return null;
        else {
            for (int i = 0; i < count; i++) {
                list.add(new Comment(c.getInt(0), taskID, c.getString(1)));
            }
            return list;

        }
    }

    /**
     * Delete a comment from its ID
     *
     * @param commentID The ID of the comment to be deleted
     * @return Whether it was successful or not
     */
    public boolean deleteComment(int commentID) {

        try {
            bdd.delete(ProviderDbHelper.TABLE_COMMENTS, ProviderDbHelper.COMMENTS_ID + " = " + commentID, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    private boolean commentIsInDB(Comment comment) {

        Cursor c = null;
        try {
            c = bdd.query(ProviderDbHelper.TABLE_COMMENTS, new String[]{ProviderDbHelper.COMMENTS_TEXT}, ProviderDbHelper.COMMENTS_ID + " LIKE \"" + comment.getId() + "\"", null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return c.getCount() != 0;
    }

    private boolean setStatus(Comment comment, String state) {

        if (commentIsInDB(comment)) {
            try {
                ContentValues values = new ContentValues();
                values.put(ProviderDbHelper.COMMENTS_STATE, state);
                bdd.update(ProviderDbHelper.TABLE_COMMENTS, values, ProviderDbHelper.COMMENTS_ID + " = " + comment.getId(), null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


}
