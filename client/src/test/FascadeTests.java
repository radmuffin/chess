import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import responses.ResponseMessage;
import server.ServerFascade;

public class FascadeTests {

    ServerFascade fascade = new ServerFascade();
    @Test
    public void goodExecution() {
        Assertions.assertDoesNotThrow(() -> fascade.sendAndReceive("/db", "DELETE", "", null, ResponseMessage.class));
    }

    @Test
    public void badExecution() {
        LoginRequest req = new LoginRequest("totatallyausername", "universalKey");
        Assertions.assertThrows(Exception.class, () -> fascade.sendAndReceive("/session", "POST", new Gson().toJson(req), null, LoginRequest.class));
    }
}
