package com.xiao.biometricmanagerlib.impl;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xiao.biometricmanagerlib.CipherHelper;
import com.xiao.biometricmanagerlib.FingerChangeCallback;
import com.xiao.biometricmanagerlib.FingerManagerBuilder;
import com.xiao.biometricmanagerlib.IBiometricPrompt;
import com.xiao.biometricmanagerlib.IFingerCallback;
import com.xiao.biometricmanagerlib.SharePreferenceUtil;

import javax.crypto.Cipher;

/**
 * Android 9.0及以上的指纹认证实现
 */
@RequiresApi(Build.VERSION_CODES.P)
public class BiometricPromptImpl28 implements IBiometricPrompt {

    private AppCompatActivity mActivity;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCanceled;//用户主动取消指纹识别
    private Cipher cipher;
    private IFingerCallback mFingerCallback;
    private FingerChangeCallback mFingerChangeCallback;
    private BiometricPrompt mBiometricPrompt;

    @RequiresApi(Build.VERSION_CODES.P)
    public BiometricPromptImpl28(AppCompatActivity activity, FingerManagerBuilder fingerManagerController) {
        this.mActivity = activity;
        this.cipher = CipherHelper.getInstance().createCipher();
        this.mFingerCallback = fingerManagerController.getFingerCallback();
        this.mFingerChangeCallback = fingerManagerController.getFingerChangeCallback();
        //Android 9.0及以下显示系统的指纹认证对话框
        this.mBiometricPrompt = new BiometricPrompt
                .Builder(activity)
                .setTitle(fingerManagerController.getTitle())
                .setDescription(fingerManagerController.getDes())
                .setNegativeButton(fingerManagerController.getNegativeText(),
                        activity.getMainExecutor(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mSelfCanceled = true;
                                mFingerCallback.onCancel();
                                mCancellationSignal.cancel();
                            }
                        })
                .build();
    }

    /**
     * 开始指纹认证
     *
     * @param cancel
     */
    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    public void authenticate(@Nullable final CancellationSignal cancel) {
        mSelfCanceled = false;
        mCancellationSignal = cancel;
        //检测指纹库是否发生变化
        if (CipherHelper.getInstance().initCipher(cipher) || SharePreferenceUtil.isFingerDataChange(mActivity)) {
            mFingerChangeCallback.onChange(mActivity);
            return;
        }
        //开始指纹认证
        mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(cipher),
                cancel, mActivity.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show();
                        //指纹认证失败五次会报错，会停留几秒钟后才可以重试
                        cancel.cancel();
                        if (!mSelfCanceled) {
                            mFingerCallback.onError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        mFingerCallback.onHelp(helpString.toString());
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        cancel.cancel();
                        mFingerCallback.onSucceed();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        //指纹不匹配
                        mFingerCallback.onFailed();
                    }
                });
    }

}
