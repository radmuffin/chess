package webSocketMessages.ServerMessages;

import webSocketMessages.serverMessages.ServerMessage;

public class ErrorMess extends ServerMessage {

    private final String errorMessage;
    public ErrorMess(String message) {
        super(ServerMessageType.ERROR);
        errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
