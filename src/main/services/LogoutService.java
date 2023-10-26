package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemAuthDAO;
import services.responses.ResponseMessage;

/**
 * LogoutService class
 */
public class LogoutService {

    private AuthDAO authDAO = new MemAuthDAO();

    /**
     * 	Logs out the user represented by the authToken.
     * @return nothing or error message
     */
    public ResponseMessage logout(String authToken) {
        ResponseMessage result = new ResponseMessage();

        try {
            authDAO.remove(authToken);
            result.setReturnCode(200);
        }
        catch (DataAccessException e) {
            result.setReturnCode(401);
            result.setMessage("Error: unauthorized");
        }
        return result;
    }
}
