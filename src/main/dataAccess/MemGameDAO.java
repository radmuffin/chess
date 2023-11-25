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

    private static final HashMap<Integer, Game> games = new HashMap<>();

    @Override
    public void insert(Game game) {
        game.setGameID(games.size() + 1);
        games.put(game.getGameID(), game);
    }

    @Override
    public Game find(int gameID) throws DataAccessException{
        if (!games.containsKey(gameID)) throw new DataAccessException("game doesn't exist");
        return games.get(gameID);
    }

    @Override
    public Collection<Game> findAll() {
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
    public void updateGame(int gameID, ChessGame chessGame) {
        games.get(gameID).setGame(chessGame);
    }



    @Override
    public int numGames() {
        return games.size();
    }


    @Override
    public void clear() {
        games.clear();
    }
}
