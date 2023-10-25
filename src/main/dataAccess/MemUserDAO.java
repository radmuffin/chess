package dataAccess;

import models.User;

import java.util.HashMap;

/**
 * UserDAO
 */
public class MemUserDAO implements UserDAO {

    private static HashMap<String, User> users = new HashMap<>();

    @Override
    public void insert(User user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) throw new DataAccessException("user already exists");
        users.put(user.getUsername(), user);
    }

    @Override
    public User find(String username) throws DataAccessException {
        if (!users.containsKey(username)) throw new DataAccessException("user doesn't exist");
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }


}
