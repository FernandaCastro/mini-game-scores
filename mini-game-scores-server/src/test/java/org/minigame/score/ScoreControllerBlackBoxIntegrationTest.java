package org.minigame.score;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.minigame.ApplicationTestingUtils;
import org.minigame.configuration.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreControllerBlackBoxIntegrationTest {

    @BeforeAll
    static void init(){
        ApplicationTestingUtils.startupHttpServer(8081, 1);
    }

    @AfterAll
    static void close(){
        ApplicationTestingUtils.stopHttpServer();
    }

    @Test
    public void givenGoodLevelIdAndScore_whenExecuteRegisterScore_shouldReturnOK() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/4711/login")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode());

        String sessionKey = response.body();

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/1/score?sessionkey="+sessionKey))
                .POST(HttpRequest.BodyPublishers.ofString("1000"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode());
    }

    @Test
    public void givenNoScore_whenExecuteRegisterScore_shouldReturnOK() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/4711/login")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode());

        String sessionKey = response.body();

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/1/score?sessionkey="+sessionKey))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), response.statusCode());
    }
}
