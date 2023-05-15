package org.minigame.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

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
        when(sessionRepository.getValidSession(4711, clock, Duration.ofMinutes(10).toMillis())).thenReturn(null);
        when(clock.instant()).thenReturn(Instant.now());

        //when
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertNotNull(returnedSession.getSessionKey());
    }

    @Test
    public void givenUserIdWithValidExistingSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        var now = clock.instant().now();
        var existingSession = new Session(4711, now.toEpochMilli());

        //when
        when(sessionRepository.getValidSession(4711, clock, Duration.ofMinutes(10).toMillis())).thenReturn(existingSession);
        // when(clock.instant()).thenReturn(now);
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertEquals(existingSession.getSessionKey(), returnedSession.getSessionKey());
    }

    @Test
    public void givenExpiredSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        var now = clock.instant().now();
        long createdAt = now.minus(15, ChronoUnit.MINUTES).toEpochMilli();
        var existingSession = new Session(4711, createdAt);

        //when
        when(sessionRepository.getValidSession(4711, clock, Duration.ofMinutes(10).toMillis())).thenReturn(null);
        when(clock.instant()).thenReturn(now);
        doNothing().when(sessionRepository).save(any(Session.class));
        //doNothing().when(sessionRepository).save(anyInt(), any(Session.class));
        var returnedSession = sessionService.registerSession(4711);

        //then
        assertNotEquals(existingSession.getSessionKey(), returnedSession.getSessionKey());
    }
}
