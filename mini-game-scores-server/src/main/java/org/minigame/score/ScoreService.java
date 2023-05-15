package org.minigame.score;

import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public void save(Score score){
        scoreRepository.save(score);
    }

    public String getHighestScores(int levelId){

        var scores = scoreRepository.getHighestScores(levelId);
        if (scores == null) {
            return "";
        }

        return scores.parallelStream()
                .limit(15)
                .map(s -> s.getUserId() + "="+ s.getScore())
                .collect(Collectors.joining(","));
    }

    public int purge(){
        return scoreRepository.purge();
    }
}
