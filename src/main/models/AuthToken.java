package models;

/**
 * AuthToken class stores usernames and their respective authorization tokens
 */
public class AuthToken {
    private String authToken;
    private String username;

    /**
     * @param authToken
     * @param username
     */
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
