package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMess;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import java.io.IOException;

@WebSocket
public class WSHandler {

    private final GameDAO gameDAO = new DbGameDAO();
    private final AuthDAO authDAO = new DbAuthDAO();
    private final WSSessions sessions = new WSSessions();
    @OnOpen
    public void onOpen() {

    }

    @OnClose
    public void onClose() {

    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {

        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);

        try {
            authDAO.find(cmd.getAuthString());
        } catch (DataAccessException e) {
            ErrorMess errorMess = new ErrorMess("authentication error :/");
            session.getRemote().sendString(new Gson().toJson(errorMess));
        }

        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> {
                JoinPlayerCmd join = new Gson().fromJson(message, JoinPlayerCmd.class);
                try {
                    String user = authDAO.find(join.getAuthString());
                    sessions.addSessionToGame(join.getGameID(), user, session);
                    String color = "white";
                    if (join.getPlayerColor() == ChessGame.TeamColor.BLACK) color = "black";
                    for (Session s : sessions.getGameMembers(join.getGameID()).values()) {
                        // TODO: 12/2/2023 send load game
                        Notification notification = new Notification(user + " joined as " + color);
                        s.getRemote().sendString(new Gson().toJson(notification));
                    }
                } catch (DataAccessException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case JOIN_OBSERVER -> {
                JoinObserverCmd join = new Gson().fromJson(message, JoinObserverCmd.class);
            }
            case MAKE_MOVE -> {
                MakeMoveCmd makeMoveCmd = new Gson().fromJson(message, MakeMoveCmd.class);
            }
            case LEAVE -> {
                LeaveCmd leaveCmd = new Gson().fromJson(message, LeaveCmd.class);
            }
            case RESIGN -> {
                ResignCmd resignCmd = new Gson().fromJson(message, ResignCmd.class);
            }
        }

    }

    private void sendError(String errMess, int gameID, String auth) {
        try {
            String user = authDAO.find(auth);
            Session session = sessions.getGameMembers(gameID).get(user);
            ErrorMess mess = new ErrorMess(errMess);
            session.getRemote().sendString(new Gson().toJson(mess));
        } catch (DataAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
