package responses;

/**
 * ResponseMessage class for clear, logout, join game,
 */
public class ResponseMessage {
    private String message;
    private transient int returnCode;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
