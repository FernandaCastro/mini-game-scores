package org.minigame.score;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScoreServiceIntegrationTest {

    ScoreRepository scoreRepository;

    ScoreService scoreService;

    @BeforeAll
    public void init(){
        scoreRepository = new ScoreRepository();
        scoreService = new ScoreService(scoreRepository);
    }

    @Test
    @Order(1)
    public void givenEmptyScoreList_getHighestScores_shouldReturnEmptyString(){
        //given empty repository

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "";
        assertEquals(expectedResult, result);
    }

    @Test
    public void givenScoreList_getHighestScores_shouldReturnStringSorted(){
        //given
        for (int i=1; i<=20; i++) {
            scoreRepository.save(new Score(1, (1000+i), i));
        }

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "";
        for (int i=20; i>5; i--){
            expectedResult += i + "=" + (1000+i) + ",";
        }
        expectedResult = expectedResult.substring(0, expectedResult.length()-1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void givenDuplicateUserIdScoreList_getHighestScores_shouldReturnDistinctStringSorted(){
        //given
        for (int i=1; i<=20; i++) {
            scoreRepository.save(new Score(2, (1000+i), 1));
        }

        //when
        String result = scoreService.getHighestScores(2);

        //then
        String expectedResult = "1=1020";
        assertEquals(expectedResult, result);
    }

    @Test
    public void givenSmallScoreList_getHighestScores_shouldReturnStringSorted(){
        //given
        for (int i=1; i<=5; i++) {
            scoreRepository.save(new Score(3, (1000+i), i));
        }

        //when
        String result = scoreService.getHighestScores(3);

        //then
        String expectedResult = "";
        for (int i=5; i>=1; i--){
            expectedResult += i + "=" + (1000+i) + ",";
        }
        expectedResult = expectedResult.substring(0, expectedResult.length()-1);

        assertEquals(expectedResult, result);
    }
}
