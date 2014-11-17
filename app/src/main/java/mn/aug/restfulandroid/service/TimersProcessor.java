package mn.aug.restfulandroid.service;

import android.content.Context;

import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Timer;
import mn.aug.restfulandroid.rest.resource.Timers;

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


    void getTimers(ProcessorCallback callback,Long task_id) {

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
                Timers.CONTENT_URI, Method.GET, null, null,task_id);
        RestMethodResult<Timers> result = getTimersMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */


        // (9) Operation complete callback to Service
        callback.send(result.getStatusCode(),result.getResource());

    }

    public void postTimer(ProcessorCallback callback, long task_id, byte[] body,long timer_id) {

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
        ownershipDBAccess.open();
        ownershipDBAccess.setStatus(timer_id, "uploading");
        ownershipDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Timer> postTaskMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Timers.CONTENT_URI, Method.POST, null, body, task_id);
        RestMethodResult<Timer> result = postTaskMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */


        if (result.getStatusCode() == 200) {

            ownershipDBAccess.open();
            ownershipDBAccess.setStatus(timer_id, "up_to_date");
            ownershipDBAccess.close();

            ownershipDBAccess.open();
            ownershipDBAccess.deleteTimer(timer_id);
            Timer timer = result.getResource();
            ownershipDBAccess.storeTimer(timer);
            tasksDBAccess.close();
            callback.send(result.getStatusCode(),timer);
        }else {

            callback.send(result.getStatusCode());
        }


    }

    public void putTimer(ProcessorCallback callback, long task_id, byte[] body,long timer_id) {

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
        ownershipDBAccess.open();
        ownershipDBAccess.setStatus(timer_id, "updating");
        ownershipDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Timer> putTaskMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Timers.CONTENT_URI, RestMethodFactory.Method.PUT, null, body, task_id);
        RestMethodResult<Timer> result = putTaskMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        if (result.getStatusCode() == 200 ) {

            ownershipDBAccess.open();
            ownershipDBAccess.setStatus(timer_id, "up_to_date");
            ownershipDBAccess.close();


        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }
}