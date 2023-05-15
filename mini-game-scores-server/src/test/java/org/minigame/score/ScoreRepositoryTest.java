package org.minigame.score;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ScoreRepositoryTest {

    @Spy
    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> levelScores;

    @InjectMocks
    ScoreRepository scoreRepository;

    @Test
    public void givenScores_whenSave_shouldStoreScores(){
        //given  //when
        scoreRepository.save(new Score(1, 500, 1));
        scoreRepository.save(new Score(1, 2500, 2));

        //then
        var storedScores = levelScores.get(1);
        assertEquals(2, storedScores.size());

        var storedScore = storedScores.pollFirst();
        assertEquals(1, storedScore.getLevelId());
        assertEquals(2, storedScore.getUserId());
        assertEquals(2500, storedScore.getScore());

        storedScore = storedScores.pollFirst();
        assertEquals(1, storedScore.getLevelId());
        assertEquals(1, storedScore.getUserId());
        assertEquals(500, storedScore.getScore());
    }

    @Test
    public void givenDuplicateUserIdScores_whenSave_shouldStoreTheLatestScoreOnly(){
        //given
        Score score1 = new Score(1, 500, 1);
        Score score2 = new Score(1, 2500, 1);

        //when
        scoreRepository.save(score1);
        scoreRepository.save(score2);

        //then
        var scoresStored = levelScores.get(1);
        assertEquals(1, scoresStored.size());

        var storedScore = scoresStored.pollFirst();
        assertEquals(1, storedScore.getLevelId());
        assertEquals(1, storedScore.getUserId());
        assertEquals(2500, storedScore.getScore());
    }

    @Test
    public void givenUnsortedScoreList_whenGetHighestScores_shouldReturnReversedSortedList(){
        //given
        var scores = new ConcurrentSkipListSet<Score>();
        scores.add(new Score(1, 500, 1));
        scores.add(new Score(1, 2500, 2));
        scores.add(new Score(1, 1500, 3));
        scores.add(new Score(1, 3000, 4));
        levelScores.put(1, scores);

        //when
        var sortedScores = scoreRepository.getHighestScores(1);

        //then
        Assertions.assertNotNull(sortedScores);
        assertEquals(4, sortedScores.size());
        assertEquals(3000, sortedScores.pollFirst().getScore());
        assertEquals(2500, sortedScores.pollFirst().getScore());
        assertEquals(1500, sortedScores.pollFirst().getScore());
        assertEquals(500, sortedScores.pollFirst().getScore());
    }

    @Test
    public void givenLargeScoreList_whenPurge_ShouldKeepHighestScoresOnly(){
        //given
        var givenScores = new ConcurrentSkipListSet<Score>();
        givenScores.add(new Score(1, 500, 1));
        givenScores.add(new Score(1, 2500, 2));
        givenScores.add(new Score(1, 1500, 3));
        givenScores.add(new Score(1, 3000, 4));
        givenScores.add(new Score(1, 50, 5));
        givenScores.add(new Score(1, 5000, 6));
        givenScores.add(new Score(1, 2000, 7));
        givenScores.add(new Score(1, 3500, 8));
        givenScores.add(new Score(1, 1000, 9));
        givenScores.add(new Score(1, 100, 10));
        givenScores.add(new Score(1, 4500, 11));
        givenScores.add(new Score(1, 4000, 12));
        givenScores.add(new Score(1, 5500, 13));
        givenScores.add(new Score(1, 7000, 14));
        givenScores.add(new Score(1, 6000, 15));
        givenScores.add(new Score(1, 6500, 16));
        levelScores.put(1, givenScores);

        //when
        int purged = scoreRepository.purge();

        //then
        assertEquals(1, purged);

        var storedScores = levelScores.get(1);
        Assertions.assertNotNull(storedScores);
        assertEquals(15, storedScores.size());
        assertEquals(7000, storedScores.pollFirst().getScore());
        assertEquals(6500, storedScores.pollFirst().getScore());
        assertEquals(6000, storedScores.pollFirst().getScore());
        assertEquals(5500, storedScores.pollFirst().getScore());
        assertEquals(5000, storedScores.pollFirst().getScore());
        assertEquals(4500, storedScores.pollFirst().getScore());
        assertEquals(4000, storedScores.pollFirst().getScore());
        assertEquals(3500, storedScores.pollFirst().getScore());
        assertEquals(3000, storedScores.pollFirst().getScore());
        assertEquals(2500, storedScores.pollFirst().getScore());
        assertEquals(2000, storedScores.pollFirst().getScore());
        assertEquals(1500, storedScores.pollFirst().getScore());
        assertEquals(1000, storedScores.pollFirst().getScore());
        assertEquals(500, storedScores.pollFirst().getScore());
        assertEquals(100, storedScores.pollFirst().getScore());
    }

    @Test
    public void givenSmallScoreList_whenPurge_ShouldPurgeNothing(){
        //given
        var givenScores = new ConcurrentSkipListSet<Score>();
        givenScores.add(new Score(1, 500, 1));
        givenScores.add(new Score(1, 2500, 2));
        givenScores.add(new Score(1, 1500, 3));
        levelScores.put(1, givenScores);

        //when
        int purged = scoreRepository.purge();

        //then
        assertEquals(0, purged);

        var storedScores = levelScores.get(1);
        Assertions.assertNotNull(storedScores);
        assertEquals(3, storedScores.size());
    }

    @Test
    public void givenTwoLevelsList_whenPurge_ShouldPurgeLevelTwo(){
        //given
        var givenScores = new ConcurrentSkipListSet<Score>();
        givenScores.add(new Score(1, 500, 1));
        givenScores.add(new Score(1, 2500, 2));
        givenScores.add(new Score(1, 1500, 3));
        levelScores.put(1, givenScores);

        givenScores = new ConcurrentSkipListSet<Score>();
        givenScores.add(new Score(2, 500, 1));
        givenScores.add(new Score(2, 2500, 2));
        givenScores.add(new Score(2, 1500, 3));
        givenScores.add(new Score(2, 3000, 4));
        givenScores.add(new Score(2, 50, 5));
        givenScores.add(new Score(2, 5000, 6));
        givenScores.add(new Score(2, 2000, 7));
        givenScores.add(new Score(2, 3500, 8));
        givenScores.add(new Score(2, 1000, 9));
        givenScores.add(new Score(2, 100, 10));
        givenScores.add(new Score(2, 4500, 11));
        givenScores.add(new Score(2, 4000, 12));
        givenScores.add(new Score(2, 5500, 13));
        givenScores.add(new Score(2, 7000, 14));
        givenScores.add(new Score(2, 6000, 15));
        givenScores.add(new Score(2, 6500, 16));
        levelScores.put(2, givenScores);

        //when
        int purged = scoreRepository.purge();

        //then
        assertEquals(1, purged);

        var storedScores = levelScores.get(1);
        Assertions.assertNotNull(storedScores);
        assertEquals(3, storedScores.size());

        storedScores = levelScores.get(2);
        Assertions.assertNotNull(storedScores);
        assertEquals(15, storedScores.size());
    }
}
