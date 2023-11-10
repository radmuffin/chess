package services;

import dataAccess.*;
import models.AuthToken;
import requests.LoginRequest;
import responses.LoginResult;

import java.util.Objects;

/**
 * LoginService class
 */
public class LoginService {

    private final AuthDAO authDAO = new DbAuthDAO();
    private final UserDAO userDAO = new DbUserDAO();

    /**
     * Logs in an existing user (returns a new authToken).
     * @param request
     * @return
     */
    public LoginResult login(LoginRequest request) {
        LoginResult result = new LoginResult();

        try {   //throws exception if user dne
            if (!Objects.equals(userDAO.find(request.username()).getPassword(), request.password())) {
                result.setReturnCode(401);
                result.setMessage("Error: unauthorized");
                return result;
            }
            AuthToken authToken = new AuthToken(request.username());
            authDAO.insert(authToken);

            result.setAuthToken(authToken.getAuthToken());
            result.setUsername(request.username());
            result.setReturnCode(200);
        }
        catch (DataAccessException exception) {
            result.setReturnCode(401);
            result.setMessage("Error: user doesn't exist");
        }
        return result;
    }
}
