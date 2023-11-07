package passoffTests.serverTests;

import chess.ChessBoard;
import chess.ChessBoardImp;
import chess.ChessGame;
import chess.ChessGameImp;
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

import java.util.ArrayList;
import java.util.Collection;

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
    public void goodAuthRemove() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);
        authDAO.remove(token.getAuthToken());

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.find(token.getAuthToken()), "shoulda thrown");

    }

    @Test
    public void badAuthRemove() throws DataAccessException {
        authDAO.clear();
        AuthToken token = new AuthToken("georgie");
        authDAO.insert(token);

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.remove("totally a thing"));
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


    @Test
    public void goodGameInsertAndFind() throws DataAccessException {
        gameDAO.clear();
        Game game = new Game("epicChess");
        ChessBoard board = new ChessBoardImp();
        board.resetBoard();
        ChessGame chessGame = new ChessGameImp();
        chessGame.setBoard(board);
        game.setGame(chessGame);
        gameDAO.insert(game);

        Assertions.assertEquals(game, gameDAO.find(game.getGameID()));
    }

    @Test
    public void badGameInsert() throws DataAccessException {
        gameDAO.clear();
        Game game = new Game(null);

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.insert(game), "let the nameless game in");
    }

    @Test
    public void badGameFind() throws DataAccessException {
        gameDAO.clear();

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.find(47), "found nonexistant game");
    }

    @Test
    public void goodGameFindAll() throws DataAccessException {
        gameDAO.clear();

        Game game = new Game("epicChess");
        ChessBoard board = new ChessBoardImp();
        board.resetBoard();
        ChessGame chessGame = new ChessGameImp();
        chessGame.setBoard(board);
        game.setGame(chessGame);
        gameDAO.insert(game);

        Game game2 = new Game("superEpicChess");
        game2.setGame(chessGame);
        gameDAO.insert(game2);

        Game game3 = new Game("superDuperEpicChess");
        game3.setGame(chessGame);
        gameDAO.insert(game3);

        ArrayList<Game> games = new ArrayList<>();
        games.add(game);
        games.add(game2);
        games.add(game3);

        Assertions.assertIterableEquals(games, gameDAO.findAll(), "why not same");
    }

    @Test
    public void goodClaimSpot() throws DataAccessException {
        gameDAO.clear();

        Game game = new Game("epicChess");
        ChessBoard board = new ChessBoardImp();
        board.resetBoard();
        ChessGame chessGame = new ChessGameImp();
        chessGame.setBoard(board);
        game.setGame(chessGame);
        gameDAO.insert(game);

        gameDAO.claimSpot("alfred", null, game.getGameID());

        Assertions.assertNull(gameDAO.find(game.getGameID()).getWhiteUsername(), "how'd that get there");
        Assertions.assertNull(gameDAO.find(game.getGameID()).getBlackUsername(), "bad");

        gameDAO.claimSpot("alfred", "BLACK", game.getGameID());

        Assertions.assertEquals("alfred", gameDAO.find(game.getGameID()).getBlackUsername());
    }

    @Test
    public void badClaimSpot() throws DataAccessException{
        gameDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.claimSpot("bob", "WHITE", 74));
    }

}
