package mn.aug.restfulandroid.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;
import mn.aug.restfulandroid.rest.resource.Timers;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.util.Logger;

/**
 * The TaskProcessor is a POJO for processing timeline requests.
 * For this pattern, there is one Processor for each resource type.
 *
 * @author Peter Pascale
 */
public class TimersProcessor {


    private ProcessorCallback mCallback;
    private Context mContext;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;


    public TimersProcessor(Context context) {

        mContext = context;
        ownershipDBAccess = new OwnershipDBAccess(mContext);
        tasksDBAccess = new TasksDBAccess(mContext);
    }


    void getTimers(ProcessorCallback callback) {

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

        RestMethod<Timers> getTimersMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Tasks.CONTENT_URI, Method.GET, null, null,0);
        RestMethodResult<Timers> result = getTimersMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */


        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode(),result.getResource());

    }

 }