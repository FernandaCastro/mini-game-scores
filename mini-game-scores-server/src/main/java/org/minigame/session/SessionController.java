package org.minigame.session;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.configuration.Controller;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;
import org.minigame.configuration.MiniGameException;

import java.util.Map;

public class SessionController implements Controller {

    private final HttpHelper httpHelper;
    private final SessionService sessionService;

    public SessionController(HttpHelper httpHelper, SessionService sessionService) {
        this.httpHelper = httpHelper;
        this.sessionService = sessionService;
    }

    protected void login(int userId, HttpExchange exchange) {

        try {

            Session session = sessionService.registerSession(userId);
            httpHelper.sendResponse(HttpStatus.OK, session.getSessionKey(), exchange);

        }catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    @Override
    public void execute(String action, HttpExchange exchange, String body, String pathVar, Map<String, String> queryParam) {

        try{
            int userId = getUserId(exchange, pathVar);
            login(userId, exchange);

        }catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    private int getUserId(HttpExchange exchange, String pathVar){
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
