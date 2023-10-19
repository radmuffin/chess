package services.responses;

/**
 * ResponseMessage class for clear, logout, join game,
 */
public class ResponseMessage {
    private String message;

    /**
     * @param message for success or fail, success is blank
     */
    public ResponseMessage(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
