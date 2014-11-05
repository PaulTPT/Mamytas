package mn.aug.restfulandroid;

import android.app.Application;
import android.content.Context;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.util.Logger;

public class RestfulAndroid extends Application {

	private static Context mAppContext;

	@Override
	public void onCreate() {
		super.onCreate();

		mAppContext = getApplicationContext();
		
		Logger.setAppTag(getString(R.string.app_log_tag));
		Logger.setLevel(Logger.DEBUG);
	}

	/**
	 * Returns the application's context. Useful for classes that need a Context
	 * but don't inherently have one.
	 * 
	 * @return application context
	 */
	public static Context getAppContext() {
		return mAppContext;
	}

}
