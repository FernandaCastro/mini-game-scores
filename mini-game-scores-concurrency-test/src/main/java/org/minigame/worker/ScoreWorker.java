package org.minigame.worker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreWorker implements Runnable{

    Logger log = Logger.getLogger(ScoreWorker.class.getName());

    private final CyclicBarrier scoreSignal;
    private final CountDownLatch doneSignal;
    private final BlockingQueue<String> sessionKeyQueue;
    private final List<String> errors;
    private final HttpClient client;
    private final String baseUri;

    public ScoreWorker(CyclicBarrier scoreSignal, CountDownLatch doneSignal, BlockingQueue<String> sessionKeyQueue, List<String> errors, HttpClient client, String baseUri) {
        this.scoreSignal = scoreSignal;
        this.doneSignal = doneSignal;
        this.sessionKeyQueue = sessionKeyQueue;
        this.errors = errors;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public void run() {
        try {
            scoreSignal.await();

            log.log(Level.FINE, "ScoreWorker-{0}", List.of(Thread.currentThread().getId()).toArray());
            String sessionKey = sessionKeyQueue.poll(2, TimeUnit.SECONDS);
            if(sessionKey==null){
                sessionKey="";
            }

            int levelId = new Random().ints(1, 0, 9).findFirst().getAsInt();
            String score = String.valueOf(new Random().ints(1, 1000, 10000).findFirst().getAsInt());

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUri +  levelId + "/score?sessionkey="+sessionKey))
                    .POST(HttpRequest.BodyPublishers.ofString(score))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 != response.statusCode()){
                errors.add(response.statusCode() + ": Score " + response.body());
            }

        } catch (InterruptedException | IOException | BrokenBarrierException e) {
            log.log(Level.WARNING, "LoginWorker: {0} : {1}", List.of(e.getClass().getName(), e.getMessage()).toArray());
        }

        doneSignal.countDown();
    }
}
