package org.minigame;

import org.minigame.worker.LoginWorker;
import org.minigame.worker.RankingWorker;
import org.minigame.worker.ScoreWorker;

import java.lang.annotation.Target;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniGameConcurrencyTestApplication {

    private static Logger log = Logger.getLogger(MiniGameConcurrencyTestApplication.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {

        MiniGameConcurrencyTestApplication concurrencyTest = new MiniGameConcurrencyTestApplication();

        String hostname = "localhost";
        int port = 8081;
        int nLoginWorkers = 1500;
        int nScoreWorkers = 1450;
        int nRankingWorker = 1000;

        String logLevel = System.getProperty("logLevel");
        if(logLevel !=null){
            concurrencyTest.setLevel(logLevel);
        }

        String _hostname = System.getProperty("hostname");
        if(_hostname !=null){
            hostname = _hostname;
        }

        String _port = System.getProperty("port");
        if(_port !=null){
            port = Integer.parseInt(_port);
        }

        String _nLoginWorkers = System.getProperty("login");
        if(_nLoginWorkers !=null){
            nLoginWorkers = Integer.parseInt(_nLoginWorkers);
        }

        String _nScoreWorkers = System.getProperty("score");
        if(_nScoreWorkers !=null){
            nScoreWorkers = Integer.parseInt(_nScoreWorkers);
        }

        String _nRankingWorkers = System.getProperty("ranking");
        if(_nRankingWorkers !=null){
            nRankingWorker = Integer.parseInt(_nRankingWorkers);
        }

        String baseUri = "http://"+ hostname + ":"+ port;

        concurrencyTest.process(baseUri,  nLoginWorkers, nScoreWorkers, nRankingWorker);

    }

    private void process(String baseUri, int nLoginWorkers, int nScoreWorkers, int nRankingWorker){
        int nTotalThreads = nLoginWorkers + nScoreWorkers + nRankingWorker;

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        BlockingQueue<String> sessionKeyQueue = new LinkedBlockingQueue<>();
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        CyclicBarrier allStartedSignal = new CyclicBarrier(nTotalThreads);
        CountDownLatch allDoneSignal = new CountDownLatch(nTotalThreads);

        log.log(Level.INFO, "Total concurrent threads: {0}", List.of(nTotalThreads).toArray());
        long start = Instant.now().toEpochMilli();

        //GET Login requests
        for(int i=0; i<nLoginWorkers; i++){
            new Thread(new LoginWorker(allStartedSignal, allDoneSignal, sessionKeyQueue, errors, client, baseUri)).start();
        }

        //POST Score requests
        for(int i=0; i<nScoreWorkers; i++){
            new Thread(new ScoreWorker(allStartedSignal, allDoneSignal, sessionKeyQueue, errors, client, baseUri)).start();
        }

        //GET HighestScores requests
        for(int i=0; i<nRankingWorker; i++){
            new Thread(new RankingWorker(allStartedSignal, allDoneSignal, errors, client, baseUri)).start();
        }

        log.log(Level.INFO, "All threads started");
        log.log(Level.INFO, "Waiting threads to complete");
        try {
            allDoneSignal.await(); //Wait for all threads to finish

        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e.getStackTrace().toString());
        }

        log.log(Level.INFO, "All threads complete");

        long end = Instant.now().toEpochMilli();
        log.log(Level.INFO, "Processing time(ms): {0}", Duration.ofMillis(end-start).toMillis());

        log.log(Level.INFO, "NOT OK[200] Responses: " + errors.size());
        if(errors.size()>0){
            errors.stream()
                    .distinct()
                    .forEach(error -> log.info(error));
        }
    }

    private void setLevel(String logLevel) {
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
