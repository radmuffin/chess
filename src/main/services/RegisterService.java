package services;

import dataAccess.*;
import models.AuthToken;
import models.User;
import services.requests.RegisterRequest;
import services.responses.RegisterResult;

/**
 * RegisterService Class
 */
public class RegisterService {

    private UserDAO userDAO = new MemUserDAO();
    private AuthDAO authDAO = new MemAuthDAO();

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
