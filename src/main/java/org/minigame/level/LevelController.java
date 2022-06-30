package org.minigame.level;

import com.sun.net.httpserver.HttpExchange;
import org.minigame.configuration.Action;
import org.minigame.configuration.Controller;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.HttpStatus;

import java.io.IOException;

public class LevelController implements Controller {

    private final HttpHelper httpHelper;

    public LevelController(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public void getScore(int levelId, HttpExchange exchange) throws IOException {

        httpHelper.sendResponse(HttpStatus.OK, "Execute POST Score action. LevelId: " + levelId, exchange);
    }

    public void getHighScoreList(int levelId, HttpExchange exchange) throws IOException {

        httpHelper.sendResponse(HttpStatus.OK, "Execute GET HighScoreList action. LevelId: " + levelId, exchange);
    }

    @Override
    public void execute(String action, HttpExchange exchange) throws IOException {
        String pathVar = httpHelper.getPathVariable(exchange);
        if (pathVar==null) {
            httpHelper.sendResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getMessage(), exchange);
            return;
        }
        int levelId = Integer.parseInt(pathVar);

        switch (action) {
            case Action.POST_SCORE -> getScore(levelId, exchange);
            case Action.GET_HIGH_SCORE_LIST -> getHighScoreList(levelId, exchange);
        }
    }
}
