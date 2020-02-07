package com.zxing.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ftet.qrcode.R;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public abstract class CaptureActivity extends Activity implements QRCodeView.Delegate {

    private ZXingView mZBarView;

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);
        mZBarView = findViewById(R.id.zxingview);
        mZBarView.setDelegate(this);

        findViewById(R.id.capture_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.selectPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPicSelectClick();
            }
        });
    }

    protected abstract void getQRResult(String result);

    protected abstract void onPicSelectClick();

    public String scanningImage(String path) {
        mZBarView.decodeQRCode(path);
        return null;
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.v("aaa", "onScanQRCodeSuccess   " + result);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        if (result != null) {
            getQRResult(result);
        } else {
            mZBarView.startSpot();
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        Log.v("aaa", "onCameraAmbientBrightnessChanged");
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.v("aaa", "onScanQRCodeOpenCameraError");
    }


    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        mZBarView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }
}
