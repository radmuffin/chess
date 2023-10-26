package services;

import dataAccess.*;
import models.AuthToken;
import services.requests.LoginRequest;
import services.responses.LoginResult;

import java.util.Objects;

/**
 * LoginService class
 */
public class LoginService {

    private AuthDAO authDAO = new MemAuthDAO();
    private UserDAO userDAO = new MemUserDAO();

    /**
     * Logs in an existing user (returns a new authToken).
     * @param request
     * @return
     */
    public LoginResult login(LoginRequest request) {
        LoginResult result = new LoginResult();

        try {   //throws exception if user dne
            if (!Objects.equals(userDAO.find(request.getUsername()).getPassword(), request.getPassword())) {
                result.setReturnCode(401);
                result.setMessage("Error: unauthorized");
                return result;
            }
            AuthToken authToken = new AuthToken(request.getUsername());
            authDAO.insert(authToken);

            result.setAuthToken(authToken.getAuthToken());
            result.setUsername(request.getUsername());
            result.setReturnCode(200);
        }
        catch (DataAccessException exception) {
            result.setReturnCode(401);
            result.setMessage("Error: user doesn't exist");
        }
        return result;
    }
}
