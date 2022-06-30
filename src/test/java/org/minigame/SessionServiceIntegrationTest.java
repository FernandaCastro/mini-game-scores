package org.minigame;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minigame.session.Session;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionServiceIntegrationTest {

    SessionRepository sessionRepository;

    SessionService sessionService;

    @BeforeAll
    public void init(){
        sessionRepository = new SessionRepository();
        sessionService = new SessionService(sessionRepository, Clock.systemUTC());
    }

    @Test
    public void givenNotExistingSession_whenRegisterSession_shouldCreateNewSessionTest(){
        //given
        int userId = 4711;

        //when
        var returnedSession = sessionService.registerSession(userId);

        //then
        assertNotNull(returnedSession);
        assertEquals(userId, returnedSession.getUserId());
        assertNotNull(returnedSession.getSessionKey());
    }

    @Test
    public void givenValidExistingSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        int userId = 1234;
        var validSession = new Session(userId, Clock.systemUTC().millis());
        sessionRepository.save(validSession);
        sessionRepository.save(validSession.getUserId(), validSession);

        //when
        var returnedSession = sessionService.registerSession(userId);

        //then
        assertNotNull(returnedSession);
        assertEquals(userId, returnedSession.getUserId());
        assertEquals(validSession.getSessionKey(), returnedSession.getSessionKey());
    }

    @Test
    public void givenUserIdInvalidExistingSession_whenRegisterSession_shouldReturnExistingSessionTest(){
        //given
        int userId = 5678;
        long expiredTime = Instant.now().minus(20, ChronoUnit.MINUTES).toEpochMilli();
        var invalidSession = new Session(userId, expiredTime);
        sessionRepository.save(invalidSession);
        sessionRepository.save(invalidSession.getUserId(), invalidSession);

        //when
        var returnedSession = sessionService.registerSession(userId);

        //then
        assertNotNull(returnedSession);
        assertEquals(userId, returnedSession.getUserId());
        assertNotEquals(invalidSession.getSessionKey(), returnedSession.getSessionKey());
    }


}
