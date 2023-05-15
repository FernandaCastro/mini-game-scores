package org.minigame.session;

import java.time.Clock;
import java.util.Objects;
import java.util.Random;

public class Session {

    private final int userId;

    private final String sessionKey;

    private final long createdAt;

    public Session(int userId, long createdAt) {
        this.userId = userId;
        this.sessionKey = generateSessionKey();
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isValid(Clock clock, long expirationTime){
        return (clock.millis() - createdAt) < expirationTime;
    }

    private String generateSessionKey() {

        return new Random().ints(7, 65, 90)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session session = (Session) o;
        return getUserId() == session.getUserId() && getSessionKey().equals(session.getSessionKey());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getSessionKey());
    }
}
