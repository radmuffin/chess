package dataAccess;

import models.AuthToken;
import java.util.HashMap;

/**
 * AuthDAO class
 */
public class MemAuthDAO implements AuthDAO {

    private static final HashMap<String, AuthToken> tokens = new HashMap<>();

    @Override
    public void insert(AuthToken authToken) {
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

    public int size() {
        return tokens.size();
    }

    @Override
    public void clear() {
        tokens.clear();
    }
}
