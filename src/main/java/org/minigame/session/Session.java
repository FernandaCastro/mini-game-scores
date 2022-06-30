package org.minigame.session;

import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Duration;
import java.util.Random;

public class Session {

    private final long EXPIRATION_TIME = Duration.ofMinutes(10).toMillis();

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

    public boolean isValid(Clock clock){
        return (clock.millis() - createdAt) < EXPIRATION_TIME;
    }

    private String generateSessionKey() {

        return new Random().ints(7, 65, 90)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
