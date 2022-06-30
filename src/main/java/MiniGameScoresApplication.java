import com.sun.net.httpserver.HttpServer;
import org.minigame.configuration.HttpDispatcherHandler;
import org.minigame.configuration.HttpHelper;
import org.minigame.configuration.RootContext;
import org.minigame.level.LevelController;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Clock;

public class MiniGameScoresApplication {

    public static void main(String[] args) {
        int port = 8081;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        var clock = Clock.systemUTC();
        var httpHelper = new HttpHelper();
        var sessionRepository = new SessionRepository();
        var sessionService = new SessionService(sessionRepository, clock);
        var rootContext = new RootContext(
                clock,
                httpHelper,
                sessionRepository,
                sessionService,
                new SessionController(httpHelper, sessionService),
                new LevelController(httpHelper)
        );

        try {
            var httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/", new HttpDispatcherHandler(rootContext, (HttpHelper) rootContext.getBean(HttpHelper.class)));
            httpServer.setExecutor(null);
            httpServer.start();
            System.out.println("HttpServer started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
