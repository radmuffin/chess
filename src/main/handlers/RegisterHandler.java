package handlers;

import com.google.gson.Gson;
import services.RegisterService;
import services.requests.RegisterRequest;
import services.responses.RegisterResult;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        RegisterRequest request = getBody(req, RegisterRequest.class);

        RegisterService service = new RegisterService();
        RegisterResult result = service.register(request);
        res.status(result.getReturnCode());

        return new Gson().toJson(result);
    }
}
