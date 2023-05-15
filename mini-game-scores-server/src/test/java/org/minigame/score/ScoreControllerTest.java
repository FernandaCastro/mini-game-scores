package org.minigame.score;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.configuration.HttpStatus;
import org.minigame.configuration.MiniGameException;
import org.minigame.session.Session;
import org.minigame.session.SessionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoreControllerTest {

    @Mock
    ScoreService scoreService;

    @Mock
    SessionService sessionService;

    @InjectMocks
    ScoreController scoreController;

    @Test
    public void givenGoodLevelIdAndScore_whenExecuteRegisterScore_shouldReturnOK() {

        //given
        Session session = new Session (1234, Clock.systemUTC().millis());
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");

        //when
        when(sessionService.isValid("UICSNDK")).thenReturn(true);
        when(sessionService.get("UICSNDK")).thenReturn(session);
        doNothing().when(scoreService).save(any(Score.class));
        var response = scoreController.registerScore("1000", "2", queryParam);

        //then
        verify(scoreService, times(1)).save(any(Score.class));

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("", response.getMessage());
    }

    @Test
    public void givenBadLevelId_whenExecuteRegisterScore_shouldReturnBadRequest(){

        //given
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> scoreController.registerScore("1000", null, queryParam));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenMissingScore_whenExecuteRegisterScore_shouldReturnBadRequest() {

        //given
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> scoreController.registerScore("", "2", queryParam));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenBadScore_whenExecuteRegisterScore_shouldReturnBadRequest() {

        //given
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> scoreController.registerScore("ABCD", "2", queryParam));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenBadQueryParam_whenExecuteRegisterScore_shouldReturnBadRequest() {

        //given
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "");

        //when
        var thrown = Assertions.assertThrows(MiniGameException.class,
                () -> scoreController.registerScore("1000", "2", queryParam));

        //then
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    public void givenExpiredSession_whenExecuteRegisterScore_shouldReturnUnauthorised() {

        //given
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("sessionkey", "UICSNDK");

        when(sessionService.isValid("UICSNDK")).thenReturn(false);

        //when
        var response = scoreController.registerScore("1000", "2", queryParam);

        //then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
    }

    @Test
    public void givenGoodLevelId_whenExecuteGetHighScoreList_shouldReturnOKAndScoreList() {
        //given
        when(scoreService.getHighestScores(anyInt())).thenReturn(anyString());

        //when
        var response = scoreController.getHighScoreList( "2", null);

        //then
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

}
