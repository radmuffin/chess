package handlers;

import com.google.gson.Gson;
import services.LoginService;
import services.requests.LoginRequest;
import services.responses.LoginResult;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler{
    @Override
    public Object handleRequest(Request req, Response res) {
        LoginRequest request = getBody(req, LoginRequest.class);

        LoginService service = new LoginService();
        LoginResult result = service.login(request);

        res.status(result.getReturnCode());
        return new Gson().toJson(result);
    }
}
