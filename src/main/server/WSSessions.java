package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;

public class WSSessions {
    //gameID, username, session
    private final HashMap<Integer, HashMap<String, Session>> sessions = new HashMap<>();

    public void addSessionToGame(int gameID, String username, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, new HashMap<>());
        }
        sessions.get(gameID).put(username, session);
    }

    public void removeSessionFromGame(int gameID, String username) {
        sessions.get(gameID).remove(username);
    }

    public HashMap<String, Session> getGameMembers(int gameID) {
        return sessions.get(gameID);
    }

    public void gameWideMessage(int gameID, String message) throws IOException {
        for (Session s : sessions.get(gameID).values()) {
            s.getRemote().sendString(message);
        }
    }

    public void gameWideMessageExclude(int gameID, String message, Session session) throws IOException {
        for (Session s : sessions.get(gameID).values()) {
            if (s != session) s.getRemote().sendString(message);
        }
    }

}
