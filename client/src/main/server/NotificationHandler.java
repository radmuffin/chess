package server;

import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {

    void receiveServerMessage(String message);
}
