package handlers;

import com.google.gson.Gson;
import services.ClearApplicationService;
import responses.ResponseMessage;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler{

    @Override
    public Object handleRequest(Request req, Response res) {
        ClearApplicationService service = new ClearApplicationService();
        ResponseMessage response = service.clearApplication();

        res.status(200);
        return new Gson().toJson(response);
    }
}
