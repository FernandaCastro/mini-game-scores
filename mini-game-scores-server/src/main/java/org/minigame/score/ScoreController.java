package org.minigame.score;

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

    protected MiniGameResponse registerScore(int levelId, int score, String sessionKey) {

        if (sessionService.isValid(sessionKey)) {

            Session session = sessionService.get(sessionKey);

            scoreService.save(new Score(levelId, score, session.getUserId()));
            return new MiniGameResponse(HttpStatus.OK, "");

        } else {
            return new MiniGameResponse(HttpStatus.UNAUTHORIZED, "Invalid session. Please log in again.");
        }

    }

    protected MiniGameResponse getHighScoreList(int levelId) {

        String scores = scoreService.getHighestScores(levelId);
        return new MiniGameResponse(HttpStatus.OK, scores);

    }

    @Override
    public MiniGameResponse execute(String action, String body, String pathVar, Map<String, String> queryParam) {
        try {
            int levelId = getLevelId(pathVar);

            switch (action) {

                case Actions.POST_SCORE:
                    int score = getScore(body);
                    String sessionKey = getSessionKey(queryParam);
                    return registerScore(levelId, score, sessionKey);

                case Actions.GET_HIGH_SCORE_LIST:
                    return getHighScoreList(levelId);
                default:
                    return new MiniGameResponse(HttpStatus.NOT_FOUND, "Action not found");
            }
        }catch(MiniGameException e){
            return new MiniGameResponse(e.getHttpStatus(), e.getMessage());
        }
    }

    private int getLevelId(String pathVar){

        if (pathVar==null) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is missing");
        }

        try {
            return Integer.parseInt(pathVar);
        }catch (NumberFormatException e) {
            throw new MiniGameException(HttpStatus.BAD_REQUEST, "<LevelId> is invalid");
        }
    }

    private String getSessionKey(Map<String, String> queryParam){
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
