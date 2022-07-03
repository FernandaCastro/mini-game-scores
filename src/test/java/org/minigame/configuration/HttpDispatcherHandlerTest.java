package org.minigame.configuration;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.session.SessionController;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HttpDispatcherHandlerTest {

    @Mock
    RootContext rootContext;

    @Mock
    SessionController sessionController;

    @Spy
    HttpExchange exchange;

    @Spy
    HttpHelper httpHelper;

    @Captor
    ArgumentCaptor<Integer> httpStatusCodeCaptor;

    @InjectMocks
    HttpDispatcherHandler httpDispatcherHandler;

    @Test
    public void givenGETLogin_whenHandle_shouldRouteToUserController() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/login"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(rootContext.get(SessionController.class)).thenReturn(sessionController);

        httpDispatcherHandler.handle(exchange);

        verify(sessionController, times(1)).execute(Actions.GET_LOGIN, exchange);
    }

    @Test
    public void givenNoUserId_whenHandle_shouldReturnBadRequest() throws IOException, URISyntaxException {
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081//login"));
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
    public void givenBadAction_whenHandle_shouldReturnBadRequest() throws IOException, URISyntaxException {
        //given
        Headers headers = new Headers();
        OutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/logout"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        //when
        httpDispatcherHandler.handle(exchange);

        //then
        Mockito.verify(exchange).sendResponseHeaders(httpStatusCodeCaptor.capture(), anyLong());
        int httpStatusCode = httpStatusCodeCaptor.getValue();

        assertEquals(HttpStatus.BAD_REQUEST.getStatusCode(), httpStatusCode);
    }

    //TODO: Write negative tests
}
