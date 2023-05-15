package org.minigame.score;

import org.minigame.configuration.*;
import org.minigame.session.Session;
import org.minigame.session.SessionService;

import java.util.Map;

public class ScoreController {

    private final SessionService sessionService;
    private final ScoreService scoreService;

    private final String SESSION_KEY = "sessionkey";

    public ScoreController(SessionService sessionService, ScoreService scoreService) {
        this.sessionService = sessionService;
        this.scoreService = scoreService;
    }

    public MiniGameResponse registerScore(String body, String pathVar, Map<String, String> queryParam) {
        int levelId = getLevelId(pathVar);
        int score = getScore(body);
        String sessionKey = getSessionKey(queryParam);

        if (sessionService.isValid(sessionKey)) {

            Session session = sessionService.get(sessionKey);

            scoreService.save(new Score(levelId, score, session.getUserId()));
            return new MiniGameResponse(HttpStatus.OK, "");

        } else {
            return new MiniGameResponse(HttpStatus.UNAUTHORIZED, "Invalid session. Please log in again.");
        }
    }

    public MiniGameResponse getHighScoreList(String pathVar, Map<String, String> queryParam) {

        int levelId = getLevelId(pathVar);

        String scores = scoreService.getHighestScores(levelId);
        return new MiniGameResponse(HttpStatus.OK, scores);

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
