import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.MiniGameHttpServer;
import org.minigame.configuration.RootContext;
import org.minigame.level.LevelController;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;

import java.time.Clock;

public class MiniGameScoresApplication {

    public static void main(String[] args) {
        int port = 8081;
        int threadPool = 10;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args[1] != null){
                threadPool = Integer.parseInt(args[1]);
            }
        }

        var rootContext = new RootContext(Clock.systemUTC());
        rootContext.add(new HttpHelper());
        rootContext.add(new SessionRepository());
        rootContext.add(new SessionService((SessionRepository)rootContext.get(SessionRepository.class), rootContext.getClock()));
        rootContext.add(new SessionController((HttpHelper)rootContext.get(HttpHelper.class), (SessionService) rootContext.get(SessionService.class)));
        rootContext.add(new LevelController((HttpHelper)rootContext.get(HttpHelper.class)));

        new MiniGameHttpServer(rootContext).start(port, threadPool);
    }
}
