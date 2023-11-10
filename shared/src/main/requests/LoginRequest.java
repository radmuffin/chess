package requests;

/**
 * LoginRequest class
 */
public record LoginRequest(String username, String password) {
    /**
     * @param username
     * @param password
     */
    public LoginRequest {
    }
}
