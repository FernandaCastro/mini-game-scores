package org.minigame.session;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {

    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public void save(Session session){
        sessions.put(session.getSessionKey(), session);
    }

    public Session get(String sessionKey){
        return sessions.get(sessionKey);
    }

    public Session getValidSession(int userId, Clock clock, long expiration){
        for (Session session : sessions.values()) {
            if (session.getUserId() == userId && session.isValid(clock, expiration))
                return session;
        }
        return null;
    }

    public int purge(Clock clock, long expiration){
        int size = sessions.size();
        sessions.values().removeIf(s -> !s.isValid(clock, expiration));
        return size - sessions.size();
    }

}