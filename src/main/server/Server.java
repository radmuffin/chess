package server;
import com.google.gson.Gson;
import handlers.ClearHandler;
import handlers.RegisterHandler;
import services.ClearApplicationService;
import services.responses.ResponseMessage;
import spark.*;

import java.util.ArrayList;
import java.util.Map;


public class Server {

    public static void main(String[] args) {
        new Server().run();
    }

    private void run() {

        Spark.port(8080);

        Spark.externalStaticFileLocation("web");

        Spark.delete("/db", (req, res) -> new ClearHandler().handleRequest(req, res));
        Spark.post("/user", (req, res) -> new RegisterHandler().handleRequest(req, res));
    }


}

