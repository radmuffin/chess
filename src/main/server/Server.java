package server;
import handlers.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.*;

@WebSocket
public class Server {

    public static void main(String[] args) {
        new Server().run();
    }

    private void run() {

        Spark.port(8080);

        Spark.externalStaticFileLocation("web");

        Spark.delete("/db", (req, res) -> new ClearHandler().handleRequest(req, res));
        Spark.post("/user", (req, res) -> new RegisterHandler().handleRequest(req, res));
        Spark.post("/session", (req, res) -> new LoginHandler().handleRequest(req, res));
        Spark.delete("/session", (req, res) -> new LogoutHandler().handleRequest(req, res));
        Spark.post("/game", (req, res) -> new CreateGameHandler().handleRequest(req, res));
        Spark.get("/game", (req, res) -> new ListGamesHandler().handleRequest(req, res));
        Spark.put("/game", (req, res) -> new JoinGameHandler().handleRequest(req, res));
        Spark.webSocket("/connect", Server.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // TODO: 11/29/2023
    }

}

