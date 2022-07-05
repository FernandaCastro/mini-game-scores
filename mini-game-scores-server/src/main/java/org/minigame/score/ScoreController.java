package org.minigame.score;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.configuration.*;
import org.minigame.session.Session;
import org.minigame.session.SessionService;

import java.util.Map;

public class ScoreController implements Controller {

    private final HttpHelper httpHelper;
    private final SessionService sessionService;
    private final ScoreService scoreService;

    private final String SESSION_KEY = "sessionkey";

    public ScoreController(HttpHelper httpHelper, SessionService sessionService, ScoreService scoreService) {
        this.httpHelper = httpHelper;
        this.sessionService = sessionService;
        this.scoreService = scoreService;
    }

    protected void registerScore(int levelId, int score, String sessionKey, HttpExchange exchange) {
        try {

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
    public void execute(String action, HttpExchange exchange, String body, String pathVar, Map<String, String> queryParam) {
        try {
            int levelId = getLevelId(exchange, pathVar);

            switch (action) {

                case Actions.POST_SCORE:
                    int score = getScore(body);
                    String sessionKey = getSessionKey(exchange, queryParam);
                    registerScore(levelId, score, sessionKey, exchange);
                    break;

                case Actions.GET_HIGH_SCORE_LIST:
                    getHighScoreList(levelId, exchange);
                    break;
            }

        }catch(MiniGameException e){
            httpHelper.sendResponse(e.getHttpStatus(), e.getMessage(), exchange);
        }
    }

    private int getLevelId(HttpExchange exchange, String pathVar){

        if (pathVar==null) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is missing");
        }

        try {
            return Integer.parseInt(pathVar);
        }catch (NumberFormatException e) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is invalid");
        }
    }

    private String getSessionKey(HttpExchange exchange, Map<String, String> queryParam){
        if (queryParam == null || !queryParam.containsKey(SESSION_KEY) || queryParam.get(SESSION_KEY)==null || queryParam.get(SESSION_KEY).isBlank()) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "Missing or invalid <sessionkey> parameter");
        }

        return queryParam.get(SESSION_KEY);
    }

    private int getScore(String body){
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
