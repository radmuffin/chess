package passoffTests.serverTests;

import dataAccess.*;
import models.AuthToken;
import models.Game;
import models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import services.*;
import services.requests.CreateGameRequest;
import services.requests.JoinGameRequest;
import services.requests.LoginRequest;
import services.requests.RegisterRequest;
import services.responses.*;
public class DatabaseTests {

    private AuthDAO authDAO = new DbAuthDAO();
    private UserDAO userDAO = new DbUserDAO();
    private GameDAO gameDAO = new DbGameDAO();

    @Test
    public void goodAuthInsertAndFind() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);

        Assertions.assertEquals("georgie", authDAO.find(token.getAuthToken()), "unsuccessful find");
    }

    @Test
    public void duplicateAuthInsert() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);

        Assertions.assertThrows(DataAccessException.class, () ->  authDAO.insert(token), "shoulda thrown");

    }

    @Test
    public void badAuthFind() throws DataAccessException {
        authDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.find("token.getAuthToken()"), "shoulda thrown");
    }

    @Test
    public void authClear() throws DataAccessException {
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);
        authDAO.clear();

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.find(token.getAuthToken()), "shoulda thrown");
    }

    @Test
    public void goodRemove() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);
        authDAO.remove(token.getAuthToken());

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.find(token.getAuthToken()), "shoulda thrown");

    }

    @Test
    public void badRemove() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);
        authDAO.remove("totally a thing");

        Assertions.assertEquals(1, authDAO.size(), "wrong size after impotent remove");
    }

    @Test
    public void goodUserInsertAndFind() throws DataAccessException {
        userDAO.clear();
        User user = new User("joe", "password", "e@mail.com");
        userDAO.insert(user);

        Assertions.assertEquals(1, userDAO.size(), "nothing there");
        Assertions.assertEquals(user, userDAO.find(user.getUsername()), "whoopsies not found or inserted");
    }


    @Test
    public void duplicateUser() throws DataAccessException {
        userDAO.clear();
        User user = new User("joe", "password", "e@mail.com");
        userDAO.insert(user);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insert(user), "uh oh");
    }

    @Test
    public void badUserFind() throws DataAccessException {
        userDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.find("daveyJones523"), "Where'd he come from?");
    }

    @Test
    public void clearUser() throws DataAccessException {
        userDAO.clear();
        User user = new User("joe", "password", "e@mail.com");
        userDAO.insert(user);
        userDAO.clear();

        Assertions.assertEquals(0, userDAO.size(), "what's this?");
    }


}
