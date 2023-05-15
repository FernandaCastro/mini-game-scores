package org.minigame;

import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.MiniGameHttpServer;
import org.minigame.configuration.PurgeTask;
import org.minigame.configuration.RootContext;
import org.minigame.score.ScoreController;
import org.minigame.score.ScoreRepository;
import org.minigame.score.ScoreService;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MiniGameScoresApplication {

    static {
        try {
            LogManager.getLogManager().readConfiguration(MiniGameScoresApplication.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Logger log = Logger.getLogger(MiniGameScoresApplication.class.getName());

    public static void main(String[] args) throws IOException {

        try(InputStream input = MiniGameScoresApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                String value = properties.getProperty(name);
                System.setProperty(name, value);
            }
        }catch (SecurityException|IOException ex){
            log.severe(ex.getMessage());
            throw ex;
        }

        int port = Integer.parseInt(System.getProperty("server.port"));
        int threadPool = Integer.parseInt(System.getProperty("server.threadPool"));
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args.length == 2){
                threadPool = Integer.parseInt(args[1]);
            }
        }

        var rootContext = new RootContext(Clock.systemUTC());
        rootContext.add(new HttpHelper());
        rootContext.add(new SessionRepository());
        rootContext.add(new SessionService((SessionRepository)rootContext.get(SessionRepository.class), rootContext.getClock()));
        rootContext.add(new SessionController((SessionService) rootContext.get(SessionService.class)));
        rootContext.add(new ScoreRepository(new ConcurrentHashMap<>()));
        rootContext.add(new ScoreService((ScoreRepository)rootContext.get(ScoreRepository.class)));
        rootContext.add(new ScoreController((SessionService)rootContext.get(SessionService.class), (ScoreService)rootContext.get(ScoreService.class)));

        PurgeTask purgeTask = new PurgeTask((SessionService)rootContext.get(SessionService.class), (ScoreService)rootContext.get(ScoreService.class));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(purgeTask, Duration.ofMinutes(10).toMillis(), Duration.ofMinutes(10).toMillis());

        new MiniGameHttpServer(rootContext).start(port, threadPool);
    }

}
