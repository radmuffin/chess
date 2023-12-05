package server;

import adapters.MoveAdapter;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMess;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WSHandler {

    private final GameDAO gameDAO = new DbGameDAO();
    private final AuthDAO authDAO = new DbAuthDAO();
    private final WSSessions sessions = new WSSessions();


    @OnWebSocketError
    public void onError(Session session, Throwable throwable) throws IOException {
        ErrorMess mess = new ErrorMess("error:" + throwable.getMessage());
        session.getRemote().sendString(new Gson().toJson(mess));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {

        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        String user = "";
        try {
            user = authDAO.find(cmd.getAuthString());
        } catch (DataAccessException e) {
            ErrorMess errorMess = new ErrorMess("authentication error :/");
            session.getRemote().sendString(new Gson().toJson(errorMess));
        }

        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> {
                JoinPlayerCmd join = new Gson().fromJson(message, JoinPlayerCmd.class);
                try {
                    LoadGameMessage loadGameMessage = null;
                    try {
                        loadGameMessage = new LoadGameMessage(gameDAO.find(join.getGameID()).getGame());
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e);
                    }
                    session.getRemote().sendString(new Gson().toJson(loadGameMessage));

                    sessions.addSessionToGame(join.getGameID(), user, session);
                    String color = "white";
                    if (join.getPlayerColor() == ChessGame.TeamColor.BLACK) color = "black";
                    Notification notification = new Notification(user + " joined as " + color);
                    sessions.gameWideMessageExclude(join.getGameID(), new Gson().toJson(notification), session);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case JOIN_OBSERVER -> {
                JoinObserverCmd join = new Gson().fromJson(message, JoinObserverCmd.class);

                LoadGameMessage loadGameMessage = null;
                try {
                    loadGameMessage = new LoadGameMessage(gameDAO.find(join.getGameId()).getGame());
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);  //http would have already found game
                }
                session.getRemote().sendString(new Gson().toJson(loadGameMessage));

                sessions.addSessionToGame(join.getGameId(), user, session);
                Notification notification = new Notification(user + " joined as observer");
                sessions.gameWideMessageExclude(join.getGameId(), new Gson().toJson(notification), session);
            }
            case MAKE_MOVE -> {
                var builder = new GsonBuilder();
                builder.registerTypeAdapter(ChessMove.class, new MoveAdapter());
                MakeMoveCmd makeMoveCmd = builder.create().fromJson(message, MakeMoveCmd.class);
                Game game = null;
                try {
                    game = gameDAO.find(makeMoveCmd.getGameID());
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
                String activeUser = game.getBlackUsername();
                if (game.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE) activeUser = game.getWhiteUsername();
                if (!Objects.equals(user, activeUser)) {
                    ErrorMess errorMess = new ErrorMess("error:it's not your turn");
                    session.getRemote().sendString(new Gson().toJson(errorMess));
                }
                ChessMove chessMove = makeMoveCmd.getMove();

                try {
                    game.getGame().makeMove(chessMove);
                    gameDAO.updateGame(makeMoveCmd.getGameID(), game.getGame());

                    LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
                    sessions.gameWideMessage(game.getGameID(), new Gson().toJson(loadGameMessage));

                    Notification notification = new Notification(user + " moved " + chessMove);
                    sessions.gameWideMessageExclude(game.getGameID(), new Gson().toJson(notification), session);
                } catch (InvalidMoveException e) {
                    ErrorMess mess = new ErrorMess("error:" + e.getMessage());
                    session.getRemote().sendString(new Gson().toJson(mess));
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }


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
