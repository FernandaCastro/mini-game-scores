package org.minigame.configuration;

import org.minigame.MiniGameScoresApplication;
import org.minigame.score.ScoreService;
import org.minigame.session.SessionService;

import java.time.Clock;
import java.util.TimerTask;
import java.util.logging.Logger;

public class PurgeTask extends TimerTask {

    private static Logger LOGGER = Logger.getLogger(PurgeTask.class.getName());

    private final SessionService sessionService;
    private final ScoreService scoreService;

    public PurgeTask(SessionService sessionService, ScoreService scoreService) {
        this.sessionService = sessionService;
        this.scoreService = scoreService;
    }

    @Override
    public void run() {
        long start = Clock.systemUTC().millis();
        int sessions = sessionService.purge();
        int scores = scoreService.purge();
        long end = Clock.systemUTC().millis() - start;
        LOGGER.info("Purge Task executed in " + end + "ms");
        LOGGER.info("Sessions purged: " + sessions);
        LOGGER.info("Scores purged: " + scores);
    }
}
