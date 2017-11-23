package main.java.webservice.jax.wsimport.service;

import javax.xml.ws.Endpoint;

public class StringHelperService {

	public static void main(String[] args) {
		String address = "http://localhost:7777/sh";
		Endpoint.publish(address, new StringHelperImpl());
		System.out.println("publish over");
	}

}
