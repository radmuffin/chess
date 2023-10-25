package dataAccess;

import models.Game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * GameDAO class
 */
public class MemGameDAO implements GameDAO {

    private static HashMap<Integer, Game> games = new HashMap<>();

    @Override
    public void insert(Game game) throws DataAccessException {
        games.put(game.getGameID(), game);
    }

    @Override
    public Game find(int gameID) throws DataAccessException{
        if (!games.containsKey(gameID)) throw new DataAccessException("game doesn't exist");
        return games.get(gameID);
    }

    @Override
    public Collection<Game> findAll() throws DataAccessException{
        return games.values();
    }

    @Override
    public void claimSpot(String username, String color, int gameID) throws DataAccessException{
        if (!games.containsKey(gameID)) throw new DataAccessException("game doesn't exist");
        if (Objects.equals(color, "BLACK")) {
            games.get(gameID).setBlackUsername(username);
        }
        else {
            games.get(gameID).setWhiteUsername(username);
        }
    }

    @Override
    public void updateGame(int gameID, String chessGame) throws DataAccessException {
        games.get(gameID).setGameName(chessGame);
    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {
        games.remove(gameID);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
