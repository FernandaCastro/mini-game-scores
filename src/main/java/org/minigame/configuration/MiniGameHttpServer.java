package org.minigame.configuration;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MiniGameHttpServer {

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
            System.out.println("HttpServer started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        httpServer.stop(0);
    }
}
