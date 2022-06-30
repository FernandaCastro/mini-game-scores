package org.minigame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.session.Session;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    SessionRepository sessionRepository;

    @Spy
    Clock clock;

    @InjectMocks
    SessionService sessionService;

    @Test
    public void givenUserIdWithNoExistingSession_whenRegisterSession_shouldCreateNewSessionTest(){
        //given
        when(sessionRepository.get(4711)).thenReturn(null);
        when(clock.instant()).thenReturn(Instant.now());

        //when
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertNotNull(returnedSession.getSessionKey());
    }

    @Test
    public void givenUserIdWithValidExistingSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        var existingSession = new Session(4711, Clock.systemUTC().millis());
        when(sessionRepository.get(4711)).thenReturn(existingSession);
        when(clock.instant()).thenReturn(Instant.now());

        //when
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertEquals(existingSession.getSessionKey(), returnedSession.getSessionKey());
    }

    @Test
    public void givenUserIdInvalidExistingSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        long createdAt = Instant.now().minus(15, ChronoUnit.MINUTES).toEpochMilli();
        var existingSession = new Session(4711, createdAt);

        when(sessionRepository.get(4711)).thenReturn(existingSession);
        when(clock.instant()).thenReturn(Instant.now());
        doNothing().when(sessionRepository).save(any(Session.class));
        doNothing().when(sessionRepository).save(anyInt(), any(Session.class));

        //when
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertNotEquals(existingSession.getSessionKey(), returnedSession.getSessionKey());
    }
}
