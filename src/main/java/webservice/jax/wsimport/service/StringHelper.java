
package main.java.webservice.jax.wsimport.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface StringHelper {
	@WebResult(name="contactResult")
	public String contact(@WebParam(name="str1")String str1, 
			@WebParam(name="str2")String str2);
}
