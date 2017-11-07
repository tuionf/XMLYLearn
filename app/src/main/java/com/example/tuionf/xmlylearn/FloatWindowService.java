package com.example.tuionf.xmlylearn;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;

/**
* 1. 创建悬浮窗
* 2. 桌面的时候创建，不是桌面取消
*/
public class FloatWindowService extends Service {
    private static final String TAG = "FloatWindowService";

    //定时器 定时检测当前是桌面还是不是桌面
    private Timer timer;

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
//        timer = null;
        if (MyWindowManager.isWindowShowing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.removeSmallWindow(getApplicationContext());
                }
            });
        }
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (!MyWindowManager.isWindowShowing()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.createSmallWindow(getApplicationContext(),intent);
                }
            });

        }else if (MyWindowManager.isWindowShowing()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.updatePlayer(getApplicationContext(),intent);
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

//    class RefreshTimerTask extends TimerTask{
//        private static final String TAG = "RefreshTimerTask";
//        @Override
//        public void run() {
//            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗
//            //所以需要写一个判断是不是桌面的方法 isHome()
//            if (!isHome() && !MyWindowManager.isWindowShowing()){
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: ");
//                        MyWindowManager.createSmallWindow(getApplicationContext());
//                    }
//                });
//
//            }else if(isHome() && MyWindowManager.isWindowShowing()){
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyWindowManager.removeSmallWindow(getApplicationContext());
//                    }
//                });
//            }else if(!isHome() && MyWindowManager.isWindowShowing()){
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: updateUsedPercent");
//                        MyWindowManager.updateUsedPercent(getApplicationContext());
//                    }
//                });
//            }
//        }
//    }
//
//    private boolean isHome(){
//        Log.d(TAG, "isHome: ");
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> rti = am.getRunningTasks(1);
//        return  getHomes().contains(rti.get(0).topActivity.getPackageName());
//    }
//
//    /**
//     * 获得属于桌面的应用的应用包名称
//     * @return 返回包含所有包名的字符串列表
//     */
//    private List<String> getHomes() {
//        Log.d(TAG, "getHomes: ");
//        List<String> names = new ArrayList<String>();
//        PackageManager packageManager = this.getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        //queryIntentActivities 用来匹配符合intent的activity并返回
//        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
//        for (ResolveInfo ri:resolveInfos
//             ) {
//            names.add(ri.activityInfo.packageName);
//            Log.d(TAG, "getHomes: "+ri.activityInfo.packageName);
//        }
//
//        return names;
//    }

}
