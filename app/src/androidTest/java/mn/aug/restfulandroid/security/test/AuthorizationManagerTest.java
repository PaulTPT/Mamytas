package mn.aug.restfulandroid.security.test;

import java.net.URI;

import android.test.AndroidTestCase;

import mn.aug.restfulandroid.rest.Request;
import mn.aug.restfulandroid.rest.Response;
import mn.aug.restfulandroid.rest.RestClient;
import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.security.AuthorizationManager;
import mn.aug.restfulandroid.security.RequestSigner;

public class AuthorizationManagerTest extends AndroidTestCase {

	/**
	 * Verifies proper authorization of Requests for Twitter OAuth. This test
	 * makes a request to the Twitter API to ensure the Authorization header has
	 * the correct value.
	 * <p>
	 * <strong>Requires that you have already installed RESTful Android on the
	 * test device and have logged in.</strong>
	 * </p>
	 */
	public void testAuthorize() {

		URI uri = URI.create("https://api.twitter.com/1/account/verify_credentials.json");
		Request request = new Request(Method.GET, uri, null, null);
		
		RequestSigner signer = AuthorizationManager.getInstance(getContext());
		signer.authorize(request);

		RestClient client = new RestClient();
		Response response = client.execute(request);
		assertTrue(response.status == 200);

	}

}
