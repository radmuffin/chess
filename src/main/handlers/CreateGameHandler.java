package handlers;

import com.google.gson.Gson;
import services.CreateGameService;
import services.requests.CreateGameRequest;
import services.responses.CreateGameResult;
import services.responses.ResponseMessage;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        String authToken = req.headers("authorization");
        CreateGameRequest request = getBody(req, CreateGameRequest.class);
        CreateGameService service = new CreateGameService();

        CreateGameResult result = service.createGame(request, authToken);
        res.status(result.getReturnCode());

        if (res.status() == 401) {                          //ugly way to avoid the non null gameID
            ResponseMessage fail = new ResponseMessage();
            fail.setReturnCode(401);
            fail.setMessage("Error: unauthorized");
            return new Gson().toJson(fail);
        }
        return new Gson().toJson(result);
    }
}
