package org.minigame.session;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.Actions;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    SessionService sessionService;

    @Spy
    HttpExchange exchange;

    @Spy
    HttpHelper httpHelper;

    @InjectMocks
    SessionController sessionController;

    @Test
    public void givenBadURI_whenExecute_shouldReturnBadRequest() throws IOException, URISyntaxException {
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081//login"));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        sessionController.execute(Actions.GET_LOGIN, exchange);

        //then
        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), responseBody.toString());
        responseBody.close();
    }

    @Test
    public void givenGoodURI_whenExecute_shouldRegisterSession() throws URISyntaxException, IOException {
        //given
        Session session = new Session(4711, Clock.systemUTC().millis());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/login"));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(sessionService.registerSession(anyInt())).thenReturn(session);

        //when
        sessionController.execute(Actions.GET_LOGIN, exchange);

        //then
        verify(sessionService, times(1)).registerSession(4711);
        assertEquals(session.getSessionKey(), responseBody.toString());
    }
}
