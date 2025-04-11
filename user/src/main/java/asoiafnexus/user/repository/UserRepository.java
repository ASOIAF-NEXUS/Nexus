package asoiafnexus.user.repository;

import asoiafnexus.user.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserRepository {

    HashMap<String, User> db = new HashMap<>();

    public User insert(User user) {
        db.put(user.username(), user);
        return db.get(user.username());
    }

    public User byUsername(String username) {
        return db.get(username);
    }
}
