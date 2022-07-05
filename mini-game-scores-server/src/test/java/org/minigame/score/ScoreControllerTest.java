package org.minigame.score;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.Actions;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;
import org.minigame.session.Session;
import org.minigame.session.SessionService;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoreControllerTest {

    @Mock
    ScoreService scoreService;

    @Mock
    SessionService sessionService;

    @Spy
    HttpExchange exchange;

    @Spy
    HttpHelper httpHelper;

    @Captor
    ArgumentCaptor<Integer> httpStatusCodeCaptor;

    @InjectMocks
    ScoreController scoreController;

    @Test
    public void givenGoodLevelIdAndScore_whenExecuteRegisterScore_shouldReturnOK() throws URISyntaxException, IOException {

        //given
        InputStream requestBody = new ByteArrayInputStream("1000".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        Session session = new Session (1234, Clock.systemUTC().millis());

        when(exchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey=UICSNDK"));
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(sessionService.isValid("UICSNDK")).thenReturn(true);
        when(sessionService.get("UICSNDK")).thenReturn(session);
        doNothing().when(scoreService).save(any(Score.class));

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        verify(scoreService, times(1)).save(any(Score.class));

        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.OK.getStatusCode(), httpStatusCode);
        assertEquals("", responseBody.toString());
    }

    @Test
    public void givenBadLevelId_whenExecuteRegisterScore_shouldReturnBadRequest() throws URISyntaxException, IOException {

        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("//score?sessionkey=UICSNDK"));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenMissingScore_whenExecuteRegisterScore_shouldReturnBadRequest() throws URISyntaxException, IOException {

        //given
        InputStream requestBody = new ByteArrayInputStream("".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey=UICSNDK"));
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenBadScore_whenExecuteRegisterScore_shouldReturnBadRequest() throws URISyntaxException, IOException {

        //given
        InputStream requestBody = new ByteArrayInputStream("ABCD".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey=UICSNDK"));
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenBadQueryParam_whenExecuteRegisterScore_shouldReturnBadRequest() throws URISyntaxException, IOException {

        //given
        InputStream requestBody = new ByteArrayInputStream("1000".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey="));
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenExpiredSession_whenExecuteRegisterScore_shouldReturnUnauthorised() throws URISyntaxException, IOException {

        //given
        InputStream requestBody = new ByteArrayInputStream("1000".getBytes());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey=UICSNDK"));
        when(exchange.getRequestBody()).thenReturn(requestBody);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(sessionService.isValid("UICSNDK")).thenReturn(false);

        //when
        scoreController.execute(Actions.POST_SCORE, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.UNAUTHORIZED.getStatusCode(), httpStatusCode);
    }

    @Test
    public void givenGoodLevelId_whenExecuteGetHighScoreList_shouldReturnOKAndScoreList() throws URISyntaxException, IOException {
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("/2/highscorelist"));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(scoreService.getHighestScores(anyInt())).thenReturn(anyString());

        //when
        scoreController.execute(Actions.GET_HIGH_SCORE_LIST, exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.OK.getStatusCode(), httpStatusCode);
    }

}