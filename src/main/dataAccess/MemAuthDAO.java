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
        tokens.put(authToken.getAuthToken(), authToken);
    }


    @Override
    public String find(String authToken) throws DataAccessException {
        if (!tokens.containsKey(authToken)) throw new DataAccessException("token doesn't exist");
        return tokens.get(authToken).getUsername();
    }



    @Override
    public void remove(String authToken) throws DataAccessException {
        if (!tokens.containsKey(authToken)) throw new DataAccessException("doesn't exist");
        tokens.remove(authToken);
    }


    @Override
    public void clear() throws DataAccessException {
        tokens.clear();
    }
}
