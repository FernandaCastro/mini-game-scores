package org.minigame.score;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.configuration.*;
import org.minigame.session.Session;
import org.minigame.session.SessionService;

public class ScoreController implements Controller {

    private final HttpHelper httpHelper;
    private final SessionService sessionService;
    private final ScoreService scoreService;


    public ScoreController(HttpHelper httpHelper, SessionService sessionService, ScoreService scoreService) {
        this.httpHelper = httpHelper;
        this.sessionService = sessionService;
        this.scoreService = scoreService;
    }

    protected void registerScore(int levelId, HttpExchange exchange) {
        try {
            int score = getScore(exchange);

            String sessionKey = getSessionKey(exchange);

            if (sessionService.isValid(sessionKey)) {

                Session session = sessionService.get(sessionKey);

                scoreService.save(new Score(levelId, score, session.getUserId()));
                httpHelper.sendResponse(HttpStatus.OK, "", exchange);

            }else{
                httpHelper.sendResponse(HttpStatus.UNAUTHORIZED, "Invalid session. Please log in again.", exchange);
            }
        }catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    protected void getHighScoreList(int levelId, HttpExchange exchange)  {

        try {

            String scores = scoreService.getHighestScores(levelId);
            httpHelper.sendResponse(HttpStatus.OK, scores, exchange);

        } catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    @Override
    public void execute(String action, HttpExchange exchange) {
        try {
            int levelId = getLevelId(exchange);

            switch (action) {

                case Actions.POST_SCORE:
                    registerScore(levelId, exchange);
                    break;

                case Actions.GET_HIGH_SCORE_LIST:
                    getHighScoreList(levelId, exchange);
                    break;
            }

        }catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    private int getLevelId(HttpExchange exchange){
        String pathVar = httpHelper.getPathVariable(exchange);
        if (pathVar==null) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is missing");
        }

        try {
            return Integer.parseInt(pathVar);
        }catch (NumberFormatException e) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is invalid");
        }
    }

    private String getSessionKey(HttpExchange exchange){
        var queryParam = httpHelper.getQueryParam(exchange);
        if (queryParam == null || !queryParam.containsKey(httpHelper.SESSION_KEY)) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "Missing or invalid <sessionkey> parameter");
        }

        return queryParam.get(httpHelper.SESSION_KEY);
    }

    private int getScore(HttpExchange exchange){
        String body = httpHelper.readRequestBody(exchange);

        if (body.isBlank()) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<Score> is missing");
        }

        try {
            return Integer.parseInt(body);
        } catch (NumberFormatException e) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<Score> is invalid (" + body + ")");
        }
    }

}
