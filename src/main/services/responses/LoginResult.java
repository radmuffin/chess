package services.responses;

/**
 * LoginResult class
 */
public class LoginResult {
    private String message;
    private String authToken;
    private String username;

    /**
     * @param message if failed login
     * @param authToken
     * @param username
     */
    public LoginResult(String message, String authToken, String username) {
        this.message = message;
        this.authToken = authToken;
        this.username = username;
    }
}
