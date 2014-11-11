package mn.aug.restfulandroid.rest;

import java.util.List;
import java.util.Map;

import android.content.Context;

import mn.aug.restfulandroid.rest.resource.Resource;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.security.RequestSigner;
import mn.aug.restfulandroid.util.Logger;


public abstract class AbstractRestMethod<T extends Resource> implements RestMethod<T> {

	private static final String DEFAULT_ENCODING = "UTF-8";
    private Request request;

	public RestMethodResult<T> execute() {

		request = buildRequest();
		if (requiresAuthorization()) {
			RequestSigner signer = AuthorizationManager.getInstance(getContext());
			signer.authorize(request);
		}
		Response response = doRequest(request);
		return buildResult(response);
	}
	
	protected abstract Context getContext();

	/**
	 * Subclasses can overwrite for full control, eg. need to do special
	 * inspection of response headers, etc.
	 * 
	 * @param response
	 * @return
	 */
	protected RestMethodResult<T> buildResult(Response response) {

		int status = response.status;
		String statusMsg = "";
		String responseBody = null;
		T resource = null;

		try {
			responseBody = new String(response.body, getCharacterEncoding(response.headers));
            if(status==200 && request.getMethod()!= RestMethodFactory.Method.DELETE)
			    resource = parseResponseBody(responseBody);

		} catch (Exception ex) {
			// TODO Should we set some custom status code?
			status += 500; // spec only defines up to 505
			statusMsg = ex.getMessage();
            ex.printStackTrace();
        }
		return new RestMethodResult<T>(status, statusMsg, resource);
	}

	protected abstract Request buildRequest();
	
	protected boolean requiresAuthorization() {
		return true;
	}

	protected abstract T parseResponseBody(String responseBody) throws Exception;

	private Response doRequest(Request request) {

		RestClient client = new RestClient();
		return client.execute(request);
	}

	private String getCharacterEncoding(Map<String, List<String>> headers) {
		// TODO get value from headers
		return DEFAULT_ENCODING;
	}

}
