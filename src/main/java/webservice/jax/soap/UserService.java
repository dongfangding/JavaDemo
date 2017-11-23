package main.java.webservice.jax.soap;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import main.java.webservice.entity.User;

@WebService
public interface UserService {
	@WebResult(name="addResult")
	public int add(@WebParam(name="a")int a,@WebParam(name="b")int b);
	
	@WebResult(name="user")
	public User addUser(@WebParam(name="user")User user);
	
	@WebResult(name="user")
	public User login(@WebParam(name="username")String username,
					  @WebParam(name="password")String password)
			throws UserException;
	
	@WebResult(name="user")
	public List<User> list();
}
