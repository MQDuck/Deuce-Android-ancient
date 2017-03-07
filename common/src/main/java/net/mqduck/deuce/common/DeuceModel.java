package net.mqduck.deuce.common;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mqduck on 3/2/17.
 */

public class DeuceModel
{
    private int scorePlayer1, scorePlayer2, scorePlayer3, scorePlayer4;

    public DeuceModel(final int scorePlayer1, final int scorePlayer2, final int scorePlayer3,
                      final int scorePlayer4)
    {
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.scorePlayer3 = scorePlayer3;
        this.scorePlayer4 = scorePlayer4;
    }

    public DeuceModel(final int scorePlayer1, final int scorePlayer2)
    {
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        scorePlayer3 = scorePlayer4 = 0;
    }

    public DeuceModel()
    {
        scorePlayer1 = scorePlayer2 = scorePlayer3 = scorePlayer4 = 0;
    }

    public int[] toIntArray()
    {
        return new int[]{ scorePlayer1, scorePlayer2, scorePlayer3, scorePlayer4 };
    }

    public ArrayList<Integer> toIntegerArrayList()
    {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(scorePlayer1);
        list.add(scorePlayer2);
        list.add(scorePlayer3);
        list.add(scorePlayer4);
        return list;
    }

    public boolean setScorePlayer1(final int score)
    {
        if(score < 0)
            return false;
        scorePlayer1 = score;
        return true;
    }
    public int getScorePlayer1() { return scorePlayer1; }
    public boolean setScorePlayer2(final int score)
    {
        if(score < 0)
            return false;
        scorePlayer2 = score;
        return true;
    }
    public int getScorePlayer2() { return scorePlayer2; }
    public boolean setScorePlayer3(final int score)
    {
        if(score < 0)
            return false;
        scorePlayer3 = score;
        return true;
    }
    public int getScorePlayer3() { return scorePlayer3; }
    public boolean setScorePlayer4(final int score)
    {
        if(score < 0)
            return false;
        scorePlayer4 = score;
        return true;
    }
    public int getScorePlayer4() { return scorePlayer4; }
}
