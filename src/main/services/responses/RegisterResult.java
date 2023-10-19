package services.responses;

/**
 * RegisterResult class
 */
public class RegisterResult {
    private String message;
    private String username;
    private String authToken;

    /**
     * @param message if failed :)
     * @param username
     * @param authToken
     */
    public RegisterResult(String message, String username, String authToken) {
        this.message = message;
        this.username = username;
        this.authToken = authToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
