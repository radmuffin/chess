package dataAccess;

import models.Game;

import java.util.Collection;

/**
 * GameDAO class
 */
public class GameDAO {

    /**
     * Inserts game into database
     * @param game for insertion
     * @throws DataAccessException if error
     */
    public void insert(Game game) throws DataAccessException {

    }

    /**
     * A method for retrieving a specified game from the database by gameID.
     * @param gameID of game
     * @return game
     * @throws DataAccessException if error
     */
    public Game find(int gameID) throws DataAccessException{
        return null;
    }

    /**
     * A method for retrieving all games from the database
     * @return collection of games
     * @throws DataAccessException if error
     */
    public Collection<Game> findAll() throws DataAccessException{
        return null;
    }

    /**
     * A method/methods for claiming a spot in the game.
     * The player's username is provided and should be saved as either the whitePlayer or blackPlayer
     * in the database.
     * @param username of player
     * @throws DataAccessException if error
     */
    public void claimSpot(String username) throws DataAccessException{

    }

    /**
     * A method for updating a chessGame in the database.
     * It should replace the chessGame string corresponding to a given gameID with a new chessGame string.
     * @param gameID of game
     * @param chessGame new configuration
     * @throws DataAccessException if error
     */
    public void updateGame(int gameID, String chessGame) throws DataAccessException {

    }

    /**
     * A method for removing a game from the database
     * @param gameID of game to remove
     * @throws DataAccessException if error
     */
    public void removeGame(int gameID) throws DataAccessException {

    }

    /**
     * A method for clearing all data from the database
     * @throws DataAccessException if error
     */
    public void clear() throws DataAccessException {

    }
}
