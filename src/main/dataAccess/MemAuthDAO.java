package dataAccess;

import models.AuthToken;
import java.util.HashMap;
import java.util.Objects;

/**
 * AuthDAO class
 */
public class MemAuthDAO implements AuthDAO {

    private static HashMap<String, AuthToken> tokens = new HashMap<>();

    @Override
    public void insert(AuthToken authToken) throws DataAccessException {
        tokens.put(authToken.getUsername(), authToken);
    }


    @Override
    public AuthToken find(String username) throws DataAccessException {
        if (!tokens.containsKey(username)) throw new DataAccessException("no authToken for user");
        return tokens.get(username);
    }



    @Override
    public void remove(String username) throws DataAccessException {
        if (!tokens.containsKey(username)) throw new DataAccessException("no authToken for user");
        tokens.remove(username);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException{
        for (AuthToken a : tokens.values()) {
            if (Objects.equals(a.getAuthToken(), authToken)) return a.getUsername();
        }
        throw new DataAccessException("no corresponding user");
    }


    @Override
    public void clear() throws DataAccessException {
        tokens.clear();
    }
}
