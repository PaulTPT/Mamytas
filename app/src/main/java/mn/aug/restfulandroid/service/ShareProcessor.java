package mn.aug.restfulandroid.service;

import android.content.Context;
import android.net.Uri;

import mn.aug.restfulandroid.rest.RestMethod;
import mn.aug.restfulandroid.rest.RestMethodFactory;
import mn.aug.restfulandroid.rest.RestMethodResult;
import mn.aug.restfulandroid.rest.resource.Listw;

/**
 * Created by Paul on 11/11/2014.
 */
public class ShareProcessor {

    public static final String LIST_ID_EXTRA ="list_id";
    public static final String AUTHORITY="WUNDERLIST";
    public static final String PATH="SHARE";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PATH);


    private ProcessorCallback mCallback;
    private Context mContext;


    public ShareProcessor(Context context) {

        mContext = context;
           }


    public void shareList(ProcessorCallback callback, long list_id, byte[] body) {

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

        RestMethod<Listw> shareListMethod = RestMethodFactory.getInstance(mContext).getRestMethod(
                this.CONTENT_URI, RestMethodFactory.Method.POST, null, body, list_id);
        RestMethodResult<Listw> result = shareListMethod.execute();

				/*
                 * (8) Insert-Update the ContentProvider status, and insert the result
				 * on success Parsing the JSON response (on success) and inserting into
				 * the content provider
				 */

        // (9) Operation complete callback to Service

        callback.send(result.getStatusCode());

    }



}




