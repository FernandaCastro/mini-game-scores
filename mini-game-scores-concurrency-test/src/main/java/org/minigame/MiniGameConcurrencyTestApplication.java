package org.minigame;

import org.minigame.worker.LoginWorker;
import org.minigame.worker.RankingWorker;
import org.minigame.worker.ScoreWorker;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniGameConcurrencyTestApplication {

    private static Logger log = Logger.getLogger(MiniGameConcurrencyTestApplication.class.getName());

    public static void main(String[] args) {

        String baseUri = "http://localhost:8081/";
        int nLoginWorkers = 1550;
        int nScoreWorkers = 1500;
        int nRankingWorker = 800;
        if (args != null && args.length==5){
            baseUri = "http://"+args[0]+":"+args[1]+"/";
            nLoginWorkers = Integer.parseInt(args[0]);
            nScoreWorkers = Integer.parseInt(args[0]);
            nRankingWorker = Integer.parseInt(args[0]);
        }

        int nTotalThreads = nLoginWorkers + nScoreWorkers + nRankingWorker;

        HttpClient client = HttpClient.newBuilder().build();
        BlockingQueue<String> sessionKeyQueue = new LinkedBlockingQueue<>();
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        CyclicBarrier allStartedSignal = new CyclicBarrier(nTotalThreads);
        CountDownLatch allDoneSignal = new CountDownLatch(nTotalThreads);

        log.log(Level.INFO, "Total number of threads: {0}", List.of(nTotalThreads).toArray());
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

        try {
            allDoneSignal.await(); //Wait for all threads to finish

        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e.getStackTrace().toString());
        }

        log.log(Level.INFO, "Waiting all threads to complete");

        long end = Instant.now().toEpochMilli();
        log.log(Level.INFO, "Processing time(ms): {0}", Duration.ofMillis(end-start).toMillis());

        log.log(Level.INFO, "Errors: " + errors.size());
        if(errors.size()>0){
            for (String error:errors) {
                log.info(error);
            }
        }
    }
}
