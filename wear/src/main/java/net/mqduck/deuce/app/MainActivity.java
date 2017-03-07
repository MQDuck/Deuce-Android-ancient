package net.mqduck.deuce.app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import net.mqduck.deuce.common.DeuceListenerService;
import net.mqduck.deuce.common.DeuceModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity
{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final String KEY_SCORES = "player_scores";
    private static final String WEARABLE_PATH = "/score";

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    //private TextView mClockView;

    private BroadcastReceiver dataUpdateReceiver = null;
    private DeuceModel model = null;
    private GoogleApiClient apiClient = null;
    private Button buttonPlayer1, buttonPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();

        mContainerView = (BoxInsetLayout)findViewById(R.id.container);
        mTextView = (TextView)findViewById(R.id.text);
        //mClockView = (TextView)findViewById(R.id.clock);

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
        updateView();

        if(apiClient.isConnected())
        {
            PutDataMapRequest requestMap = PutDataMapRequest.create(WEARABLE_PATH);
            requestMap.getDataMap().putIntegerArrayList(KEY_SCORES, model.toIntegerArrayList());
            PutDataRequest request = requestMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(apiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override public void onResult(@NonNull DataApi.DataItemResult dataItemResult)
                        {
                            if (!dataItemResult.getStatus().isSuccess())
                            {
                            }
                            else
                            {
                            }
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateView()
    {
        buttonPlayer1.setText(Integer.toString(model.getScorePlayer1()));
        buttonPlayer2.setText(Integer.toString(model.getScorePlayer2()));
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        int[] scores = { model.getScorePlayer1(), model.getScorePlayer2(), model.getScorePlayer3(),
                model.getScorePlayer4() };
        outState.putIntArray(KEY_SCORES, scores);

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
