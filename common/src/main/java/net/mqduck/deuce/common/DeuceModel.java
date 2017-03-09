/*
 * Copyright 2017 Jeffrey Thomas Piercy.
 *
 * This file is part of Deuce Android.
 *
 * Deuce Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deuce Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimp Engine.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mqduck.deuce.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mqduck on 3/2/17.
 */

public class DeuceModel
{
    public static final String KEY_SCORES = "player_scores";
    public final SparseArray<String> scoreJargonMap;
  private int pointsTeam1, pointsTeam2, scoreGameTeam1, scoreGameTeam2, scoreSetTeam1, scoreSetTeam2;
    private Context context = null;
    private GoogleApiClient apiClient = null;

    public DeuceModel(final Context context, final Bundle savedInstanceState)
    {
        this.context = context;

        if(savedInstanceState == null)
            pointsTeam1 = pointsTeam2 = scoreGameTeam1 = scoreGameTeam2 = scoreSetTeam1 = scoreSetTeam2 = 0;
        else
        {
            final int[] scores = savedInstanceState.getIntArray(KEY_SCORES);
            if(scores == null)
                pointsTeam1 = pointsTeam2 = scoreGameTeam1 = scoreGameTeam2 = scoreSetTeam1 = scoreSetTeam2 = 0;
            else
            {
                pointsTeam1 = scores[0];
                pointsTeam2 = scores[1];
                scoreGameTeam1 = scores[2];
                scoreGameTeam2 = scores[3];
                scoreSetTeam1 = scores[4];
                scoreSetTeam2 = scores[5];
            }
        }

        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();

        scoreJargonMap = new SparseArray<>();
        scoreJargonMap.put(0, context.getString(R.string.score0));
        scoreJargonMap.put(1, context.getString(R.string.score1));
        scoreJargonMap.put(2, context.getString(R.string.score2));
        scoreJargonMap.put(3, context.getString(R.string.score3));
        scoreJargonMap.put(4, context.getString(R.string.score4));
    }

    public void save(final Bundle outState)
    {
        outState.putIntArray(KEY_SCORES, new int[]{ pointsTeam1, pointsTeam2, scoreGameTeam1, scoreGameTeam2,
                scoreSetTeam1, scoreSetTeam2 });
    }

    public void updateState()
    {
        Log.d("Deuce", "entering updateState()");
        if(apiClient.isConnected())
        {
            Log.d("Deuce", "apiClient.isConnected() is true");
            PutDataMapRequest requestMap =
                    PutDataMapRequest.create("/deuce/update_score");
            ArrayList<Integer> scores = new ArrayList<>();
            scores.add(pointsTeam1);
            scores.add(pointsTeam2);
            requestMap.getDataMap().putIntegerArrayList(KEY_SCORES, scores);
            Wearable.DataApi.putDataItem(apiClient, requestMap.asPutDataRequest())
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override public void onResult(@NonNull DataApi.DataItemResult dataItemResult)
                        {
                            if (!dataItemResult.getStatus().isSuccess())
                            {
                                Log.d("Deuce", "data sync failed");
                            }
                            else
                            {
                                Log.d("Deuce", "data sync succeeded");
                            }
                        }
                    });
        }
    }

    public boolean setPoints(final int pointsTeam1, final int pointsTeam2)
    {
        if(pointsTeam1 < 0 || pointsTeam2 < 0)
            return false;
        this.pointsTeam1 = pointsTeam1;
        this.pointsTeam2 = pointsTeam2;
        return true;
    }

    public boolean setPointsTeam1(final int points)
    {
        if(points < 0)
            return false;
        pointsTeam1 = points;
        return true;
    }

    public int getPointsTeam1() { return pointsTeam1; }

    public String getPointsStrTeam1()
    {
        String str = scoreJargonMap.get(pointsTeam1);
        if(str == null)
            return Integer.toString(pointsTeam1);
        return str;
    }

    public String getPointsStrTeam2()
    {
        String str = scoreJargonMap.get(pointsTeam2);
        if(str == null)
            return Integer.toString(pointsTeam2);
        return str;
    }

    public boolean setPointsTeam2(final int points)
    {
        if(points < 0)
            return false;
        pointsTeam2 = points;
        return true;
    }

    public int getPointsTeam2() { return pointsTeam2; }

    public boolean setScoreGameTeam1(final int score)
    {
        if(score < 0)
            return false;
        scoreGameTeam1 = score;
        return true;
    }

    public int getScoreGameTeam1() { return scoreGameTeam1; }

    public boolean setScoreGameTeam2(final int score)
    {
        if(score < 0)
            return false;
        scoreGameTeam2 = score;
        return true;
    }

    public int getScoreGameTeam2() { return scoreGameTeam2; }

    public boolean setScoreSetTeam1(final int score)
    {
        if(score < 0)
            return false;
        scoreSetTeam1 = score;
        return true;
    }

    public int getScoreSetTeam1() { return scoreSetTeam1; }

    public boolean setScoreSetTeam2(final int score)
    {
        if(score < 0)
            return false;
        scoreSetTeam2 = score;
        return true;
    }

    public int getScoreSetTeam() { return scoreSetTeam2; }
}
