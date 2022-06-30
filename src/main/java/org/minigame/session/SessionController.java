package org.minigame.session;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.configuration.Action;
import org.minigame.configuration.Controller;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;

import java.io.IOException;

public class SessionController implements Controller {

    private final HttpHelper httpHelper;
    private final SessionService sessionService;

    public SessionController(HttpHelper httpHelper, SessionService sessionService) {
        this.httpHelper = httpHelper;
        this.sessionService = sessionService;
    }

    //Request: GET /<userid>/login
    //Response: <sessionkey>
    //<userid> : 31 bit unsigned integer number
    //<sessionkey> : A string representing a session (valid for 10 minutes).
    //Example: http://localhost:8081/4711/login --> UICSNDK
    protected void login(int userId, HttpExchange exchange) throws IOException {
        try {
            Session session = sessionService.registerSession(userId);
            httpHelper.sendResponse(HttpStatus.OK, session.getSessionKey(), exchange);

        //TODO: Treat Exception
        }catch(Exception e){
            httpHelper.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), exchange);
        }
    }

    @Override
    public void execute(String action, HttpExchange exchange) throws IOException {
        String pathVar = httpHelper.getPathVariable(exchange);
        if (pathVar==null) {
            httpHelper.sendResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getMessage(), exchange);
            return;
        }
        int userId = Integer.parseInt(pathVar);

        login(userId, exchange);
    }
}
