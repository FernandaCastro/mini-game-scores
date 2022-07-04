package org.minigame.score;

import org.minigame.configuration.Service;

import java.util.stream.Collectors;

public class ScoreService implements Service {

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

        //TODO: find better alternative to limit() and toSet()/distinct() due to low performance
        var filtered = scores.parallelStream()
                .limit(15)
                .collect(Collectors.toSet());

        return filtered.parallelStream()
                .sorted()
                .map(s -> s.getUserId() + "="+ s.getScore())
                .collect(Collectors.joining(","));

//        var iterator = scores.iterator();
//        int count = 0;
//        StringBuilder builder = new StringBuilder();
//        List<Integer> userIds = new ArrayList<>();
//        while(iterator.hasNext() && count < 15){
//            var score = iterator.next();
//            if(userIds.indexOf(score.getUserId()) == -1){
//                builder.append(score.getUserId()+"="+ score.getScore()+",");
//                userIds.add(score.getUserId());
//                count ++;
//            }
//        }
//        return builder.toString().substring(0, builder.length()-1);
    }
}
