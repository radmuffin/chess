package handlers;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public abstract class Handler {
    public abstract Object handleRequest(Request req, Response res);

    protected static <T> T getBody(Request req, Class<T> clazz) {
        return new Gson().fromJson(req.body(), clazz);
    }

}
