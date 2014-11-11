package mn.aug.restfulandroid.service;

import java.util.List;

import android.content.Context;

import mn.aug.restfulandroid.provider.UsersDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Login;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.util.Logger;

/**
 * The LoginProcessor is a POJO for processing login requests.
 * For this pattern, there is one Processor for each resource type.
 *
 * @author Paul
 */
public class LoginProcessor {


    private ProcessorCallback mCallback;
    private Context mContext;
    private UsersDBAccess userDBAccess;


    public LoginProcessor(Context context) {

        mContext = context;
        userDBAccess=new UsersDBAccess(mContext);
    }


    void getToken(ProcessorCallback callback,byte[] body) {

		/*
		Processor is a POJO
			- Processor for each resource type
			- Processor can handle each method on the resource that is supported.
			- Processor needs a callback (which is how the request gets back to the service)
			- Processor uses a RESTMethod - created through a RESTMethodFactory.create(parameterized) or .createGetTask()
			
			First iteration had a callback that updated the content provider
			with the resources. But the eventual implementation simply block 
			for the response and do the update.
		 */

        // (4) Insert-Update the ContentProvider with a status column and
        // results column
        // Look at ContentProvider example, and build a content provider
        // that tracks the necessary data.

        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.




        RestMethod<Login> getTokenMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Login.CONTENT_URI, Method.POST, null, body,0);
        RestMethodResult<Login> result = getTokenMethod.execute();

				/*
				 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */
        Logger.debug("login",String.valueOf(result.getStatusCode()));

        if(result.getStatusCode()==200) {
            Logger.debug("split",new String(body));
            String[] strings=(new String(body)).split("&");
            String name= strings[0].split("email=")[1];
            String password=strings[1].split("password=")[1];
            userDBAccess.open();
            if(!userDBAccess.userIsInDB(name))
                userDBAccess.addUser(name,password);
            else
                userDBAccess.updateUser(name,password);
            userDBAccess.close();
            registerToken(result.getResource().getToken(),name);
        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }

    private void registerToken(String token, String name) {

        AuthorizationManager authMan = AuthorizationManager.getInstance(mContext);
        authMan.saveToken(token,name);
    }
}