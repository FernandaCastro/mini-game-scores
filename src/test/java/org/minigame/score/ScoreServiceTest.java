package org.minigame.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceTest {

    @Mock
    ScoreRepository scoreRepository;

    @InjectMocks
    ScoreService scoreService;

    @Test
    public void givenScoreList_getHighestScores_shouldReturnStringSorted(){
        //given
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        for (int i=1; i<=20; i++) {
            scores.add(new Score(1, (1000+i), i));
        }
        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);


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
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        for (int i=1; i<=20; i++) {
            scores.add(new Score(1, (1000+i), 1));
        }
        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "1=1020";
        assertEquals(expectedResult, result);
    }

    @Test
    public void givenSmallScoreList_getHighestScores_shouldReturnStringSorted(){
        //given
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        for (int i=1; i<=5; i++) {
            scores.add(new Score(1, (1000+i), i));
        }
        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);


        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "";
        for (int i=5; i>=1; i--){
            expectedResult += i + "=" + (1000+i) + ",";
        }
        expectedResult = expectedResult.substring(0, expectedResult.length()-1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void givenEmptyScoreList_getHighestScores_shouldReturnEmptyString(){
        //given
        when(scoreRepository.getHighestScores(1)).thenReturn(null);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "";
        assertEquals(expectedResult, result);
    }


}
