package dataAccess;

import models.AuthToken;

import java.sql.SQLException;

public class DbAuthDAO implements AuthDAO {
    private boolean initialized = false;
    private static Database db = new Database();

    @Override
    public void insert(AuthToken authToken) throws DataAccessException {
        initialize();
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (token, name) VALUES(?, ?)")) {
            preparedStatement.setString(1, authToken.getAuthToken());
            preparedStatement.setString(2, authToken.getUsername());

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
    public String find(String authToken) throws DataAccessException {
        initialize();
        var conn = db.getConnection();
        String username;

        try (var preparedStatement = conn.prepareStatement("SELECT token, name FROM auth WHERE token=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                rs.next();
                username = rs.getString("name");
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        finally {
            db.closeConnection(conn);
        }
        return username;
    }

    @Override
    public void remove(String authToken) throws DataAccessException {
        initialize();
        find(authToken);                //gotta throw if it aint there
        var conn = db.getConnection();

        try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE token=?")) {
            preparedStatement.setString(1, authToken);
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
        int count = 0;

        try (var preparedStatement = conn.prepareStatement("SELECT token, name FROM auth")) {
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

        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
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
                CREATE TABLE  IF NOT EXISTS auth (
                token VARCHAR(255) NOT NULL,
                name VARCHAR(255) NOT NULL,
                PRIMARY KEY (token)
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
