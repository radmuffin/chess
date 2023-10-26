package handlers;

import com.google.gson.Gson;
import services.ListGameService;
import services.responses.ListGamesResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        String authToken = req.headers("authorization");
        ListGameService service = new ListGameService();

        ListGamesResult result = service.listGames(authToken);
        res.status(result.getReturnCode());

        return new Gson().toJson(result);
    }
}
