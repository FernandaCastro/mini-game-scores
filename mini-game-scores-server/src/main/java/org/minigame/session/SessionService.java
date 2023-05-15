package org.minigame.session;

import java.time.Clock;
import java.time.Duration;

public class SessionService {

    private final long EXPIRATION_TIME = Duration.ofMinutes(10).toMillis();

    private final SessionRepository sessionRepository;
    private final Clock clock;

    public SessionService(SessionRepository sessionRepository, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    public Session registerSession(int userId){

        var existingSession = sessionRepository.getValidSession(userId, clock, EXPIRATION_TIME);
        if (existingSession != null){
            return existingSession;
        }

        Session session = new Session(userId, clock.millis());
//        while (sessionRepository.get(session.getSessionKey()) != null) {
//            session = new Session(userId, clock.millis());
//        }
        sessionRepository.save(session);
        return session;
    }

    public boolean isValid(String sessionKey){
        Session session = sessionRepository.get(sessionKey);
        return session != null && session.isValid(clock, EXPIRATION_TIME);
    }

    public Session get(String sessionKey){
        return sessionRepository.get(sessionKey);
    }

    public int purge(){
        return sessionRepository.purge(clock, EXPIRATION_TIME);
    }
}
