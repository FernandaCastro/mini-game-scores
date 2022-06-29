package org.minigame;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.minigame.level.LevelController;
import org.minigame.user.UserController;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.regex.Matcher;

//Contexts
//Request: GET /<userid>/login
//Response: <sessionkey>
//<userid> : 31 bit unsigned integer number
//<sessionkey> : A string representing a session (valid for 10 minutes).
//Example: http://localhost:8081/4711/login --> UICSNDK

//Request: POST /<levelid>/score?sessionkey=<sessionkey>
//Request body: <score>
//Response: (nothing)
//<levelid> : 31 bit unsigned integer number
//<sessionkey> : A session key string retrieved from the login function.
//<score> : 31 bit unsigned integer number
//Example: POST http://localhost:8081/2/score?sessionkey=UICSNDK (with the post
//body: 1500)

//Request: GET /<levelid>/highscorelist
//Response: CSV of <userid>=<score>
//<levelid> : 31 bit unsigned integer number
//<score> : 31 bit unsigned integer number
//<userid> : 31 bit unsigned integer number
//Example: http://localhost:8081/2/highscorelist -> 4711=1500,131=1220


public class HttpDispatcherHandler implements HttpHandler {

    private final RootContext rootContext;
    private final HttpHelper httpHelper;

    public HttpDispatcherHandler(RootContext rootContext, HttpHelper httpHelper) {
        this.rootContext = rootContext;
        this.httpHelper = httpHelper;
    }

    public static final Set<String> context = Set.of(
            Action.GET_LOGIN,
            Action.POST_SCORE,
            Action.GET_HIGH_SCORE_LIST
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            URI uri = exchange.getRequestURI();
            Matcher matcher = HttpHelper.PATTERN_URI.matcher(uri.getPath());
            if (!matcher.matches()) {
                httpHelper.sendResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getMessage(), exchange);
                return;
            }

            String actionKey = exchange.getRequestMethod() + "/" + matcher.group(2);
            if(!context.contains(actionKey)){
                httpHelper.sendResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getMessage(), exchange);
                return;
            }

            Controller controller = getController(actionKey, exchange);
            if(controller!=null) {
                controller.execute(actionKey, exchange);
            }

        }catch(Exception e){
            e.printStackTrace();
            String error = HttpStatus.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage();
            httpHelper.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, error.toString(), exchange);
        }
    }


    private Controller getController(String action, HttpExchange exchange) throws IOException{

        switch (action) {
            case Action.GET_LOGIN:
                return (UserController) rootContext.getBean(UserController.class);
            case Action.POST_SCORE:
            case Action.GET_HIGH_SCORE_LIST:
                return (LevelController) rootContext.getBean(LevelController.class);
            default:
                httpHelper.sendResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getMessage(), exchange);
                break;
        }
        return null;
    }
}
