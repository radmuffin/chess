package server;

import adapters.GameAdapter;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;
    private static final String url = "http://localhost:8080";

    public ServerFacade() {
        session = null;
        notificationHandler = null;
    }

    public ServerFacade(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }
    public void setNotificationHandler(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public <T> T sendHTTP(String path, String method, String body, String auth, Class<T> responseClass) throws URISyntaxException, IOException {

        URI uri = new URI(url + path);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.addRequestProperty("authorization", auth);
        writeRequestBody(body, http);
        http.connect();

        return readResponseBody(http, responseClass);
    }

    public void connectWS() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI(url.replace("http", "ws") + "/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                notificationHandler.receiveServerMessage(s);
            }
        });
    }

    public void sendWSCmd(UserGameCommand cmd) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
    }

    public void disconnectWS() throws IOException {
        this.session.close();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private <T> T readResponseBody(HttpURLConnection http, Class<T> clazz) throws IOException {
        T responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);

            var builder = new GsonBuilder();
            builder.registerTypeAdapter(ChessGame.class, new GameAdapter());

            responseBody =  builder.create().fromJson(inputStreamReader, clazz);
        }
        return responseBody;
    }

}
