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
    public void givenEmptyScoreList_whenGetHighestScores_shouldReturnEmptyString(){
        //given
        when(scoreRepository.getHighestScores(1)).thenReturn(null);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "";
        assertEquals(expectedResult, result);
    }

    @Test
    public void givenSmallScoreList_whenGetHighestScores_shouldReturnStringSorted(){
        //given
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        scores.add(new Score(1, 500, 1));
        scores.add(new Score(1, 2500, 2));
        scores.add(new Score(1, 1000, 3));

        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "2=2500,3=1000,1=500";

        assertEquals(expectedResult, result);
    }

    @Test
    public void givenEqualScores_whenGetHighestScores_shouldReturnHighestEqualScores(){
        //given
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        scores.add(new Score(1, 100, 1));
        scores.add(new Score(1, 100, 2));
        scores.add(new Score(1, 200, 3));
        scores.add(new Score(1, 200, 4));
        scores.add(new Score(1, 300, 5));


        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "5=300,3=200,4=200,1=100,2=100";

        assertEquals(expectedResult, result);
    }


    @Test
    public void givenBigScoreList_whenGetHighestScores_shouldReturn15HighestScores(){
        //given 16 scores
        ConcurrentSkipListSet<Score> scores = new ConcurrentSkipListSet<>();
        scores.add(new Score(1, 100, 1));
        scores.add(new Score(1, 200, 2));
        scores.add(new Score(1, 300, 3));
        scores.add(new Score(1, 400, 4));
        scores.add(new Score(1, 500, 5));
        scores.add(new Score(1, 600, 6));
        scores.add(new Score(1, 700, 7));
        scores.add(new Score(1, 800, 8));
        scores.add(new Score(1, 900, 9));
        scores.add(new Score(1, 1000, 10));
        scores.add(new Score(1, 1100, 11));
        scores.add(new Score(1, 1200, 12));
        scores.add(new Score(1, 1300, 13));
        scores.add(new Score(1, 1400, 14));
        scores.add(new Score(1, 1500, 15));
        scores.add(new Score(1, 100, 16));

        var rankedScores = scores.headSet(scores.last(), true);
        when(scoreRepository.getHighestScores(1)).thenReturn(rankedScores);

        //when
        String result = scoreService.getHighestScores(1);

        //then
        String expectedResult = "15=1500,14=1400,13=1300,12=1200,11=1100,10=1000,9=900,8=800,7=700,6=600,5=500,4=400,3=300,2=200,1=100";

        assertEquals(expectedResult, result);
    }




}
