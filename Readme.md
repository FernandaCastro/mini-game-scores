##Mini Game Scores

Coded and tested using ```JDK 11```

Mini Game Scores is an HTTP-based mini game back-end in Java which registers game scores for different users and
levels, with the capability to return a list ranking the 15 highest scores of a level.

It has a simple login-system in place (without any authentication) which returns a session key that is valid for 10 minutes.

It accepts the following HTTP Requests:

````
 Request: GET /<userid>/login 
 Response: <sessionkey>
 
 <userid> : 31 bit unsigned integer number
 <sessionkey> : A string representing a session (valid for 10 minutes).
 
 --- Example ---
 Request: curl http://localhost:8081/4711/login
 Response: UICSNDK
````

```
 Request: POST /<levelid>/score?sessionkey=<sessionkey>
 Request Body: <score>
 Response: (nothing)

 <levelid> : 31 bit unsigned integer number
 <sessionkey> : A session key string retrieved from the login function.
 <score> : 31 bit unsigned integer number

 --- Example ---
 Request: curl -X POST http://localhost:8081/1/score?sessionkey=PYNNINJ -H "Content-Type: text/html" -d '10500'
```

````
 Request: GET /<levelid>/highscorelist
 Response: CSV of <userid>=<score>
 
 <levelid> : 31 bit unsigned integer number
 <score> : 31 bit unsigned integer number
 <userid> : 31 bit unsigned integer number

 --- Example ---
 Request: curl http://localhost:8081/1/highscorelist
 Response: 4711=10500,1531=9989,1185=9979,1280=9974,1241=9929,1068=9927,1603=9910,1804=9907,1223=9906,1842=9897,1390=9889,1614=9887,1986=9884,1939=9882
````


###How to build the project
The following instructions will perform **32 tests cases** related the **mini-game-scores-server** module,
and will also generate 2 executable JARs:

- **mini-game-scores-server** : mini-game-scores-server-1.0-jar-with-dependencies.jar
- **mini-game-scores-concurrency-test** : mini-game-scores-concurrency-test-1.0-jar-with-dependencies.jar

1. Navigate to the root project ```mini-game-scores```
2. Run ```mvn clean install```

###How to run mini-game-scores-server
1. Navigate to mini-game-scores-server\target
2. Run ```chmod 775 mini-game-scores-server-1.0-jar-with-dependencies.jar```
3. Run ```java -jar mini-game-scores-server-1.0-jar-with-dependencies.jar [<port> <threadPool>]```
   1. If no parameter is informed it will assume **port:8081** and **threadPool:100**

###Points of Improvements and Considerations
- **Define and implement a __purge process__ for the scores stored in memory.** 
- Refactor the HTTP Route Mapping process, making it more abstract and separate it from the HTTP Dispatch process.
- Improve the process of collecting the 15 highest scores. 
  - Ideally no duplicate userId should be stored when adding a score. 
  - This solution is aware of the inconsistency among equals() and compareTo() for the chosen data structure: "Note: this class has a natural ordering that is inconsistent with equals." 
  - So a second step was needed to skip/remove duplicate userId in the highscorelist
   
###Testing the Concurrency
A small application was developed to test concurrency **[ mini-game-scores-concurrency-test ]**. 

It uses CycleBarrier synchronizer to wait until all thread have started, and it also uses CountDownLatch synchronizer to wait until all threads complete. 

A BlockingQueue<String> is used to share the sessionKey, where the LoginWorker adds sessionKey to the queue, and the ScoreWorker pools from it. 

So keep in mind that when ScoreWorker has no sessionKey to use the request will return: [400: Score Missing or invalid <sessionkey> parameter]   

So that a simple analysis of the responses for the concurrent requests is made at the end.

####How to run the Concurrency Test application
1. Navigate to mini-game-scores-concurrency-test\target
   1. If no JAR is available is this folder, then generate it by running ```mvn clean install```
2. Run ```chmod 775 mini-game-scores-concurrency-test-1.0-jar-with-dependencies.jar```
3. Run ```java [properties] -jar mini-game-scores-concurrency-test-1.0-jar-with-dependencies.jar```
   
   Property | Default | Description
   ---| ---| ---
   hostname | localhost | Mini Game Server hostname
   port | 8081 | Mini Game server port 
   login |1500 | Number of threads sending ``GET /<userid>/login``requests 
   score | 1450 | Number of threads sending ``POST /<levelid>/score?sessionkey=<sessionkey>``requests 
   ranking | 1000 | Number of threads sending ``GET /<levelid>/highscorelist``requests
   logLevel | INFO | Defines the granularity of the log messages: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL 

Example:

```java -Dlogin=1100 -Dscore=1000 -Dranking=500 -jar mini-game-scores-concurrency-test-1.0-jar-with-dependencies.jar```

Example of output:
```
[2022-07-04 15:42:30 793] [INFO   ] Total concurrent threads: 3.950 
[2022-07-04 15:42:32 395] [INFO   ] All threads started 
[2022-07-04 15:42:32 398] [INFO   ] Waiting threads to complete 
[2022-07-04 15:42:39 554] [INFO   ] All threads complete 
[2022-07-04 15:42:39 555] [INFO   ] Processing time(ms): 8.701 
[2022-07-04 15:42:39 558] [INFO   ] NOT OK[200] Responses: 0 
```






