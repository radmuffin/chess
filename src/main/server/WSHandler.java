package server;

import adapters.MoveAdapter;
import chess.ChessGame;
import chess.ChessMove;
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
            return;
        }

        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> play(session, message, user);
            case JOIN_OBSERVER -> observe(session, message, user);
            case MAKE_MOVE -> move(session, message, user);
            case LEAVE -> leave(message, user);
            case RESIGN -> resign(session, message, user);
        }

    }

    private void play(Session session, String message, String user) throws IOException {
        JoinPlayerCmd join = new Gson().fromJson(message, JoinPlayerCmd.class);

        Game game = null;
        try {
            game = gameDAO.find(join.getGameID());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);  //http should've found game
        }

        switch (join.getPlayerColor()) {
            case WHITE -> {
                if (!Objects.equals(game.getWhiteUsername(), user)) {
                    ErrorMess errorMess = new ErrorMess("error: spot taken");
                    session.getRemote().sendString(new Gson().toJson(errorMess));
                    return;
                }
            }
            case BLACK -> {
                if (!Objects.equals(game.getBlackUsername(), user)) {
                    ErrorMess errorMess = new ErrorMess("error: spot taken");
                    session.getRemote().sendString(new Gson().toJson(errorMess));
                    return;
                }
            }
        }

        try {
            LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
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

    private void observe(Session session, String message, String user) throws IOException {
        JoinObserverCmd join = new Gson().fromJson(message, JoinObserverCmd.class);

        LoadGameMessage loadGameMessage = null;
        try {
            loadGameMessage = new LoadGameMessage(gameDAO.find(join.getGameID()).getGame());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);  //http would have already found game
        }
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        sessions.addSessionToGame(join.getGameID(), user, session);
        Notification notification = new Notification(user + " joined as observer");
        sessions.gameWideMessageExclude(join.getGameID(), new Gson().toJson(notification), session);
    }

    private void move(Session session, String message, String user) throws IOException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessMove.class, new MoveAdapter());
        MakeMoveCmd makeMoveCmd = builder.create().fromJson(message, MakeMoveCmd.class);
        Game game = null;
        try {
            game = gameDAO.find(makeMoveCmd.getGameID());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ChessGame chessGame = game.getGame();
        String activeUser = getActiveUser(game, chessGame);
        if (gameOver(session, game)) return;

        if (!Objects.equals(user, activeUser)) {
            ErrorMess errorMess = new ErrorMess("error:it's not your turn");
            session.getRemote().sendString(new Gson().toJson(errorMess));
            return;
        }
        ChessMove chessMove = makeMoveCmd.getMove();

        try {
            chessGame.makeMove(chessMove);

            Notification closingNote = checkTheMates(chessGame, game, user);

            LoadGameMessage loadGameMessage = new LoadGameMessage(chessGame);
            sessions.gameWideMessage(game.getGameID(), new Gson().toJson(loadGameMessage));
            gameDAO.updateGame(makeMoveCmd.getGameID(), chessGame);

            Notification notification = new Notification(user + " moved " + chessMove);
            sessions.gameWideMessageExclude(game.getGameID(), new Gson().toJson(notification), session);
            if (closingNote != null) sessions.gameWideMessage(game.getGameID(), new Gson().toJson(closingNote));
        } catch (InvalidMoveException e) {
            ErrorMess mess = new ErrorMess("error:" + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(mess));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(String message, String user) throws IOException {
        LeaveCmd leaveCmd = new Gson().fromJson(message, LeaveCmd.class);
        sessions.removeSessionFromGame(leaveCmd.getGameID(), user);

        Notification notification = new Notification(user + " left");
        sessions.gameWideMessage(leaveCmd.getGameID(), new Gson().toJson(notification));
    }

    private void resign(Session session, String message, String user) throws IOException {
        ResignCmd resignCmd = new Gson().fromJson(message, ResignCmd.class);
        try {
            Game game = gameDAO.find(resignCmd.getGameID());
            ChessGame chessGame = game.getGame();

            if (gameOver(session, game)) return;

            String otherPlayer = game.getBlackUsername();
            ChessGame.GameState winner = ChessGame.GameState.BLACK_WON;
            if (Objects.equals(otherPlayer, user)) {
                otherPlayer = game.getWhiteUsername();
                winner = ChessGame.GameState.WHITE_WON;
            }
            else if (!Objects.equals(user, game.getWhiteUsername())) {      //silly observer
                ErrorMess mess = new ErrorMess("error: you weren't even a player");
                session.getRemote().sendString(new Gson().toJson(mess));
                return;
            }
            chessGame.setGameState(winner);
            gameDAO.updateGame(resignCmd.getGameID(), chessGame);

            Notification notification = new Notification(user + " resigned, " + otherPlayer + " wins.");
            sessions.gameWideMessage(resignCmd.getGameID(), new Gson().toJson(notification));

        } catch (DataAccessException e) {
            ErrorMess mess = new ErrorMess("error:" + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(mess));
        }
    }

    private static boolean gameOver(Session session, Game game) throws IOException {
        switch (game.getGame().getGameState()) {
            case STALEMATE, WHITE_WON, BLACK_WON ->  {
                ErrorMess mess = new ErrorMess("error: game over bro");
                session.getRemote().sendString(new Gson().toJson(mess));
                return true;
            }
        }
        return false;
    }

    private static Notification checkTheMates(ChessGame chessGame, Game game, String user) {
        Notification closingNote = null;
        if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
            closingNote = new Notification(getActiveUser(game, chessGame) + " is in checkmate. " + user + " wins, gg y'all");
            switch (getInactiveUserColor(chessGame)) {
                case WHITE -> chessGame.setGameState(ChessGame.GameState.WHITE_WON);
                case BLACK -> chessGame.setGameState(ChessGame.GameState.BLACK_WON);
            }
        }
        else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
            closingNote = new Notification(getActiveUser(game, chessGame) + " is in check 0_0");
            switch (getInactiveUserColor(chessGame)) {
                case WHITE -> chessGame.setGameState(ChessGame.GameState.BLACK_CHECKED);
                case BLACK -> chessGame.setGameState(ChessGame.GameState.WHITE_CHECKED);
            }
        }
        else if (chessGame.isInStalemate(getInactiveUserColor(chessGame))) {
            closingNote = new Notification(user + " is in stalemate. nobody wins, this sucks fr");
            chessGame.setGameState(ChessGame.GameState.STALEMATE);
        }
        return closingNote;
    }

    private static ChessGame.TeamColor getInactiveUserColor(ChessGame chessGame) {
        ChessGame.TeamColor justWent = ChessGame.TeamColor.BLACK;
        if (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK) justWent = ChessGame.TeamColor.WHITE;
        return justWent;
    }

    private static String getActiveUser(Game game, ChessGame chessGame) {
        String activeUser = game.getBlackUsername();
        if (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) activeUser = game.getWhiteUsername();
        return activeUser;
    }

}
