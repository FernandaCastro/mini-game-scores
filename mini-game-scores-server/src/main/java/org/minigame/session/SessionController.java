package org.minigame.session;

import org.minigame.configuration.*;

import java.util.Map;

public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public MiniGameResponse login(String pathVar, Map<String, String> queryParam) {

        int userId = getUserId(pathVar);
        Session session = sessionService.registerSession(userId);
        return new MiniGameResponse(HttpStatus.OK, session.getSessionKey());

    }

    private int getUserId(String pathVar){
        if (pathVar==null) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<UserId> is missing");
        }

        try {
            return Integer.parseInt(pathVar);
        }catch (NumberFormatException e) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<UserId> is invalid");
        }
    }
}
