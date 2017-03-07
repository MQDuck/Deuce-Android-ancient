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

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mqduck on 3/2/17.
 */

public class DeuceListenerService extends WearableListenerService
{
    public static final String TAG_BROADCAST = "deuce_broadcast";
    private static final String DATA_PATH = "/score";
    private static final String KEY_SCORES = "player_scores";

    private static DeuceModel model = null;

    public static void setModel(final DeuceModel model) { DeuceListenerService.model = model; }
    public static DeuceModel getModel() { return model; }

    @Override
    public void onDataChanged(final DataEventBuffer dataEvents)
    {
        Log.d("Deuce", "entering onDataChange");

        if(model == null)
            return;
        Log.d("Deuce", "model is not null");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess())
            return;

        for(DataEvent event : events)
        {
            /*Uri uri = event.getDataItem().getUri();
            String nodeId = uri.getHost();
            byte[] payload = uri.toString().getBytes();*/
            if(event.getType() == DataEvent.TYPE_CHANGED)
            {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                ArrayList<Integer> scores = dataMap.getIntegerArrayList(KEY_SCORES);
                if(scores != null)
                {
                    Log.d("Deuce", "updating model");
                    Log.d("Deuce", "old: " + model.getScorePlayer1() + "   new: " + scores.get(0));
                    model.setScorePlayer1(scores.get(0));
                    model.setScorePlayer2(scores.get(1));
                    model.setScorePlayer3(scores.get(2));
                    model.setScorePlayer4(scores.get(3));
                }
            }
        }

        sendBroadcast(new Intent(DeuceListenerService.TAG_BROADCAST));

        super.onDataChanged(dataEvents);

//        if (Log.isLoggable(TAG, Log.DEBUG))
//            Log.d(TAG, "onDataChanged: " + dataEvents);
//        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
//
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//
//        ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
//
//        if (!connectionResult.isSuccess())
//        {
//            Log.e(TAG, "Failed to connect to GoogleApiClient.");
//            return;
//        }
//
//        // Loop through the events and send a message
//        // to the node that created the data item.
//        for (DataEvent event : events)
//        {
//            Uri uri = event.getDataItem().getUri();
//
//            // Get the node id from the host value of the URI
//            String nodeId = uri.getHost();
//            // Set the data of the message to be the bytes of the URI
//            byte[] payload = uri.toString().getBytes();
//
//            // Send the RPC
//            Wearable.MessageApi.sendMessage(googleApiClient, nodeId, DATA_PATH, payload);
//        }
    }
}
