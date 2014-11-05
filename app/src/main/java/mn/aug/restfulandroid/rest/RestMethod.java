package mn.aug.restfulandroid.rest;

import mn.aug.restfulandroid.rest.resource.Resource;

public interface RestMethod<T extends Resource>{

	public RestMethodResult<T> execute();
}
