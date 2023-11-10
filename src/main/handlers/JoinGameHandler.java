package handlers;

import com.google.gson.Gson;
import services.JoinGameService;
import requests.JoinGameRequest;
import responses.ResponseMessage;
import spark.Request;
import spark.Response;

public class JoinGameHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        String authToken = req.headers("authorization");
        JoinGameRequest request = getBody(req, JoinGameRequest.class);
        JoinGameService service = new JoinGameService();

        ResponseMessage result = service.joinGame(request, authToken);
        res.status(result.getReturnCode());
        return new Gson().toJson(result);
    }
}
