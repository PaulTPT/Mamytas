package mn.aug.restfulandroid.service;

import android.content.Context;

import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Task;

/**
 * Created by Paul on 11/11/2014.
 */
public class TaskProcessor {

    private ProcessorCallback mCallback;
    private Context mContext;
    private OwnershipDBAccess ownershipDBAccess;
    private TasksDBAccess tasksDBAccess;


    public TaskProcessor(Context context) {

        mContext = context;
        ownershipDBAccess = new OwnershipDBAccess(mContext);
        tasksDBAccess = new TasksDBAccess(mContext);
    }


    public void putTask(ProcessorCallback callback, long task_id, byte[] body) {

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
        tasksDBAccess.open();
        tasksDBAccess.setStatus(task_id, "post_update");
        tasksDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Task> putTaskMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Task.CONTENT_URI, RestMethodFactory.Method.PUT, null, body, task_id);
        RestMethodResult<Task> result = putTaskMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */
        Task task = result.getResource();

        if (result.getStatusCode() == 200 && task_id ==task.getId()) {

            tasksDBAccess.open();
            tasksDBAccess.setStatus(task_id, "up_to_date");
            tasksDBAccess.close();


        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }

    public void deleteTask(ProcessorCallback callback, long task_id) {

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
        tasksDBAccess.open();
        tasksDBAccess.setStatus(task_id, "deleting");
        tasksDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Task> deleteTaskMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Task.CONTENT_URI, RestMethodFactory.Method.DELETE, null,null,task_id);
        RestMethodResult<Task> result = deleteTaskMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        if (result.getStatusCode() == 200) {

            ownershipDBAccess.open();
            ownershipDBAccess.removeTask(task_id);
            ownershipDBAccess.close();

        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }



}




