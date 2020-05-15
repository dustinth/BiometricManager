package com.xiao.biometricmanagerlib;

import android.app.Application;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.xiao.biometricmanagerlib.dialog.BaseFingerDialog;

/**
 * FingerManager的建造者类
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerManagerBuilder {

    private Application mApplication;

    //弹窗标题
    private String mTitle;

    //弹窗描述
    private String mDes;

    //取消按钮话术
    private String mNegativeText;

    //Android P 以下版本的指纹识别弹窗（如需自定义样式就设置）
    private BaseFingerDialog mFingerDialogApi23;

    //指纹识别回调
    private IFingerCallback mFingerCallback;

    //指纹库发生变化时的回调
    private FingerChangeCallback mFingerChangeCallback;

    public FingerManagerBuilder setApplication(Application application) {
        mApplication = application;
        return this;
    }

    public Application getApplication() {
        return mApplication;
    }

    public FingerManagerBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public FingerManagerBuilder setDes(String des) {
        this.mDes = des;
        return this;
    }

    public String getDes() {
        return mDes;
    }

    public FingerManagerBuilder setNegativeText(String negativeText) {
        this.mNegativeText = negativeText;
        return this;
    }

    public String getNegativeText() {
        return mNegativeText;
    }

    public FingerManagerBuilder setFingerDialogApi23(@Nullable BaseFingerDialog fingerDialogApi23) {
        this.mFingerDialogApi23 = fingerDialogApi23;
        return this;
    }

    public BaseFingerDialog getFingerDialogApi23() {
        return mFingerDialogApi23;
    }

    public FingerManagerBuilder setFingerCallback(IFingerCallback fingerCallback) {
        this.mFingerCallback = fingerCallback;
        return this;
    }

    public IFingerCallback getFingerCallback() {
        return mFingerCallback;
    }

    public FingerManagerBuilder setFingerChangeCallback(FingerChangeCallback fingerChangeCallback) {
        this.mFingerChangeCallback = fingerChangeCallback;
        return this;
    }

    public FingerChangeCallback getFingerChangeCallback() {
        return mFingerChangeCallback;
    }


    public FingerManager create() {
        if (mFingerCallback == null) {
            throw new RuntimeException("CompatFingerManager : FingerCheckCallback can not be null");
        }

        return FingerManager.getInstance(this);
    }

}
