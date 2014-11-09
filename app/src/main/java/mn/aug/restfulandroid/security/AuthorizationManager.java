package mn.aug.restfulandroid.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import mn.aug.restfulandroid.rest.Request;


/**
 * OAuthManager handles OAuth authentication with the Twitter API.
 * 
 * @author jeremy
 * 
 */
public class AuthorizationManager implements RequestSigner {


	// Singleton instance of the OAuthManager
	private static AuthorizationManager mInstance;

	// Preferences in which to store the request and access tokens
	private final SharedPreferences prefs;

    public static final String  WUNDERLIST_TOKEN="WUNDERLIST_TOKEN";
    public static final String  WUNDERLIST_NAME="WUNDERLIST_NAME";

	private String mToken;
    private String mUser;


	/**
	 * Returns the singleton instance of the OAuthManager
	 * 
	 * @return singleton instance of the OAuthManager
	 */
	public static AuthorizationManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new AuthorizationManager(context);
		}
		return mInstance;
	}

	/**
	 * Private constructor for the OAuthManager. Initializes the persistent
	 * storate and OAuthService
	 */
	private AuthorizationManager(Context context) {

		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

	}


	/**
	 * Persists the token. Pass in <code>null</code> to clear the saved
	 * token.
	 * 
	 * @param token
	 *            the token to persist, or <code>null</code> to clear it
	 * @return <code>true</code> if the save was successful
	 */
	public boolean saveToken(String token, String user) {

		SharedPreferences.Editor editor = prefs.edit();

		if (token == null) {
			editor.remove(WUNDERLIST_TOKEN);
            editor.remove(WUNDERLIST_NAME);
		} else {
			editor.putString(WUNDERLIST_TOKEN, token);
            editor.putString(WUNDERLIST_NAME, user);
		}

		return editor.commit();
	}




	/**
	 * Retrieves  the request token for authorizing the app
	 */
	private void retrieveToken() {

        //Appel au service pour récupérer le token

        String token = prefs.getString(WUNDERLIST_TOKEN,null);
        this.mToken=token;
	}

    /**
     * Retrieves the name of the usere
     */
    private void retrieveName() {

        //Appel au service pour récupérer le token

        String name = prefs.getString(WUNDERLIST_NAME,null);
        this.mUser=name;
    }

	/**
	 * Returns the token (may be null)
	 *
	 * @return saved  token (or null if it does not exist)
	 */
    public String getToken() {
		return mToken;
	}

    /**
     * Returns the user (may be null)
     *
     * @return saved user (or null if it does not exist)
     */
    public String getUser() {
        return mUser;
    }

	/**
	 * Determines whether a user is currently logged in
	 * 
	 * @return <code>true</code> if user is logged in, <code>false</code>
	 *         otherwise
	 */
	public boolean loggedIn() {
		return getToken() != null;
	}

	/**
	 * Log out of the application
	 */
	public void logout() {
		mToken = null;
        mUser=null;
		saveToken(mToken,mUser);
	}

	/**
	 * Authorizes aWunderlist request.
	 *
	 * Authorizing a Request</a> for authorization requirements and methods.
	 */
	@Override
	public void authorize(Request request) {

        retrieveToken();

        List<String> values = new ArrayList<String>();
        values.add("Bearer " + mToken);

		request.addHeader("Authorization", values);

	}


}
