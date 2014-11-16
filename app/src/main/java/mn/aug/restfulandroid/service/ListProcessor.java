package mn.aug.restfulandroid.service;

import android.content.Context;

import mn.aug.restfulandroid.provider.ListsDBAccess;
import mn.aug.restfulandroid.provider.OwnershipDBAccess;
import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Listw;
import mn.aug.restfulandroid.security.AuthorizationManager;

/**
 * Created by Paul on 11/11/2014.
 */
public class ListProcessor {

    private ProcessorCallback mCallback;
    private Context mContext;
    private OwnershipDBAccess ownershipDBAccess;
    private ListsDBAccess listsDBAccess;


    public ListProcessor(Context context) {

        mContext = context;
        ownershipDBAccess = new OwnershipDBAccess(mContext);
        listsDBAccess = new ListsDBAccess(mContext);
    }


    public void putList(ProcessorCallback callback, long list_id, byte[] body) {

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
        listsDBAccess.setStatus(list_id, "post_update");
        listsDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Listw> putListMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Listw.CONTENT_URI, RestMethodFactory.Method.PUT, null, body, list_id);
        RestMethodResult<Listw> result = putListMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */
         Listw list = result.getResource();

        if (result.getStatusCode() == 200 && list_id ==list.getId()) {

            listsDBAccess.open();
            listsDBAccess.setStatus(list_id, "up_to_date");
            listsDBAccess.close();


        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }

    public void deleteList(ProcessorCallback callback, long list_id) {

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
        listsDBAccess.setStatus(list_id, "deleting");
        listsDBAccess.close();
        // (5) Call the REST method
        // Create a RESTMethod class that knows how to assemble the URL,
        // and performs the HTTP operation.

        RestMethod<Listw> deleteListMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                Listw.CONTENT_URI, RestMethodFactory.Method.DELETE, null,null,list_id);
        RestMethodResult<Listw> result = deleteListMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        if (result.getStatusCode() == 200) {

            String user = AuthorizationManager.getInstance(mContext).getUser();
            ownershipDBAccess.open();
            ownershipDBAccess.removeListFromUser(user, list_id);
            ownershipDBAccess.close();

        }

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }



}




