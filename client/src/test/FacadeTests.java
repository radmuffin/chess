import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import responses.ResponseMessage;
import server.ServerFacade;

public class FacadeTests {

    final ServerFacade fascade = new ServerFacade();

    @Test
    public void goodExecution() {
        Assertions.assertDoesNotThrow(() -> fascade.sendHTTP("/db", "DELETE", "", null, ResponseMessage.class));
    }

    @Test
    public void badExecution() {
        LoginRequest req = new LoginRequest("totatallyausername", "universalKey");
        Assertions.assertThrows(Exception.class, () -> fascade.sendHTTP("/session", "POST", new Gson().toJson(req), null, LoginRequest.class));
    }
}
