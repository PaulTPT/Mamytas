package mn.aug.restfulandroid.provider;

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


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
	}

}
