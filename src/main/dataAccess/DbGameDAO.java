package dataAccess;

import adapters.PosAdapter;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGameImp;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import adapters.BoardAdapter;
import models.Game;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class DbGameDAO implements GameDAO{

    private boolean initialized = false;

    private static final Database db = new Database();

    @Override
    public void insert(Game game) throws DataAccessException {
        initialize();
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("INSERT INTO game (name, whiteUser, blackUser, game) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.getGameName());
            preparedStatement.setString(2, game.getWhiteUsername());
            preparedStatement.setString(3, game.getBlackUsername());
            preparedStatement.setString(4, new Gson().toJson(game.getGame()));

            preparedStatement.executeUpdate();

            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                game.setGameID(resultSet.getInt(1));
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Game find(int gameID) throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        Game game = new Game(gameID, null, null, null, null);

        try (var preparedStatement = conn.prepareStatement("SELECT name, whiteUser, blackUser, game FROM game WHERE id=?")) {
            preparedStatement.setInt(1, gameID);

            try (var rs = preparedStatement.executeQuery()) {
                rs.next();
                game.setGameName(rs.getString("name"));
                game.setWhiteUsername(rs.getString("whiteUser"));
                game.setBlackUsername(rs.getString("blackUser"));

                var json = rs.getString("game");
                var builder = new GsonBuilder();
                builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());
                builder.registerTypeAdapter(ChessPosition.class, new PosAdapter());
                game.setGame(builder.create().fromJson(json, ChessGameImp.class));
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return game;
    }

    @Override
    public Collection<Game> findAll() throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        ArrayList<Game> games = new ArrayList<>();

        try (var preparedStatement = conn.prepareStatement("SELECT id, name, whiteUser, blackUser, game FROM game")) {

            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Game game = new Game(null);

                    game.setGameID(rs.getInt("id"));
                    game.setGameName(rs.getString("name"));
                    game.setWhiteUsername(rs.getString("whiteUser"));
                    game.setBlackUsername(rs.getString("blackUser"));

                    var json = rs.getString("game");
                    var builder = new GsonBuilder();
                    builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());
                    builder.registerTypeAdapter(ChessPosition.class, new PosAdapter());
                    game.setGame(builder.create().fromJson(json, ChessGameImp.class));

                    games.add(game);
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return games;
    }

    @Override
    public void claimSpot(String username, String color, int gameID) throws DataAccessException {
        initialize();
        find(gameID);                       //gotta check that it's there :))
        var conn = db.getConnection();

        if (Objects.equals(color, "WHITE")) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUser=? WHERE id=?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }
            catch (SQLException ex) {
                throw new DataAccessException(ex.getMessage());
            }
            finally {
                db.closeConnection(conn);
            }
        }
        else if (Objects.equals(color, "BLACK")) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET blackUser=? WHERE id=?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);

                preparedStatement.executeUpdate();
            }
            catch (SQLException ex) {
                throw new DataAccessException(ex.getMessage());
            }
            finally {
                db.closeConnection(conn);
            }
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        initialize();
        find(gameID);                       //gotta check that it's there :))
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("UPDATE game SET game=? WHERE id=?")) {
            preparedStatement.setString(1, new Gson().toJson(chessGame));
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }



    }

    @Override
    public int numGames() throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        int count = 0;

        try (var preparedStatement = conn.prepareStatement("SELECT id, name, whiteUser, blackUser, game FROM game")) {

            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    ++count;
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return count;
    }

    @Override
    public void clear() throws DataAccessException {
        initialize();
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
    }

    private void initialize() throws DataAccessException {
        if (!initialized) {
            var conn = db.getConnection();
            var createAuthTable = """
                CREATE TABLE  IF NOT EXISTS game (
                id INT NOT NULL AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL,
                whiteUser VARCHAR(255),
                blackUser VARCHAR(255),
                game LONGTEXT,
                PRIMARY KEY (id)
            )""";
            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
                initialized = true;
            }
            catch (SQLException ex) {
                throw new DataAccessException(ex.getMessage());
            }
            finally {
                db.closeConnection(conn);
            }
        }
    }

}
