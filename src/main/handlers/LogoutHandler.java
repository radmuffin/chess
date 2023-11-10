package handlers;

import com.google.gson.Gson;
import services.LogoutService;
import responses.ResponseMessage;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        String authToken = req.headers("authorization");
        LogoutService service = new LogoutService();

        ResponseMessage result = service.logout(authToken);
        res.status(result.getReturnCode());

        return new Gson().toJson(result);
    }
}
