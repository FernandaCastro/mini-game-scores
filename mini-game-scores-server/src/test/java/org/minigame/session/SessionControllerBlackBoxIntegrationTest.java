package org.minigame.session;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionControllerBlackBoxIntegrationTest {

    @BeforeAll
    static void init(){
        ApplicationTestingUtils.startupHttpServer(8081, 1);
    }

    @AfterAll
    static void close(){
        ApplicationTestingUtils.stopHttpServer();
    }

    @Test
    public void givenGoodURI_whenLogin_returnSessionKeyOK() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/4711/login")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    public void givenGoodURI_whenLoginTwice_returnSameSessionKeyOK() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/4711/login")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String sessionKey = response.body();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.getStatusCode(), response.statusCode());
        assertNotNull(response.body());
        assertEquals(sessionKey, response.body());
    }

    @Test
    public void givenBadUserId_whenLogin_returnBadRequest() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/aa/login")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), response.statusCode());
    }

    @Test
    public void givenInvalidAction_whenLogin_returnBadRequest() throws Exception{

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/4711/logout")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), response.statusCode());
    }

}
