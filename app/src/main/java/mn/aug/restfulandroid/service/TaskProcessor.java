package mn.aug.restfulandroid.service;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Task;
import mn.aug.restfulandroid.rest.resource.Tasks;
import mn.aug.restfulandroid.util.Logger;

/**
 * The TaskProcessor is a POJO for processing timeline requests.
 * For this pattern, there is one Processor for each resource type.
 * 
 * @author Peter Pascale
 */
public class TaskProcessor {


	private TaskProcessorCallback mCallback;
	private Context mContext;

	
	public TaskProcessor(Context context) {
		mContext = context;
	}

	
	void getTask(TaskProcessorCallback callback) {

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

				@SuppressWarnings("unchecked")
				RestMethod<Tasks> getTasksMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
						Tasks.CONTENT_URI, Method.GET, null, null);
				RestMethodResult<Tasks> result = getTasksMethod.execute();

				/*
				 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

                Logger.debug("tasks", String.valueOf(result.getStatusCode()));

                if(result.getStatusCode()==200) {
                    updateDataBase(result.getResource());
                }

				// (9) Operation complete callback to Service

				callback.send(result.getStatusCode());

			}

			private void updateDataBase(Tasks tasksResult) {

				List<Task> tasks = tasksResult.getTasks();

                if(tasks !=null) {
                    // insert/update row for each Task
                    for (Task task : tasks) {
                        Long id = task.getId();
                        String title = task.getTitle();
                        String duedate = task.getDue_date();
                        Long idList = task.getList_id();
                        //TODO
                    }
                }



				
			}
}