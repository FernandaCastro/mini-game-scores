package org.minigame.score;

import org.junit.jupiter.api.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScoreServiceIntegrationTest {

    ScoreRepository scoreRepository;

    ScoreService scoreService;

    ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> levelScores;

    @BeforeAll
    public void init(){
        levelScores = new ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>>();
        scoreRepository = new ScoreRepository(levelScores);
        scoreService = new ScoreService(scoreRepository);
    }

    @Test
    @Order(1)
    public void givenScore_whenSave_shouldStoreScore(){
        //given
        var givenScore = new Score(1, 100, 1);

        //when
        scoreService.save(givenScore);

        //then
        var scores = levelScores.get(1);
        Assertions.assertNotNull(scores);

        var savedScore = scores.pollFirst();
        assertEquals(givenScore.getLevelId(), savedScore.getLevelId());
        assertEquals(givenScore.getUserId(), savedScore.getUserId());
        assertEquals(givenScore.getScore(), savedScore.getScore());
    }

    @Test
    public void givenLargeScoreList_WhenGetHighestScores_shouldReturn15HighestScores(){
        //given
        scoreRepository.save(new Score(1, 100, 1));
        scoreRepository.save(new Score(1, 200, 2));
        scoreRepository.save(new Score(1, 300, 3));
        scoreRepository.save(new Score(1, 400, 4));
        scoreRepository.save(new Score(1, 500, 5));
        scoreRepository.save(new Score(1, 600, 6));
        scoreRepository.save(new Score(1, 700, 7));
        scoreRepository.save(new Score(1, 800, 8));
        scoreRepository.save(new Score(1, 900, 9));
        scoreRepository.save(new Score(1, 1000, 10));
        scoreRepository.save(new Score(1, 1100, 11));
        scoreRepository.save(new Score(1, 1200, 12));
        scoreRepository.save(new Score(1, 1300, 13));
        scoreRepository.save(new Score(1, 1400, 14));
        scoreRepository.save(new Score(1, 1500, 15));
        scoreRepository.save(new Score(1, 100, 16));

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "15=1500,14=1400,13=1300,12=1200,11=1100,10=1000,9=900,8=800,7=700,6=600,5=500,4=400,3=300,2=200,1=100";

        assertEquals(expectedResult, result);
    }

    @Test
    public void givenDuplicateUserIdScoreList_whenGetHighestScores_shouldReturnDistinctStringSorted(){
        //given
        scoreRepository.save(new Score(2, 500, 1));
        scoreRepository.save(new Score(2, 1500, 1));
        scoreRepository.save(new Score(2, 1000, 1));

        //when
        String result = scoreService.getHighestScores(2);

        //then
        String expectedResult = "1=1000";
        assertEquals(expectedResult, result);
    }
}
