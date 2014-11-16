package mn.aug.restfulandroid.service;

import android.content.Context;

import java.util.List;

import mn.aug.restfulandroid.provider.ListsDBAccess;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Lists;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.util.Logger;

/**
 * The TaskProcessor is a POJO for processing timeline requests.
 * For this pattern, there is one Processor for each resource type.
 *
 * @author Peter Pascale
 */
public class ListsProcessor {


    private ProcessorCallback mCallback;
    private Context mContext;
    private OwnershipDBAccess ownershipDBAccess;
    private ListsDBAccess listsDBAccess;


    public ListsProcessor(Context context) {

        mContext = context;
        ownershipDBAccess = new OwnershipDBAccess(mContext);
        listsDBAccess = new ListsDBAccess(mContext);
    }


    void getLists(ProcessorCallback callback) {

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
        listsDBAccess.open();
        List<Long> list_ids = listsDBAccess.retrieveAllLists();
        if (list_ids != null) for (Long id : list_ids) {
            listsDBAccess.setStatus(id, "updating");
        }
        listsDBAccess.close();

        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Lists> getTasksMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Lists.CONTENT_URI, Method.GET, null, null, 0);
        RestMethodResult<Lists> result = getTasksMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        Logger.debug("lists", String.valueOf(result.getStatusCode()));

        if (result.getStatusCode() == 200) {
            updateDataBase(result.getResource());
        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }


    public void postList(ProcessorCallback callback, long list_id, byte[] body) {

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
        listsDBAccess.open();
        listsDBAccess.setStatus(list_id, "uploading");
        listsDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Listw> postListMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Lists.CONTENT_URI, Method.POST, null, body, 0);
        RestMethodResult<Listw> result = postListMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */


        if (result.getStatusCode() == 200) {

            String user = AuthorizationManager.getInstance(mContext).getUser();
            ownershipDBAccess.open();
            ownershipDBAccess.removeListFromUser(user, list_id);
            Listw list = result.getResource();
            ownershipDBAccess.addListGetId(user, list);
            ownershipDBAccess.close();
        }


    // (9) Operation complete callback to Service

    callback.send(result.getStatusCode());

}

    //TODO
    private void updateDataBase(Lists listsResult) {

        List<Listw> lists = listsResult.getLists();

        String user = AuthorizationManager.getInstance(mContext).getUser();

        if (lists != null && user != null) {
            // insert/update row for each Task
            listsDBAccess.open();
            ownershipDBAccess.open();
            for (Listw list : lists) {

                if (!listsDBAccess.ListIsInDB(list.getId())) {
                    ownershipDBAccess.addList(user, list);
                } else {
                    listsDBAccess.updateList(list);
                    listsDBAccess.setStatus((int) list.getId(), "up_to_date");
                }

                }


            List<Long> list_ids = listsDBAccess.retrieveListsWithState("updating");
            if (list_ids != null) for (long id : list_ids) {
                ownershipDBAccess.removeListFromUser(user, id);
            }
            listsDBAccess.close();
            ownershipDBAccess.close();
        }


    }


}