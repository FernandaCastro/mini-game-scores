import com.sun.net.httpserver.HttpServer;
import org.minigame.HttpDispatcherHandler;
import org.minigame.HttpHelper;
import org.minigame.RootContext;
import org.minigame.level.LevelController;
import org.minigame.user.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MiniGameScoresApplication {

    public static void main(String[] args) {
        int port = 8081;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        var httpHelper = new HttpHelper();
        var rootContext = new RootContext(
                httpHelper,
                new UserController(httpHelper),
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
