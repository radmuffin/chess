package webSocketMessages.userCommands;

import java.util.Objects;

public class ResignCmd extends  UserGameCommand{
    private int gameID;

    public ResignCmd(String authToken) {
        super(authToken);
        commandType = CommandType.RESIGN;
    }

    public ResignCmd(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;
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
        if (!(o instanceof ResignCmd resignCmd)) return false;
        if (!super.equals(o)) return false;
        return gameID == resignCmd.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID);
    }
}
