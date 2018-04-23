package webservice.jax.soap;


import webservice.entity.User;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;


@WebService
public interface UserService {
    @WebResult(name = "addResult")
    public int add(@WebParam(name = "a") int a, @WebParam(name = "b") int b);

    @WebResult(name = "user")
    public User addUser(@WebParam(name = "user") User user);

    @WebResult(name = "user")
    public User login(@WebParam(name = "username") String username,
                      @WebParam(name = "password") String password)
            throws UserException;

    @WebResult(name = "user")
    public List<User> list();
}
