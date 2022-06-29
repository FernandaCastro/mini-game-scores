package org.minigame.user;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.Action;
import org.minigame.Controller;
import org.minigame.HttpHelper;
import org.minigame.HttpStatus;

import java.io.IOException;

public class UserController implements Controller {

    private final HttpHelper httpHelper;

    public UserController(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    //Contexts
    //Request: GET /<userid>/login
    //Response: <sessionkey>
    //<userid> : 31 bit unsigned integer number
    //<sessionkey> : A string representing a session (valid for 10 minutes).
    //Example: http://localhost:8081/4711/login --> UICSNDK
    public void login(int userId, HttpExchange exchange) throws IOException {

        httpHelper.sendResponse(HttpStatus.OK, "Execute GET Login action. UserId: " + userId, exchange);
    }

    @Override
    public void execute(String action, HttpExchange exchange) throws IOException {
        String pathVar = httpHelper.getPathVariable(exchange);
        if (pathVar==null) {
            httpHelper.sendResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getMessage(), exchange);
            return;
        }
        int userId = Integer.parseInt(pathVar);

        switch (action){
            case Action.GET_LOGIN -> login(userId, exchange);
        }
    }
}
