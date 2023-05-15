package org.minigame.configuration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HttpDispatcherHandler implements HttpHandler {

    private static final Logger log = Logger.getLogger(HttpDispatcherHandler.class.getName());

    private final RootContext rootContext;
    private final HttpHelper httpHelper;

    public HttpDispatcherHandler(RootContext rootContext, HttpHelper httpHelper) {
        this.rootContext = rootContext;
        this.httpHelper = httpHelper;
    }

    private final Map<Pattern, String> uriContext = Map.ofEntries(
            Map.entry(Pattern.compile("^/(?<userId>\\d*)/login"), "org.minigame.session.SessionController::login"),
            Map.entry(Pattern.compile("^/(?<levelId>\\d*)/score"), "org.minigame.score.ScoreController::registerScore"),
            Map.entry(Pattern.compile("^/(?<levelId>\\d*)/highscorelist"), "org.minigame.score.ScoreController::getHighScoreList")
    );

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var classMethod = lookupURI(exchange);
            var method = getMethod(exchange, classMethod);

            String pathVar = httpHelper.getPathVariable(exchange);
            Map<String, String> queryParam = httpHelper.getQueryParam(exchange);

            MiniGameResponse response;

            if (exchange.getRequestMethod().equals("POST")) {
                String body = httpHelper.readRequestBody(exchange);
                response = (MiniGameResponse) method.invoke(rootContext.get(Class.forName(classMethod[0])), body, pathVar, queryParam);

            } else {
                response = (MiniGameResponse) method.invoke(rootContext.get(Class.forName(classMethod[0])), pathVar, queryParam);
            }

            httpHelper.sendResponse(response.getHttpStatus(), response.getMessage(), exchange);

        } catch (MiniGameException e) {
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);

        }catch (InvocationTargetException e){

            if (e.getTargetException()!=null && e.getTargetException() instanceof MiniGameException){
                var appException = (MiniGameException)e.getTargetException();
                httpHelper.sendResponse(appException.getHttpStatus(), appException.getMessage(), exchange);

            }else{
                log.log(Level.WARNING, e.getMessage());
                httpHelper.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), exchange);
            }

        } catch (IllegalStateException e) {
            log.log(Level.SEVERE, e.getMessage());

        }  catch (RuntimeException|ReflectiveOperationException e) {
            log.log(Level.WARNING, e.getMessage());
            httpHelper.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), exchange);
        }
    }

    private String[] lookupURI(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String[] classMethod = null;

        for (Pattern entry : uriContext.keySet()) {
            if (entry.matcher(uri.getPath()).matches()) {
                classMethod = uriContext.get(entry).split("::");
                break;
            }
        }
        if (classMethod == null) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "URI requested is invalid");
        }
        return classMethod;
    }

    private Method getMethod(HttpExchange exchange, String[] classMethod) throws ClassNotFoundException, NoSuchMethodException {

        var classController = Class.forName(classMethod[0]);
        Method method;

        if (exchange.getRequestMethod().equals("POST")) {
            method = classController.getMethod(classMethod[1], String.class, String.class, Map.class);
        } else {
            method = classController.getMethod(classMethod[1], String.class, Map.class);
        }

        return method;
    }

}
