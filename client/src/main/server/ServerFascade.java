package server;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerFascade {

    private static final String url = "http://localhost:8080";
    public <T> T sendAndReceive(String path, String method, String body, String auth, Class<T> responseClass) throws URISyntaxException, IOException {

        URI uri = new URI(url + path);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.addRequestProperty("authorization", auth);
        writeRequestBody(body, http);
        http.connect();

        return readResponseBody(http, responseClass);
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private <T> T readResponseBody(HttpURLConnection http, Class<T> clazz) throws IOException {
        T responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, clazz);
        }
        return responseBody;
    }

}
