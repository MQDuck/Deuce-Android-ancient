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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.mqduck.deuce.common.DeuceListenerService;
import net.mqduck.deuce.common.DeuceModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity
{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    //private TextView mClockView;

    private BroadcastReceiver dataUpdateReceiver = null;
    private DeuceModel model = null;
    private Button buttonPlayer1, buttonPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout)findViewById(R.id.container);
        mTextView = (TextView)findViewById(R.id.text);
        //mClockView = (TextView)findViewById(R.id.clock);

        buttonPlayer1 = (Button)findViewById(R.id.buttonPlayer1);
        buttonPlayer2 = (Button)findViewById(R.id.buttonPlayer2);

        model = new DeuceModel(this, savedInstanceState);
        DeuceListenerService.setModel(model);


        buttonPlayer1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                model.setPointsTeam1(model.getPointsTeam1() + 1);
                updateState();
            }
        });
        buttonPlayer2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                model.setPointsTeam2(model.getPointsTeam2() + 1);
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
    private void updateView()
    {
        buttonPlayer1.setText(model.getPointsStrTeam1());
        buttonPlayer2.setText(model.getPointsStrTeam2());
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
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

    @Override
    public void onEnterAmbient(final Bundle ambientDetails)
    {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient()
    {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient()
    {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay()
    {
        if(isAmbient())
        {
            mContainerView.setBackgroundColor(getColor(android.R.color.black));
            mTextView.setTextColor(getColor(android.R.color.white));
            //mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        }
        else
        {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getColor(android.R.color.black));
            //mClockView.setVisibility(View.GONE);
        }
    }
}
