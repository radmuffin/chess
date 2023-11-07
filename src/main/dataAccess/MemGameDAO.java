package dataAccess;

import chess.ChessGame;
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
        game.setGameID(games.size() + 1);
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
        else if (Objects.equals(color, "WHITE")) {
            games.get(gameID).setWhiteUsername(username);
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        games.get(gameID).setGame(chessGame);
    }



    @Override
    public int numGames() throws DataAccessException {
        return games.size();
    }


    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
