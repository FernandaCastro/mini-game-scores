package org.minigame.score;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ScoreRepository {

    private final ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> levelScores;

    public ScoreRepository(ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> levelScores) {
        this.levelScores = levelScores;
    }

    public void save(Score score){
        var scores = levelScores.get(score.getLevelId());
        if (scores == null){
            scores = new ConcurrentSkipListSet<Score>();
        }
        if(scores.contains(score)) {
            scores.remove(score);
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

    public int purge(){
        var levels = levelScores.keys();
        int purged = 0;
        while(levels.hasMoreElements()) {
            int levelId = levels.nextElement();
            var scores = levelScores.get(levelId);

            int size = scores.size();

            while (scores != null && scores.size()>15) {
                scores.pollLast();
            }
            purged += (size - scores.size());
        }
        return purged;
    }

}
