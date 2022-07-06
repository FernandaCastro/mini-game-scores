package org.minigame.session;

import org.minigame.configuration.*;

import java.util.Map;

public class SessionController implements Controller {

    private final HttpHelper httpHelper;
    private final SessionService sessionService;

    public SessionController(HttpHelper httpHelper, SessionService sessionService) {
        this.httpHelper = httpHelper;
        this.sessionService = sessionService;
    }

    protected MiniGameResponse login(int userId) {

        Session session = sessionService.registerSession(userId);
        return new MiniGameResponse(HttpStatus.OK, session.getSessionKey());

    }

    @Override
    public MiniGameResponse execute(String action, String body, String pathVar, Map<String, String> queryParam) {
        try {
            int userId = getUserId(pathVar);
            return login(userId);
        }catch(MiniGameException e){
            return new MiniGameResponse(e.getHttpStatus(), e.getMessage());
        }
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
