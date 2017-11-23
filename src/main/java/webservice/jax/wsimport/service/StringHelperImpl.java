package main.java.webservice.jax.wsimport.service;

import javax.jws.WebService;

@WebService(endpointInterface="com.java.webservice.jax.wsimport.service.StringHelper")
public class StringHelperImpl implements StringHelper {
	@Override
	public String contact(String str1, String str2) {
		return str1 + str2;
	}

}
