package services.requests;

/**
 * RegisterRequest class
 */
public class RegisterRequest {

    private String username;
    private String password;
    private String email;

    /**
     * @param username
     * @param password
     * @param email
     */
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Boolean isComplete() {
        return (username != null && password != null && email != null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

}
