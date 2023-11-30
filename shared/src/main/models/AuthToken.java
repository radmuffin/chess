package models;

import java.util.Objects;
import java.util.UUID;

/**
 * AuthToken class stores usernames and their respective authorization tokens
 */
public class AuthToken {
    private final String authToken;
    private final String username;

    /**
     * @param username
     */
    public AuthToken(String username) {
        this.username = username;
        authToken = UUID.randomUUID().toString();

    }

    public AuthToken(String username, String auth) {
        this.username = username;
        authToken = auth;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken1 = (AuthToken) o;
        return Objects.equals(authToken, authToken1.authToken) && Objects.equals(username, authToken1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }
}
