package mn.aug.restfulandroid.provider;

import mn.aug.restfulandroid.provider.TasksProviderContract.TasksTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This creates, updates, and opens the database.  Opening is handled by the superclass, we handle 
 * the create & upgrade steps
 */
public class ProviderDbHelper extends SQLiteOpenHelper {

	public final String TAG = getClass().getSimpleName();

	//Name of the database file
	private static final String DATABASE_NAME = "wunderlist.db";
	private static final int DATABASE_VERSION = 1;

	public ProviderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// CREATE TIMELINE TABLE
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE " + TasksTable.TABLE_NAME + " (");
		sqlBuilder.append(TasksTable._ID + " INTEGER, ");
		sqlBuilder.append(TasksTable._STATUS + " TEXT, ");
		sqlBuilder.append(TasksTable._RESULT + " INTEGER, ");
		sqlBuilder.append(TasksTable.TITLE + " TEXT, ");
		sqlBuilder.append(TasksTable.DUE_DATE + " TEXT, ");
        sqlBuilder.append(TasksTable.LIST_ID + " INTEGER, ");
		sqlBuilder.append(TasksTable.CREATED + " INTEGER, ");
		sqlBuilder.append(");");
		String sql = sqlBuilder.toString();
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
		

		sql = sqlBuilder.toString();
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Gets called when the database is upgraded, i.e. the version number changes
	}

}
