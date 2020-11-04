package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import static com.bytedance.sdk.openadsdk.AdSlot.Builder;

/**
 * 插屏广告Activity示例
 */
public class InteractionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "InteractionActivity";

    private TTAdNative mTTAdNative;
    private Button mShow_InterstitialAd_btn;
    private Button mShow_InterstitialAd_btn_ladingpage;
    private Context mContext;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        mContext = this.getBaseContext();
        //step2:创建TTAdNative对象,用于调用广告请求接口，createAdNative(Context context) 插屏广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        mShow_InterstitialAd_btn = (Button) findViewById(R.id.show_ad_dialog);
        mShow_InterstitialAd_btn_ladingpage = (Button) findViewById(R.id.show_ad_dialog_landingpage);
        mShow_InterstitialAd_btn.setOnClickListener(this);
        mShow_InterstitialAd_btn_ladingpage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show_ad_dialog_landingpage) {
            loadInteractionAd("901121725");
        } else if (v.getId() == R.id.show_ad_dialog) {
            loadInteractionAd("901121417");
        }
    }

    /**
     * 加载插屏广告
     */
    private void loadInteractionAd(String codeId) {
        //step4:创建插屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 600) //根据广告平台选择的尺寸，传入同比例尺寸
                .build();
        //step5:请求广告，调用插屏广告异步请求接口
        mTTAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(getApplicationContext(), "code: " + code + "  message: " + message);
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                TToast.show(getApplicationContext(), "type:  " + ttInteractionAd.getInteractionType());
                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "被点击");
                        TToast.show(mContext, "广告被点击");
                    }

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "被展示");
                        TToast.show(mContext, "广告被展示");
                    }

                    @Override
                    public void onAdDismiss() {
                        Log.d(TAG, "插屏广告消失");
                        TToast.show(mContext, "广告消失");
                    }
                });
                //如果是下载类型的广告，可以注册下载状态回调监听
                if (ttInteractionAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ttInteractionAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {
                            Log.d(TAG, "点击开始下载");
                            TToast.show(mContext, "点击开始下载");
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载中");
                            TToast.show(mContext, "下载中");
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载暂停");
                            TToast.show(mContext, "下载暂停");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载失败");
                            TToast.show(mContext, "下载失败");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            Log.d(TAG, "下载完成");
                            TToast.show(mContext, "下载完成");
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            Log.d(TAG, "安装完成");
                            TToast.show(mContext, "安装完成");
                        }
                    });
                }
                ttInteractionAd.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        //TToast.show(mContext, "反馈了 " + value);
                        TToast.show(mContext,"\t\t\t\t\t\t\t感谢您的反馈!\t\t\t\t\t\t\n我们将为您带来更优质的广告体验",3);
                    }

                    @Override
                    public void onCancel() {
                        TToast.show(mContext, "点击取消 ");
                    }

                    @Override
                    public void onRefuse() {
                        TToast.show(mContext,"您已成功提交反馈，请勿重复提交哦！",3);
                    }
                });
                //弹出插屏广告
                ttInteractionAd.showInteractionAd(InteractionActivity.this);
            }
        });
    }
}
