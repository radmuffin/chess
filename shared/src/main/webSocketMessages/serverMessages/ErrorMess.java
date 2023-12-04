package webSocketMessages.serverMessages;

import java.util.Objects;

public class ErrorMess extends ServerMessage {

    private final String errorMessage;
    public ErrorMess(String message) {
        super(ServerMessageType.ERROR);
        errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorMess errorMess)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(errorMessage, errorMess.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errorMessage);
    }
}
