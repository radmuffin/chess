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

public class ServiceTests {

    private AuthDAO authDAO = new DbAuthDAO();
    private UserDAO userDAO = new DbUserDAO();
    private GameDAO gameDAO = new MemGameDAO();
    private ClearApplicationService clearApplicationService = new ClearApplicationService();

    @Test
    public void successfulClear() throws DataAccessException {
        User user = new User("a", "b", "c");
        Game game = new Game("game");
        game.setGameID(1);
        AuthToken authToken = new AuthToken("a");

        authDAO.insert(authToken);
        userDAO.insert(user);
        gameDAO.insert(game);

        ClearApplicationService service = new ClearApplicationService();
        service.clearApplication();

        Assertions.assertEquals(0, authDAO.size(), "authDAO not cleared");
        Assertions.assertEquals(0, gameDAO.numGames(), "gameDAO not cleared");
        Assertions.assertEquals(0, userDAO.size(), "userDAO not cleared");
    }

    @Test
    public void successfulRegister() throws DataAccessException {
        clearApplicationService.clearApplication();

        RegisterRequest req = new RegisterRequest("username", "password", "e@mail.com");
        RegisterService service = new RegisterService();
        RegisterResult res = service.register(req);

        Assertions.assertEquals(200, res.getReturnCode(), "returned wrong status");
        Assertions.assertEquals(1, userDAO.size(), "no user registered");
    }

    @Test
    public void badRegisterReq() throws DataAccessException {
        clearApplicationService.clearApplication();

        RegisterRequest req = new RegisterRequest("username", "password", "e@mail.com");
        req.setUsername(null);
        RegisterService service = new RegisterService();
        RegisterResult res = service.register(req);

        Assertions.assertEquals(400, res.getReturnCode(), "returned wrong status");
        Assertions.assertEquals(0, userDAO.size(), "user registered but it shouldn't have");
    }

    @Test
    public void successfulLogin() {
        clearApplicationService.clearApplication();
        registerAndGetAuth();

        LoginRequest req = new LoginRequest("username", "password");
        LoginService service = new LoginService();
        LoginResult res = service.login(req);

        Assertions.assertEquals(200, res.getReturnCode(), "wrong status");
        Assertions.assertNotNull(res.getAuthToken(), "no authToken");
    }

    @Test
    public void wrongPassword() {
        clearApplicationService.clearApplication();
        registerAndGetAuth();

        LoginRequest req = new LoginRequest("username", "wordpass");
        LoginService service = new LoginService();
        LoginResult res = service.login(req);

        Assertions.assertEquals(401, res.getReturnCode(), "wrong status");
        Assertions.assertNull(res.getAuthToken(), "authToken returned for invalid login");
    }

    @Test
    public void successfulLogout() throws DataAccessException {
        clearApplicationService.clearApplication();
        String authToken = registerAndGetAuth();

        LogoutService service = new LogoutService();
        ResponseMessage res = service.logout(authToken);

        Assertions.assertEquals(200, res.getReturnCode(), "wrong status");
        Assertions.assertEquals(0, authDAO.size(), "token not removed");
    }

    @Test
    public void unauthorizedLogout() throws DataAccessException {
        clearApplicationService.clearApplication();
        registerAndGetAuth();

        LogoutService service = new LogoutService();
        ResponseMessage res = service.logout("super good authentication");

        Assertions.assertEquals(401, res.getReturnCode(), "wrong status");
        Assertions.assertEquals(1, authDAO.size(), "token removed erroneously");
    }

    @Test
    public void successfulGameCreation() throws DataAccessException {
        clearApplicationService.clearApplication();
        String authToken = registerAndGetAuth();

        CreateGameRequest req = new CreateGameRequest("super cool game name");
        CreateGameService service = new CreateGameService();
        CreateGameResult res = service.createGame(req, authToken);

        Assertions.assertEquals(200, res.getReturnCode(), "wrong status");
        Assertions.assertEquals(1, gameDAO.numGames(), "no game added");
    }

    @Test
    public void unauthorizedCreation() throws DataAccessException {
        clearApplicationService.clearApplication();

        CreateGameRequest req = new CreateGameRequest("super cool game name");
        CreateGameService service = new CreateGameService();
        CreateGameResult res = service.createGame(req, "try this");

        Assertions.assertEquals(401, res.getReturnCode(), "wrong status");
        Assertions.assertEquals(0, gameDAO.numGames(), "game added but why");
    }

    @Test
    public void successfulListGames() throws DataAccessException {
        clearApplicationService.clearApplication();
        String authToken = registerAndGetAuth();

        Game game = new Game("game");
        gameDAO.insert(game);

        ListGameService service = new ListGameService();
        ListGamesResult res = service.listGames(authToken);

        Assertions.assertEquals(200, res.getReturnCode(), "wrong status");
        Assertions.assertTrue(res.getGames().contains(game), "didn't return game");
    }

    @Test
    public void unauthorizedListGames() throws DataAccessException {
        clearApplicationService.clearApplication();

        Game game = new Game("game");
        game.setGameID(42);
        gameDAO.insert(game);

        ListGameService service = new ListGameService();
        ListGamesResult res = service.listGames("universal key");

        Assertions.assertEquals(401, res.getReturnCode(), "wrong status");
        Assertions.assertNull(res.getGames(), "still returned games smh");
    }

    @Test
    public void successfulJoinGame() throws DataAccessException {
        clearApplicationService.clearApplication();
        String authToken = registerAndGetAuth();

        Game game = new Game("game");
        gameDAO.insert(game);

        JoinGameRequest req = new JoinGameRequest("WHITE", game.getGameID());
        JoinGameService service = new JoinGameService();
        ResponseMessage res = service.joinGame(req, authToken);

        Assertions.assertEquals(200, res.getReturnCode(), "wrong status");
        Assertions.assertEquals("username", gameDAO.find(game.getGameID()).getWhiteUsername(), "player wasn't added");
    }

    @Test
    public void alreadyTaken() throws DataAccessException {
        clearApplicationService.clearApplication();
        String authToken = registerAndGetAuth();

        Game game = new Game("game");

        gameDAO.insert(game);

        JoinGameRequest req = new JoinGameRequest("WHITE", game.getGameID());
        JoinGameService service = new JoinGameService();
        ResponseMessage res = service.joinGame(req, authToken);

        res = service.joinGame(req, authToken); //do it again

        Assertions.assertEquals(403, res.getReturnCode(), "wrong status");
    }

    private String registerAndGetAuth() {
        RegisterRequest req = new RegisterRequest("username", "password", "e@mail.com");
        RegisterService service = new RegisterService();
        RegisterResult res = service.register(req);
        return res.getAuthToken();
    }
}
