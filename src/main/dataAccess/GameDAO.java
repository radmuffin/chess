package dataAccess;

import chess.ChessGame;
import models.Game;

import java.util.Collection;

public interface GameDAO {
    /**
     * Inserts game into database
     *
     * @param game for insertion
     * @throws DataAccessException if error
     */
    void insert(Game game) throws DataAccessException;

    /**
     * A method for retrieving a specified game from the database by gameID.
     *
     * @param gameID of game
     * @return game
     * @throws DataAccessException if error
     */
    Game find(int gameID) throws DataAccessException;

    /**
     * A method for retrieving all games from the database
     *
     * @return collection of games
     * @throws DataAccessException if error
     */
    Collection<Game> findAll() throws DataAccessException;

    /**
     * A method/methods for claiming a spot in the game.
     * The player's username is provided and should be saved as either the whitePlayer or blackPlayer
     * in the database.
     *
     * @param username of player
     * @param color requested
     * @param gameID of game
     * @throws DataAccessException if error
     */
    void claimSpot(String username, String color, int gameID) throws DataAccessException;

    /**
     * A method for updating a chessGame in the database.
     * It should replace the chessGame string corresponding to a given gameID with a new chessGame string.
     *
     * @param gameID    of game
     * @param chessGame new configuration
     * @throws DataAccessException if error
     */
    void updateGame(int gameID, ChessGame chessGame) throws DataAccessException;


    int numGames() throws DataAccessException;

    /**
     * A method for clearing all data from the database
     *
     * @throws DataAccessException if error
     */
    void clear() throws DataAccessException;
}
