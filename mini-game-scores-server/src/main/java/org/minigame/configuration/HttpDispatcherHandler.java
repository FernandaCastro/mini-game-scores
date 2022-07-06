package org.minigame.configuration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.minigame.score.ScoreController;
import org.minigame.session.SessionController;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class HttpDispatcherHandler implements HttpHandler {

    private static Logger log = Logger.getLogger(HttpDispatcherHandler.class.getName());

    private final RootContext rootContext;
    private final HttpHelper httpHelper;

    public HttpDispatcherHandler(RootContext rootContext, HttpHelper httpHelper) {
        this.rootContext = rootContext;
        this.httpHelper = httpHelper;
    }

    public static final Set<String> httpContext = Set.of(
            Actions.GET_LOGIN,
            Actions.POST_SCORE,
            Actions.GET_HIGH_SCORE_LIST
    );

    @Override
    public void handle(HttpExchange exchange) {
        try {
            URI uri = exchange.getRequestURI();
            Matcher matcher = httpHelper.PATTERN_URI.matcher(uri.getPath());
            if (!matcher.matches()) {
                throw new MiniGameException(HttpStatus.BAD_REQUEST, "URI requested is invalid" );
            }

            String actionKey = exchange.getRequestMethod() + "/" + matcher.group(2);
            if (!httpContext.contains(actionKey)) {
                throw new MiniGameException(HttpStatus.BAD_REQUEST, "PathParam and/or Action are invalid");
            }

            String body = httpHelper.readRequestBody(exchange);
            String pathVar = httpHelper.getPathVariable(exchange);
            Map<String, String> queryParam = httpHelper.getQueryParam(exchange);

            Controller controller = getController(actionKey, exchange);
            if (controller != null) {
                controller.execute(actionKey, exchange, body, pathVar, queryParam);
            }

        } catch (MiniGameException e) {
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);

        } catch (IllegalStateException e) {
            log.log(Level.SEVERE, e.getMessage());

        } catch (RuntimeException e){
            log.log(Level.WARNING, e.getMessage());
            httpHelper.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), exchange);
        }
    }

    private Controller getController(String action, HttpExchange exchange){

        switch (action) {
            case Actions.GET_LOGIN:
                return (SessionController) rootContext.get(SessionController.class);
            case Actions.POST_SCORE:
            case Actions.GET_HIGH_SCORE_LIST:
                return (ScoreController) rootContext.get(ScoreController.class);
            default:
                throw new MiniGameException(HttpStatus.NOT_FOUND, "Action not found");
        }
    }

}
