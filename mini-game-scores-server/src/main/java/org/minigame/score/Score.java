package org.minigame.score;

import java.util.Objects;

public class Score implements Comparable<Score>{

    private final int levelId;

    private final int score;

    private final int userId;

    public Score(int levelId, int score, int userId) {
        this.levelId = levelId;
        this.score = score;
        this.userId = userId;
    }

    public int getLevelId() {
        return levelId;
    }

    public int getScore() {
        return score;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score1 = (Score) o;
        return this.getLevelId() == score1.getLevelId() && this.getUserId() == score1.getUserId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLevelId(), getUserId());
    }


    @Override
    public int compareTo(Score o) {
        if (this.getLevelId() == o.getLevelId() && this.getUserId() == o.getUserId()){
            return 0;
        }else if (o.getScore() == this.getScore()) {
            //UserId in naturalOrder
            return Integer.compare(this.getUserId(), o.getUserId());
        }else{
            //Score in reversedOrder
            return Integer.compare(o.getScore(), this.getScore());
        }
    }
}
