package com.itay.hw21;

public class Score {
    private long score;

    public Score(long score) {
        this.score = score;
    }
    public long getScore() {
        return score;
    }


    //firebase constructor
    public Score() {}
    @Override
    public String toString() {
        return "Score{" +
                "score=" + score +
                '}';
    }
}
