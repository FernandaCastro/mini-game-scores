package org.minigame.score;

import org.minigame.configuration.Repository;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ScoreRepository implements Repository {

    private final ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> levelScores = new ConcurrentHashMap<>();

    public void save(Score score){
        var scores = levelScores.get(score.getLevelId());
        if (scores == null){
            scores = new ConcurrentSkipListSet<Score>();
        }
        scores.add(score);
        levelScores.put(score.getLevelId(), scores);
    }

    public NavigableSet<Score> getHighestScores(int levelId){
        var scores = levelScores.get(levelId);
        if (scores != null) {
            return scores.headSet(scores.last(), true);
        }
        return null;
    }

    //TODO: Purge

}
