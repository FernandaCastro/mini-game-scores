package org.minigame.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.HttpStatus;
import org.minigame.configuration.MiniGameException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    SessionService sessionService;

    @InjectMocks
    SessionController sessionController;

    @Test
    public void givenNullUserId_whenExecuteLogin_shouldReturnBadRequest(){

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> sessionController.login(null, null));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenBadUserId_whenExecuteLogin_shouldReturnBadRequest(){

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> sessionController.login("ABCD", null));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenMissingUserId_whenExecuteLogin_shouldReturnBadRequest(){

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> sessionController.login("", null));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenGoodURI_whenLogin_shouldRegisterSession(){
        //given
        Session session = new Session(4711, Clock.systemUTC().millis());
        when(sessionService.registerSession(anyInt())).thenReturn(session);

        //when
        //var response = sessionController.execute(Actions.GET_LOGIN, "", "4711", null);
        var response = sessionController.login("4711", null);

        //then
        verify(sessionService, times(1)).registerSession(4711);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(session.getSessionKey(), response.getMessage());
    }
}
