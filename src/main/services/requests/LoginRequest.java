package services.requests;

/**
 * LoginRequest class
 */
public class LoginRequest {
    private String username;
    private String password;

    /**
     * @param username
     * @param password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
