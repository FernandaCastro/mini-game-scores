package org.minigame.configuration;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.score.ScoreController;
import org.minigame.session.SessionController;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HttpDispatcherHandlerTest {

    @Mock
    RootContext rootContext;

    @Mock
    SessionController sessionController;

    @Mock
    ScoreController scoreController;

    @Spy
    HttpExchange exchange;

    @Spy
    HttpHelper httpHelper;

    @Captor
    ArgumentCaptor<Integer> httpStatusCodeCaptor;

    @InjectMocks
    HttpDispatcherHandler httpDispatcherHandler;

    @Test
    public void givenLogin_whenHandle_shouldRouteSuccessfully() throws URISyntaxException {
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        var response = new MiniGameResponse(HttpStatus.OK, "");

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/login"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(SessionController.class)).thenReturn(sessionController);
        when(sessionController.login("4711", null)).thenReturn(response);

        httpDispatcherHandler.handle(exchange);

        verify(sessionController, times(1)).login("4711", null);
    }

    @Test
    public void givenLoginWithNoUserId_whenHandle_shouldRouteSuccessfully() throws URISyntaxException {
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        var response = new MiniGameResponse(HttpStatus.BAD_REQUEST, "");

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081//login"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(SessionController.class)).thenReturn(sessionController);
        when(sessionController.login(null, null)).thenReturn(response);

        //when
        httpDispatcherHandler.handle(exchange);

        //then
        verify(sessionController, times(1)).login(null, null);
    }

    @Test
    public void givenBadAction_whenHandle_shouldReturnBadRequest() throws IOException, URISyntaxException {
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/logout"));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        httpDispatcherHandler.handle(exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenPOSTScore_whenHandle_shouldRouteSuccessfully() throws URISyntaxException {
        InputStream requestBody = new ByteArrayInputStream("1000".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");
        var response = new MiniGameResponse(HttpStatus.OK, "");

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/1/score?sessionkey=UICSNDK"));
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(ScoreController.class)).thenReturn(scoreController);
        when(scoreController.registerScore("1000", "1", queryParam)).thenReturn(response);

        httpDispatcherHandler.handle(exchange);

        verify(scoreController, times(1)).registerScore("1000", "1", queryParam);
    }

    @Test
    public void givenGETHighscorelist_whenHandle_shouldRouteSuccessfully() throws URISyntaxException {
        InputStream requestBody = new ByteArrayInputStream("".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        var response = new MiniGameResponse(HttpStatus.OK, "");
        when(scoreController.getHighScoreList("1", null)).thenReturn(response);

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/1/highscorelist"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(ScoreController.class)).thenReturn(scoreController);

        httpDispatcherHandler.handle(exchange);

        verify(scoreController, times(1)).getHighScoreList("1", null);
    }

    @Test
    public void givenPOSTScoreAndBadLevelId_whenHandle_shouldReturnBadRequest() throws URISyntaxException {
        InputStream requestBody = new ByteArrayInputStream("".getBytes());
        Headers headers = new Headers();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");
        OutputStream responseBody = new ByteArrayOutputStream();
        var response = new MiniGameResponse(HttpStatus.BAD_REQUEST, "");

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081//score?sessionkey=UICSNDK"));
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(ScoreController.class)).thenReturn(scoreController);
        when(scoreController.registerScore("", null, queryParam)).thenReturn(response);

        httpDispatcherHandler.handle(exchange);

        //then
        verify(scoreController, times(1)).registerScore("", null, queryParam);
    }

    @Test
    public void givenPOSTScoreAndBadQueryParam_whenHandle_shouldReturnBadRequest() throws URISyntaxException {
        InputStream requestBody = new ByteArrayInputStream("1000".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        var response = new MiniGameResponse(HttpStatus.BAD_REQUEST, "");

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/1/score?sessionkey="));
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(rootContext.get(ScoreController.class)).thenReturn(scoreController);
        when(scoreController.registerScore("1000", "1", null)).thenReturn(response);

        httpDispatcherHandler.handle(exchange);

        //then
        verify(scoreController, times(1)).registerScore("1000", "1", null);
    }

}
