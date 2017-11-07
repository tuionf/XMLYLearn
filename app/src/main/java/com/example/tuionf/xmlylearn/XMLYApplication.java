package com.example.tuionf.xmlylearn;

import android.app.Application;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

/**
 * @author tuionf
 * @date 2017/10/24
 * @email 596019286@qq.com
 * @explain
 */

public class XMLYApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        String mAppSecret;
        if(!DTransferConstants.isRelease) {
            mAppSecret = "c1c9c9cc4b2d3e2982ce09513dad8ac8";
            mXimalaya.setAppkey("7721f10cb16aeb985303615f8e9f4aa5");
            mXimalaya.setPackid("com.app.joke.android");
        } else {
            mAppSecret = "4d8e605fa7ed546c4bcb33dee1381179";
            mXimalaya.setAppkey("b617866c20482d133d5de66fceb37da3");
//            mAppSecret = "4d8e605fa7ed546c4bcb33dee1381179";
//            mXimalaya.setAppkey("b617866c20482d133d5de66fceb37da3");
            mXimalaya.setPackid("com.app.test.android");
        }
        mXimalaya.init(this ,mAppSecret);
    }
}
