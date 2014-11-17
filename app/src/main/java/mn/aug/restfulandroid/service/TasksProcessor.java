package mn.aug.restfulandroid.service;

import android.content.Context;

import java.util.List;

import mn.aug.restfulandroid.provider.TasksDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.util.Logger;

/**
 * The TaskProcessor is a POJO for processing timeline requests.
 * For this pattern, there is one Processor for each resource type.
 *
 * @author Peter Pascale
 */
public class TasksProcessor {


    private ProcessorCallback mCallback;
    private Context mContext;
    private TasksDBAccess tasksDBAccess;


    public TasksProcessor(Context context) {

        mContext = context;
        tasksDBAccess = new TasksDBAccess(mContext);
    }


    void getTasks(ProcessorCallback callback,long list_id) {

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
        List<Integer> list_ids = tasksDBAccess.retrieveTodosFromList(list_id);
        if (list_ids != null) for (int id : list_ids) {
            tasksDBAccess.setStatus(id, "updating");
        }
        tasksDBAccess.close();

        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Tasks> getTasksMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Tasks.CONTENT_URI, Method.GET, null, null, list_id);
        RestMethodResult<Tasks> result = getTasksMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        Logger.debug("tasks", String.valueOf(result.getStatusCode()));

        if (result.getStatusCode() == 200) {
            updateDataBase(result.getResource());
        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }


    public void postTask(ProcessorCallback callback, long task_id, byte[] body) {

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
        tasksDBAccess.setStatus(task_id, "uploading");
        tasksDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Task> postTaskMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Tasks.CONTENT_URI, Method.POST, null, body, 0);
        RestMethodResult<Task> result = postTaskMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */


        if (result.getStatusCode() == 200) {

            tasksDBAccess.open();
            tasksDBAccess.setStatus(task_id, "up_to_date");
            tasksDBAccess.close();

            tasksDBAccess.open();
            tasksDBAccess.deleteTodo(task_id);
            Task task = result.getResource();
            tasksDBAccess.storeTodo(task);
            tasksDBAccess.close();
              }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }

    private void updateDataBase(Tasks tasksResult) {


        List<Task> tasks = tasksResult.getTasks();

        String user = AuthorizationManager.getInstance(mContext).getUser();

        if (tasks != null && user != null) {
            // insert/update row for each Task
            tasksDBAccess.open();
                      for (Task task : tasks) {

                if (!tasksDBAccess.TodoIsInDB(task)) {
                    tasksDBAccess.storeTodo(task);
                } else {
                    tasksDBAccess.updateTodo(task);
                    tasksDBAccess.setStatus((int) task.getId(), "up_to_date");
                }


            }


            List<Integer> list_ids = tasksDBAccess.retrieveTasksWithState("updating");
            if (list_ids != null) for (int id : list_ids) {
                tasksDBAccess.deleteTodo(id);
            }


            tasksDBAccess.close();

        }


    }


}