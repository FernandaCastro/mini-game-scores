package org.minigame;

import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.MiniGameHttpServer;
import org.minigame.configuration.PurgeTask;
import org.minigame.configuration.RootContext;
import org.minigame.score.Score;
import org.minigame.score.ScoreController;
import org.minigame.score.ScoreRepository;
import org.minigame.score.ScoreService;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ApplicationTestingUtils {

    static MiniGameHttpServer httpServer;

    public static void startupHttpServer(int port, int threadPool){
        var rootContext = new RootContext(Clock.systemUTC());
        rootContext.add(new HttpHelper());
        rootContext.add(new SessionRepository());
        rootContext.add(new SessionService((SessionRepository)rootContext.get(SessionRepository.class), rootContext.getClock()));
        rootContext.add(new SessionController((HttpHelper)rootContext.get(HttpHelper.class), (SessionService) rootContext.get(SessionService.class)));
        rootContext.add(new ScoreRepository(new ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>>()));
        rootContext.add(new ScoreService((ScoreRepository)rootContext.get(ScoreRepository.class)));
        rootContext.add(new ScoreController((HttpHelper)rootContext.get(HttpHelper.class), (SessionService)rootContext.get(SessionService.class), (ScoreService)rootContext.get(ScoreService.class)));

        httpServer = new MiniGameHttpServer(rootContext);
        httpServer.start(port, threadPool);
    }

    public static void stopHttpServer(){
        httpServer.stop();
    }
}
