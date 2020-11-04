package com.mopub.actiivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.union_test.toutiao.R;

import java.util.Set;

/**
 * created by wuzejian on 2019/11/29
 */
@SuppressLint("LongLogTag")
public class MopubRewardedVideoActivity extends Activity implements MoPubRewardedVideoListener {
    private static final String TAG= "PangolinAdapter";
    @Nullable
    private Button mShowButton;
    @Nullable
    private String mAdUnitId = "2960ef5338b34e0aa92358c768a9c1e5";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_reward_activity);
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mAdUnitId)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .build();

        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());
        MoPubRewardedVideos.setRewardedVideoListener(this);
        MoPubRewardedVideoManager.updateActivity(this);// must updateActivity
        findViewById(R.id.loadRewardAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoPubRewardedVideos.loadRewardedVideo(mAdUnitId);
            }
        });

        mShowButton = findViewById(R.id.showRewardAd);
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoPubRewardedVideos.showRewardedVideo(mAdUnitId);
            }
        });



    }
    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {

                Log.d("PangolinAdapter","onInitializationFinished////");
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        Log.d(TAG,"onRewardedVideoLoadSuccess.....adUnitId="+adUnitId);
        if (adUnitId.equals(mAdUnitId)) {
            if (mShowButton != null) {
                mShowButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        Log.d(TAG,"onRewardedVideoLoadFailure....."+moPubErrorCode);

    }

    @Override
    public void onRewardedVideoStarted(@NonNull String s) {
        mShowButton.setEnabled(false);
        Log.d(TAG,"onRewardedVideoStarted.....");

    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        Log.d(TAG,"onRewardedVideoPlaybackError.....");

    }

    @Override
    public void onRewardedVideoClicked(@NonNull String s) {
        Log.d(TAG,"onRewardedVideoClicked.....");

    }

    @Override
    public void onRewardedVideoClosed(@NonNull String s) {
        Log.d(TAG,"onRewardedVideoClosed.....");

    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> set, @NonNull MoPubReward moPubReward) {
        Log.d(TAG,"onRewardedVideoLoadFailure.....");

    }

    @Override
    public void onDestroy() {
        MoPubRewardedVideos.setRewardedVideoListener(null);
        super.onDestroy();
    }
}
