package org.minigame;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.HttpDispatcherHandler;
import org.minigame.configuration.RootContext;
import org.minigame.session.SessionController;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpDispatcherHandlerTest {

    @Mock
    RootContext rootContext;

    @Mock
    SessionController sessionController;

    @Mock
    HttpExchange exchange;

    @InjectMocks
    HttpDispatcherHandler httpDispatcherHandler;

    @Test
    public void givenGETLogin_whenHandle_shouldRouteToUserController() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/login"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(rootContext.getBean(SessionController.class)).thenReturn(sessionController);

        httpDispatcherHandler.handle(exchange);

        verify(sessionController, times(1)).execute("GET/login", exchange);
    }

    //TODO: Write negative tests
}
