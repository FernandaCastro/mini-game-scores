package org.minigame.worker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RankingWorker implements Runnable{

    private static Logger log = Logger.getLogger(RankingWorker.class.getName());

    private final CyclicBarrier barrierRanking;
    private final CountDownLatch doneSignal;
    private final List<String> errors;
    private final HttpClient client;
    private final String baseUri;

    public RankingWorker(CyclicBarrier barrierRanking, CountDownLatch doneSignal, List<String> errors, HttpClient client, String baseUri) {
        this.barrierRanking = barrierRanking;
        this.doneSignal = doneSignal;
        this.errors = errors;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public void run() {
        try {
            barrierRanking.await();

            log.log(Level.FINE, "Ranking-{0}", List.of(Thread.currentThread().getName()).toArray());

            int levelId = new Random().ints(1, 0, 9).findFirst().getAsInt();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUri +  levelId + "/highscorelist")).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 != response.statusCode()){
                errors.add(response.statusCode() + ": HighScores" + response.body());
            }

        } catch (InterruptedException | BrokenBarrierException | IOException e) {
            log.log(Level.WARNING, "LoginWorker: {0} : {1}", List.of(e.getClass().getName(), e.getMessage()).toArray());
        }

        doneSignal.countDown();
    }
}
