package org.minigame.configuration;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniGameHttpServer {

    private static Logger log = Logger.getLogger(MiniGameHttpServer.class.getName());

    private final RootContext rootContext;
    private HttpServer httpServer;

    public MiniGameHttpServer(RootContext rootContext) {
        this.rootContext = rootContext;
    }

    public void start(int port, int threadPool){
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/", new HttpDispatcherHandler(rootContext, (HttpHelper) rootContext.get(HttpHelper.class)));
            httpServer.setExecutor((ThreadPoolExecutor)Executors.newFixedThreadPool(threadPool));
            httpServer.start();
            log.log(Level.INFO, "HttpServer started on port: {0} - threadPool: {1} ", new Integer[]{port, threadPool});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        httpServer.stop(0);
    }
}
