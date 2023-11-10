package services;

import dataAccess.*;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import responses.RegisterResult;

/**
 * RegisterService Class
 */
public class RegisterService {

    private final UserDAO userDAO = new DbUserDAO();
    private final AuthDAO authDAO = new DbAuthDAO();

    /**
     * Register a new user.
     * @param request object
     * @return result of request
     */
    public RegisterResult register(RegisterRequest request) {
        RegisterResult result = new RegisterResult();

        if (!request.isComplete()) {
            result.setReturnCode(400);
            result.setMessage("Error: bad request");
            return result;
        }

        try {
            User user = new User(request.getUsername(),request.getPassword(),request.getEmail());
            userDAO.insert(user);
            result.setUsername(request.getUsername());

            AuthToken authToken = new AuthToken(result.getUsername());
            authDAO.insert(authToken);
            result.setAuthToken(authToken.getAuthToken());

            result.setReturnCode(200);
            return result;
        }
        catch (DataAccessException exception) {
            result.setReturnCode(403);
            result.setMessage("Error: already taken");
            return result;
        }
    }
}
