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

package net.mqduck.deuce.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import net.mqduck.deuce.common.DeuceListenerService;
import net.mqduck.deuce.common.DeuceModel;

import net.mqduck.deuce.app.R;

public class MainActivity extends Activity
{
    private BroadcastReceiver dataUpdateReceiver = null;
    private DeuceModel model = null;
    private Button buttonPlayer1, buttonPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlayer1 = (Button)findViewById(R.id.buttonPlayer1);
        buttonPlayer2 = (Button)findViewById(R.id.buttonPlayer2);

        model = new DeuceModel(this, savedInstanceState);
        DeuceListenerService.setModel(model);

        buttonPlayer1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                model.setScorePlayer1(model.getScorePlayer1() + 1);
                updateState();
            }
        });
        buttonPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                model.setScorePlayer2(model.getScorePlayer2() + 1);
                updateState();
            }
        });

        updateView();
    }

    private void updateState()
    {
        updateView();
        model.updateState();
    }

    @SuppressLint("SetTextI18n")
    protected void updateView()
    {
        buttonPlayer1.setText(Integer.toString(model.getScorePlayer1()));
        buttonPlayer2.setText(Integer.toString(model.getScorePlayer2()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        model.save(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume()
    {
        if(dataUpdateReceiver == null)
            dataUpdateReceiver = new BroadcastReceiver() {
                @Override public void onReceive(final Context context, final Intent intent)
                {
                    if(intent.getAction().equals(DeuceListenerService.TAG_BROADCAST))
                        updateView();
                }
            };

        IntentFilter intentFilter = new IntentFilter(DeuceListenerService.TAG_BROADCAST);
        registerReceiver(dataUpdateReceiver, intentFilter);

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        if(dataUpdateReceiver != null)
            unregisterReceiver(dataUpdateReceiver);

        super.onPause();
    }
}
