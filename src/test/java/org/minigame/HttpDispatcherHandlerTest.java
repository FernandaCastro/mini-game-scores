package org.minigame;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.user.UserController;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    UserController userController;

    @Mock
    HttpExchange exchange;

    @InjectMocks
    HttpDispatcherHandler httpDispatcherHandler;

    @Test
    public void givenGETLogin_whenHandle_shouldRouteToUserController() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8081/4711/login"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(rootContext.getBean(UserController.class)).thenReturn(userController);

        httpDispatcherHandler.handle(exchange);

        verify(userController, times(1)).execute("GET/login", exchange);

    }
}
