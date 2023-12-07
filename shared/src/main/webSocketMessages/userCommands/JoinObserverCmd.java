package webSocketMessages.userCommands;

import java.util.Objects;

public class JoinObserverCmd extends UserGameCommand{
    private int gameID;

    public JoinObserverCmd(String authToken) {
        super(authToken);
        commandType = CommandType.JOIN_OBSERVER;
    }
    public JoinObserverCmd(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinObserverCmd that)) return false;
        if (!super.equals(o)) return false;
        return gameID == that.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID);
    }
}
