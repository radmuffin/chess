package webSocketMessages.userCommands;

import java.util.Objects;

public class LeaveCmd extends UserGameCommand{
    private int gameID;
    public LeaveCmd(String authToken) {
        super(authToken);
        commandType = CommandType.LEAVE;
    }

    public LeaveCmd(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
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
        if (!(o instanceof LeaveCmd leaveCmd)) return false;
        if (!super.equals(o)) return false;
        return gameID == leaveCmd.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID);
    }
}
