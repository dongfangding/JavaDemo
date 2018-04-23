package webservice.jax.soap;

import javax.xml.ws.Endpoint;

public class UserServicePublish {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8889/us", new UserServiceImpl());
        System.out.println("publish over");
    }
}
