package org.minigame.session;

import org.minigame.configuration.Repository;

import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository implements Repository {

    //TODO: check it default is enough. Default initial capacity (16), load factor (0.75) and concurrencyLevel (16).
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Session> userSessions = new ConcurrentHashMap<>();

    public void save(Session session){
        sessions.put(session.getSessionKey(), session);
    }

    public Session get(String sessionKey){
        return sessions.get(sessionKey);
    }

    public void save(int userId, Session session){
        userSessions.put(userId, session);
    }

    public Session get(int userId){
        return userSessions.get(userId);
    }

}