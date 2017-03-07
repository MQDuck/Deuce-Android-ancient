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
import net.mqduck.deuce.common.R;

public class MainActivity extends Activity
{
    private static final String KEY_SCORES = "player_scores";

    private BroadcastReceiver dataUpdateReceiver = null;
    private DeuceModel model = null;
    private GoogleApiClient apiClient = null;
    private Button buttonPlayer1, buttonPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();

        buttonPlayer1 = (Button)findViewById(R.id.buttonPlayer1);
        buttonPlayer2 = (Button)findViewById(R.id.buttonPlayer2);

        if(savedInstanceState == null)
            model = new DeuceModel();
        else
        {
            final int[] scores = savedInstanceState.getIntArray(KEY_SCORES);
            if(scores == null)
                model = new DeuceModel();
            else
                model = new DeuceModel(scores[0], scores[1], scores[2], scores[3]);
        }
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
        Log.d("Deuce", "entering updateState()");
        updateView();

        if(apiClient.isConnected())
        {
            Log.d("Deuce", "apiClient.isConnected() is true");
            PutDataMapRequest requestMap =
                    PutDataMapRequest.create(getString(R.string.path_update_score));
            requestMap.getDataMap().putIntegerArrayList(KEY_SCORES, model.toIntegerArrayList());
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

    @SuppressLint("SetTextI18n")
    protected void updateView()
    {
        buttonPlayer1.setText(Integer.toString(model.getScorePlayer1()));
        buttonPlayer2.setText(Integer.toString(model.getScorePlayer2()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putIntArray(KEY_SCORES, model.toIntArray());
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
