package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

@SuppressWarnings("unused")
public class BannerActivity extends Activity {

    private TTAdNative mTTAdNative;
    private FrameLayout mBannerContainer;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonDownload;
    private Button mButtonLandingPage;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_banner);
        mContext = this.getApplicationContext();
        mBannerContainer = (FrameLayout) findViewById(R.id.banner_container);
        mButtonDownload = (Button) findViewById(R.id.btn_banner_download);
        mButtonLandingPage = (Button) findViewById(R.id.btn_banner_landingpage);
        mButtonDownload.setOnClickListener(mClickListener);
        mButtonLandingPage.setOnClickListener(mClickListener);
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadBannerAd("901121895");
            } else if (v.getId() == R.id.btn_banner_landingpage) {
                loadBannerAd("901121987");
            }

        }
    };

    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                TToast.show(BannerActivity.this, "load error : " + code + ", " + message);
                mBannerContainer.removeAllViews();
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        TToast.show(mContext, "广告被点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        TToast.show(mContext, "广告展示");
                    }
                });
                //（可选）设置下载类广告的下载监听
                bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        TToast.show(mContext, "点击 " + value);
                        //用户选择不喜欢原因后，移除广告展示
                        mBannerContainer.removeAllViews();
                    }

                    @Override
                    public void onCancel() {
                        TToast.show(mContext, "点击取消 ");
                    }

                    @Override
                    public void onRefuse() {

                    }
                });

                //获取网盟dislike dialog，您可以在您应用中本身自定义的dislike icon 按钮中设置 mTTAdDislike.showDislikeDialog();
                /*mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            TToast.show(mContext, "点击 " + value);
                        }

                        @Override
                        public void onCancel() {
                            TToast.show(mContext, "点击取消 ");
                        }
                    });
                if (mTTAdDislike != null) {
                    XXX.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTTAdDislike.showDislikeDialog();
                        }
                    });
                } */

            }
        });
    }

    private boolean mHasShowDownloadActive = false;

    private void bindDownloadListener(TTBannerAd ad) {
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(BannerActivity.this, "点击图片开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(BannerActivity.this, "下载中，点击图片暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(BannerActivity.this, "下载暂停，点击图片继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(BannerActivity.this, "下载失败，点击图片重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(BannerActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(BannerActivity.this, "点击图片安装", Toast.LENGTH_LONG);
            }
        });
    }
}
