package mn.aug.restfulandroid.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This creates, updates, and opens the database.  Opening is handled by the superclass, we handle 
 * the create & upgrade steps
 */
public class ProviderDbHelper extends SQLiteOpenHelper {

	public final String TAG = getClass().getSimpleName();

	//Name of the database file
	private static final String DATABASE_NAME = "wunderlist.db";
	private static final int DATABASE_VERSION = 1;

    //Strings for the COMMENTS TABLE
    private static final String TABLE_COMMENTS = "comments";
    private static final String COMMENTS_ID = "_id";
    private static final String COMMENTS_STATE= "state";
    private static final String COMMENTS_TEXT= "text";
    private static final String COMMENTS_TASK_ID= "task_id";

    // COMMENTS database creation sql statement
    private static final String COMMENTS_CREATE =
            "create table " + TABLE_COMMENTS + "("
            + COMMENTS_ID+ " integer primary key autoincrement, "
            + COMMENTS_STATE+ " text, "
            + COMMENTS_TEXT + " text, "
            + COMMENTS_TASK_ID + " integer "
            +");";


    //Strings for the LISTS TABLE
    private static final String TABLE_LISTS = "lists";
    private static final String LISTS_ID = "_id";
    private static final String LISTS_STATE= "state";
    private static final String LISTS_TITLE= "title";

    // LISTS database creation sql statement
    private static final String LISTS_CREATE =
            "create table " + TABLE_LISTS + "("
                    + LISTS_ID+ " integer primary key autoincrement, "
                    + LISTS_STATE + " text "
                    + LISTS_TITLE + " text "
                    +");";


    //Strings for the OWNERSHIP TABLE
    private static final String TABLE_OWNERSHIP = "ownership";
    private static final String OWNERSHIP_ID = "_id";
    private static final String OWNERSHIP_STATE= "state";
    private static final String OWNERSHIP_TYPE= "type";
    private static final String OWNERSHIP_OWNER= "owner";
    private static final String OWNERSHIP_EFFECTIVE_ID= "effective_id";

    // OWNERSHIP database creation sql statement
    private static final String OWNERSHIP_CREATE =
            "create table " + TABLE_OWNERSHIP + "("
                    + OWNERSHIP_ID+ " integer primary key autoincrement, "
                    + OWNERSHIP_STATE + "text, "
                    + OWNERSHIP_TYPE + " text, "
                    + OWNERSHIP_OWNER + " text, "
                    + OWNERSHIP_EFFECTIVE_ID + " integer "
                    +");";

    //Strings for the REMINDERS TABLE
    private static final String TABLE_REMINDERS = "reminders";
    private static final String REMINDERS_ID = "_id";
    private static final String REMINDERS_STATE= "state";
    private static final String REMINDERS_DATE= "date";
    private static final String REMINDERS_OWNER= "owner";
    private static final String REMINDERS_TASK_ID= "task_id";

    // REMINDERS database creation sql statement
    private static final String REMINDERS_CREATE =
            "create table " + TABLE_REMINDERS + "("
                    + REMINDERS_ID+ " integer primary key autoincrement, "
                    + REMINDERS_STATE + " text, "
                    + REMINDERS_OWNER + " text , "
                    + REMINDERS_DATE + " text, "
                    + REMINDERS_TASK_ID + " integer "
                    +");";



    //Strings for the TODOS TABLE
    private static final String TABLE_TODOS = "todos";
    private static final String TODOS_ID = "_id";
    private static final String TODOS_STATE= "state";
    private static final String TODOS_TITLE= "title";
    private static final String TODOS_DUE_DATE= "due_date";
    private static final String TODOS_LIST_ID= "list_id";

    // TODOS database creation sql statement
    private static final String TODOS_CREATE =
            "create table " + TABLE_TODOS + "("
                    +TODOS_ID+ " integer primary key autoincrement, "
                    + TODOS_STATE + " text, "
                    + TODOS_TITLE + " text, "
                    + TODOS_DUE_DATE + " text, "
                    + TODOS_LIST_ID + " integer "
                    +");";



    //Strings for the USERS TABLE
    private static final String TABLE_USERS = "users";
    private static final String USERS_ID = "_id";
    private static final String USERS_STATE= "state";
    private static final String USERS_NAME= "name";
    private static final String USERS_PASSWORD= "password";

    // USERSdatabase creation sql statement
    private static final String USERS_CREATE =
            "create table " + TABLE_USERS + "("
            +USERS_ID+ " integer primary key autoincrement, "
            +USERS_STATE+ " text, "
            +USERS_NAME+" text, "
            +USERS_PASSWORD+ " text, "
            +"),";


	public ProviderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(COMMENTS_CREATE);
        db.execSQL(OWNERSHIP_CREATE);
        db.execSQL(LISTS_CREATE);
        db.execSQL(REMINDERS_CREATE);
        db.execSQL(TODOS_CREATE);
        db.execSQL(USERS_CREATE);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNERSHIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
	}

}
