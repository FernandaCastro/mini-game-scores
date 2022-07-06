package org.minigame.session;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.Actions;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    @Captor
    ArgumentCaptor<Integer> httpStatusCodeCaptor;


    @InjectMocks
    SessionController sessionController;

    @Test
    public void givenBadURI_whenExecuteLogin_shouldReturnBadRequest() throws IOException{
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        sessionController.execute(Actions.GET_LOGIN, exchange, "", null, null);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
        responseBody.close();
    }

    @Test
    public void givenGoodURI_whenExecuteLogin_shouldRegisterSession() throws IOException {
        //given
        Session session = new Session(4711, Clock.systemUTC().millis());
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);
        when(sessionService.registerSession(anyInt())).thenReturn(session);

        //when
        sessionController.execute(Actions.GET_LOGIN, exchange, "", "4711", null);

        //then
        verify(sessionService, times(1)).registerSession(4711);

        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.OK.getStatusCode(), httpStatusCode);
        assertEquals(session.getSessionKey(), responseBody.toString());
    }
}
