package webservice.jax.soap;


import webservice.entity.User;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;


@WebService(endpointInterface = "webservice.jax.soap.UserService")
@HandlerChain(file = "service-handler-chain.xml")
public class UserServiceImpl implements UserService {
    private static List<User> users = new ArrayList<User>();

    public UserServiceImpl() {
        users.add(new User(1, "admin", "管理員", "111111"));
    }

    @Override
    public int add(int a, int b) {
        System.out.println("a+b=" + (a + b));
        return a + b;
    }

    @Override
    public User addUser(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User login(String username, String password) throws UserException {
        for (User user : users) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        throw new UserException("用户名或密码错误！");
    }

    @Override
    public List<User> list() {
        return users;
    }

}
