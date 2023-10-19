package services.requests;

/**
 * CreateGameRequest class
 */
public class CreateGameRequest {
    private String gameName;

    /**
     * @param gameName
     */
    public CreateGameRequest(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
