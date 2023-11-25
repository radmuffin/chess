package dataAccess;

import models.User;

import java.sql.SQLException;

public class DbUserDAO implements UserDAO{


    private boolean initialized = false;
    private static final Database db = new Database();

    @Override
    public void insert(User user) throws DataAccessException {
        initialize();
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());

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
    public User find(String username) throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        User user = new User(null, null, null);

        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
            preparedStatement.setString(1, username);

            try (var rs = preparedStatement.executeQuery()) {
                rs.next();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return user;
    }

    @Override
    public void clear() throws DataAccessException {
        initialize();
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
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
    public int size() throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        int size = 0;

        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user")) {

            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    ++size;
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return size;
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
                db.closeConnection(conn);
            }
        }
    }

}
