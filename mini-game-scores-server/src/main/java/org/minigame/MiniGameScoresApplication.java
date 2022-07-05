package org.minigame;

import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.MiniGameHttpServer;
import org.minigame.configuration.RootContext;
import org.minigame.score.ScoreController;
import org.minigame.score.ScoreRepository;
import org.minigame.score.ScoreService;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;
import java.time.Clock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniGameScoresApplication {

    private static Logger log = Logger.getLogger(MiniGameScoresApplication.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {

        int port = 8081;
        int threadPool = 100;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args.length == 2){
                threadPool = Integer.parseInt(args[1]);
            }
        }

        String logLevel = System.getProperty("logLevel");
        if(logLevel !=null){
            setLevel(logLevel);
        }

        var rootContext = new RootContext(Clock.systemUTC());
        rootContext.add(new HttpHelper());
        rootContext.add(new SessionRepository());
        rootContext.add(new SessionService((SessionRepository)rootContext.get(SessionRepository.class), rootContext.getClock()));
        rootContext.add(new SessionController((HttpHelper)rootContext.get(HttpHelper.class), (SessionService) rootContext.get(SessionService.class)));
        rootContext.add(new ScoreRepository());
        rootContext.add(new ScoreService((ScoreRepository)rootContext.get(ScoreRepository.class)));
        rootContext.add(new ScoreController((HttpHelper)rootContext.get(HttpHelper.class), (SessionService)rootContext.get(SessionService.class), (ScoreService)rootContext.get(ScoreService.class)));

        new MiniGameHttpServer(rootContext).start(port, threadPool);
    }


    private static void setLevel(String logLevel) {
        Level targetLevel = null;
        try {
            targetLevel = Level.parse(logLevel);
            Logger root = Logger.getLogger("");
            root.setLevel(targetLevel);
            for (Handler handler : root.getHandlers()) {
                handler.setLevel(targetLevel);
            }
        }catch (IllegalArgumentException e){
            log.warning("logLevel invalid [" + logLevel + "]. Default Log Level in use.");
        }
    }
}
