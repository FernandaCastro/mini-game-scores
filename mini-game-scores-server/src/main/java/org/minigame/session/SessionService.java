package org.minigame.session;

import org.minigame.configuration.Service;

import java.time.Clock;
import java.time.Duration;

public class SessionService implements Service {

    private final long EXPIRATION_TIME = Duration.ofMinutes(10).toMillis();

    private final SessionRepository sessionRepository;
    private final Clock clock;

    public SessionService(SessionRepository sessionRepository, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    public Session registerSession(int userId){
        var existingSession = sessionRepository.get(userId);
        if (existingSession != null && existingSession.isValid(clock, EXPIRATION_TIME)){
            return existingSession;
        }

         var session = new Session(userId, clock.millis());
         sessionRepository.save(session);
         sessionRepository.save(userId, session);
         return session;
    }

    public boolean isValid(String sessionKey){
        Session session = sessionRepository.get(sessionKey);
        return session != null && session.isValid(clock, EXPIRATION_TIME);
    }

    public Session get(String sessionKey){
        return sessionRepository.get(sessionKey);
    }

    //TODO: Implement a Purge to clean expired sessions
}
