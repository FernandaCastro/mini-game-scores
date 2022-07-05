package org.minigame.worker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginWorker implements Runnable{

    private static Logger log = Logger.getLogger(LoginWorker.class.getName());

    private final CyclicBarrier loginSignal;
    private final CountDownLatch doneSignal;
    private final BlockingQueue<String> sessionKeyQueue;
    private final List<String> errors;
    private final HttpClient client;
    private final String baseUri;

    public LoginWorker(CyclicBarrier loginSignal, CountDownLatch doneSignal, BlockingQueue<String> sessionKeyQueue, List<String> errors, HttpClient client, String baseUri) {
        this.loginSignal = loginSignal;
        this.doneSignal = doneSignal;
        this.sessionKeyQueue = sessionKeyQueue;
        this.errors = errors;
        this.client = client;
        this.baseUri = baseUri;
    }

    @Override
    public void run() {
        try {
            loginSignal.await();

            int userId = new Random().ints(1, 1000, 2000).findFirst().getAsInt();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUri + "/" + userId + "/login")).build();
            HttpResponse<String> response = null;

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 == response.statusCode()) {
                sessionKeyQueue.add(response.body());

            } else {
                errors.add(response.statusCode() + ": Login : " + response.body());
            }

        } catch (IOException | InterruptedException | BrokenBarrierException e) {
            log.log(Level.WARNING, "LoginWorker: {0} : {1}", List.of(e.getClass().getName(), e.getMessage()).toArray());
        }

        //loginSignal.countDown();
        doneSignal.countDown();
    }
}
