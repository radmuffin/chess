package dataAccess;

import models.Game;

import java.sql.SQLException;
import java.util.Collection;

public class DbGameDAO implements GameDAO{

    private boolean initialized = false;

    private static Database db = new Database();

    @Override
    public void insert(Game game) throws DataAccessException {
        initialize();
    }

    @Override
    public Game find(int gameID) throws DataAccessException {
        initialize();
        return null;
    }

    @Override
    public Collection<Game> findAll() throws DataAccessException {
        initialize();
        return null;
    }

    @Override
    public void claimSpot(String username, String color, int gameID) throws DataAccessException {
        initialize();
    }

    @Override
    public void updateGame(int gameID, String chessGame) throws DataAccessException {
        initialize();
    }

    @Override
    public int numGames() throws DataAccessException {
        initialize();
        return 0;
    }

    @Override
    public void clear() throws DataAccessException {
        initialize();
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
