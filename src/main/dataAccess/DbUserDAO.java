package dataAccess;

import models.User;

import java.sql.SQLException;

public class DbUserDAO implements UserDAO{


    private boolean initialized = false;
    private static Database db = new Database();

    @Override
    public void insert(User user) throws DataAccessException {
        initialize();
    }

    @Override
    public User find(String username) throws DataAccessException {
        initialize();
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        initialize();
    }

    @Override
    public int size() throws DataAccessException {
        initialize();
        return 0;
    }

    private void initialize() throws DataAccessException {
        if (!initialized) {
            var conn = db.getConnection();
            var createAuthTable = """
                CREATE TABLE  IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";
            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
                initialized = true;
            }
            catch (SQLException ex) {
                throw new DataAccessException(ex.getMessage());
            }
            finally {
                db.returnConnection(conn);
            }
        }
    }

}
