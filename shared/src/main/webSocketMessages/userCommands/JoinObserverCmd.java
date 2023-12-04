package webSocketMessages.userCommands;

import java.util.Objects;

public class JoinObserverCmd extends UserGameCommand{
    private int gameId;

    public JoinObserverCmd(String authToken) {
        super(authToken);
        commandType = CommandType.JOIN_OBSERVER;
    }
    public JoinObserverCmd(String authToken, int gameId) {
        super(authToken);
        commandType = CommandType.JOIN_OBSERVER;
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinObserverCmd that)) return false;
        if (!super.equals(o)) return false;
        return gameId == that.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameId);
    }
}
