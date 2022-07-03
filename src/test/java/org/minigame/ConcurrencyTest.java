package org.minigame;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.minigame.configuration.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyTest {

    @BeforeAll
    static void init(){
        ApplicationTestingUtils.startupHttpServer(8081, 100);
    }

    @AfterAll
    static void close(){
        ApplicationTestingUtils.stopHttpServer();
    }


    @Test
    public void concurrencyTest() throws InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();
        BlockingQueue<String> sessionKeyQueue = new LinkedBlockingQueue<>();
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        int nLoginWorkers = 210;
        int nScoreWorkers = 200;
        int nRankingWorker = 100;

        CountDownLatch allDoneSignal = new CountDownLatch(nLoginWorkers + nScoreWorkers + nRankingWorker);

        System.out.println("Total number of threads: " + (nLoginWorkers + nScoreWorkers + nRankingWorker));
        long start = Instant.now().toEpochMilli();

        //Login and store a minimum number of the shared session keys
        int nMinLoginDone = nLoginWorkers/2;
        CountDownLatch loginSignal = new CountDownLatch(nMinLoginDone);

        List<Thread> loginWorkers = Stream
                .generate(() -> new Thread(new LoginWorker(loginSignal, allDoneSignal, sessionKeyQueue, errors, client)))
                .limit(nLoginWorkers)
                .collect(toList());
        loginWorkers.forEach(Thread::start);

        loginSignal.await();
        System.out.println("Minimum number of Login threads has completed");


        //Store a minimum number of scores using the shared sessions keys
        int nMinScoreDone = nScoreWorkers/2;
        CountDownLatch scoreSignal = new CountDownLatch(nMinScoreDone);

        List<Thread> scoreWorkers = Stream
                .generate(() -> new Thread(new ScoreWorker(scoreSignal, allDoneSignal, sessionKeyQueue, errors, client)))
                .limit(200)
                .collect(toList());
        scoreWorkers.forEach(Thread::start);

        scoreSignal.await();
        System.out.println("Minimum number of Score threads has completed");


        //Get Ranking with at least a number of threads in parallel
        int nMinRankingParallel = nRankingWorker/2;
        CyclicBarrier rankingBarrier = new CyclicBarrier(nMinRankingParallel);
        HttpClient clientRanking = HttpClient.newBuilder().build();

        List<Thread> rankingWorkers = Stream
                .generate(() -> new Thread(new RankingWorker(rankingBarrier, allDoneSignal, sessionKeyQueue, errors, client)))
                .limit(100)
                .collect(toList());
        rankingWorkers.forEach(Thread::start);
        System.out.println("All Ranking threads have started");

        //Wait for all threads to finish
        allDoneSignal.await();

        long end = Instant.now().toEpochMilli();
        System.out.println("Processing time(ms): " + Duration.ofMillis(end-start).toMillis());

        if(errors.size()>0){
            for (String error:errors) {
                System.out.println(error);
            }
        }
        assertEquals(0, errors.size());

    }

    class LoginWorker implements Runnable{

        private final CountDownLatch loginSignal;
        private final CountDownLatch doneSignal;
        private final BlockingQueue<String> sessionKeyQueue;
        private final List<String> errors;
        private final HttpClient client;

        LoginWorker(CountDownLatch loginSignal, CountDownLatch doneSignal, BlockingQueue<String> sessionKeyQueue, List<String> errors, HttpClient client) {
            this.loginSignal = loginSignal;
            this.doneSignal = doneSignal;
            this.sessionKeyQueue = sessionKeyQueue;
            this.errors = errors;
            this.client = client;
        }

        @Override
        public void run() {
            int userId = new Random().ints(1, 1000, 2000).findFirst().getAsInt();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/"+ userId + "/login")).build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (HttpStatus.OK.getStatusCode() == response.statusCode()){
                    sessionKeyQueue.put(response.body());
                }else{
                    errors.add(response.statusCode() + ": Login : " + response.body());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            loginSignal.countDown();
            doneSignal.countDown();
        }
    }

    class ScoreWorker implements Runnable{

        private final CountDownLatch scoreSignal;
        private final CountDownLatch doneSignal;
        private final BlockingQueue<String> sessionKeyQueue;
        private final List<String> errors;
        private final HttpClient client;

        ScoreWorker(CountDownLatch scoreSignal, CountDownLatch doneSignal, BlockingQueue<String> sessionKeyQueue, List<String> errors, HttpClient client) {
            this.scoreSignal = scoreSignal;
            this.doneSignal = doneSignal;
            this.sessionKeyQueue = sessionKeyQueue;
            this.errors = errors;
            this.client = client;
        }

        @Override
        public void run() {
            try {

                String sessionKey = sessionKeyQueue.take();

                int levelId = new Random().ints(1, 0, 9).findFirst().getAsInt();
                String score = String.valueOf(new Random().ints(1, 1000, 10000).findFirst().getAsInt());

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/"+  levelId + "/score?sessionkey="+sessionKey))
                        .POST(HttpRequest.BodyPublishers.ofString(score))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (HttpStatus.OK.getStatusCode() != response.statusCode()){
                    errors.add(response.statusCode() + ": Score" + response.body());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            scoreSignal.countDown();
            doneSignal.countDown();
        }
    }

    class RankingWorker implements Runnable{

        private final CyclicBarrier barrierRanking;
        private final CountDownLatch doneSignal;
        private final BlockingQueue<String> sessionKeyQueue;
        private final List<String> errors;
        private final HttpClient client;

        RankingWorker(CyclicBarrier barrierRanking, CountDownLatch doneSignal, BlockingQueue<String> sessionKeyQueue, List<String> errors, HttpClient client) {
            this.barrierRanking = barrierRanking;
            this.doneSignal = doneSignal;
            this.sessionKeyQueue = sessionKeyQueue;
            this.errors = errors;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                barrierRanking.await();

                int levelId = new Random().ints(1, 0, 9).findFirst().getAsInt();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/"+  levelId + "/highscorelist")).build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (HttpStatus.OK.getStatusCode() != response.statusCode()){
                    errors.add(response.statusCode() + ": HighScores" + response.body());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            doneSignal.countDown();
        }
    }
}
