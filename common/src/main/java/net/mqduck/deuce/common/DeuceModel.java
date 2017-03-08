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
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;

import net.mqduck.deuce.common.R;

/**
 * Created by mqduck on 3/2/17.
 */

public class DeuceModel
{
    public static final String KEY_SCORES = "player_scores";

    private int scorePlayer1, scorePlayer2, scorePlayer3, scorePlayer4;
    private Context context = null;
    private GoogleApiClient apiClient = null;

    public DeuceModel(final Context context, final int scorePlayer1, final int scorePlayer2,
                      final int scorePlayer3, final int scorePlayer4)
    {
        init(context, scorePlayer1, scorePlayer2, scorePlayer3, scorePlayer4);
    }

    public DeuceModel(final Context context, final int scorePlayer1, final int scorePlayer2)
    {
        init(context, scorePlayer1, scorePlayer2, 0, 0);
    }

    public DeuceModel(final Context context)
    {
        init(context, 0, 0, 0, 0);
    }

    private void init(final Context context, final int scorePlayer1, final int scorePlayer2,
                      final int scorePlayer3, final int scorePlayer4)
    {
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.scorePlayer3 = scorePlayer3;
        this.scorePlayer4 = scorePlayer4;
        this.context = context;

        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();
    }

    public void updateState()
    {
        Log.d("Deuce", "entering updateState()");
        if(apiClient.isConnected())
        {
            Log.d("Deuce", "apiClient.isConnected() is true");
            PutDataMapRequest requestMap =
                    PutDataMapRequest.create("/deuce/update_score");
            requestMap.getDataMap().putIntegerArrayList(KEY_SCORES, toIntegerArrayList());
            PutDataRequest request = requestMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(apiClient, request)
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
