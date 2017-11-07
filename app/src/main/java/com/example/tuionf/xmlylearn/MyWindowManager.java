package com.example.tuionf.xmlylearn;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tuionf on 2016/9/6.
 */
public class MyWindowManager {

    /**
     * 小悬浮窗View的实例
     */
    private static FloatWindowSmallView smallWindow;

    /**
     * 小悬浮窗View的参数
     */
    private static LayoutParams smallWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 用于获取手机可用内存
     */
    private static ActivityManager mActivityManager;

    private static XmPlayerManager playerManager;

    private static final String TAG = "MyWindowManager";

    public static void createSmallWindow(Context context, Intent intent) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        Log.d(TAG, "createSmallWindow: "+screenHeight+"  "+screenWidth);

        if (smallWindow == null){
            smallWindow = new FloatWindowSmallView(context,intent);
            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                //TODO hhp 申请权限在 小米 乐视  魅族上面
                smallWindowParams.type = LayoutParams.TYPE_TOAST;
//                    smallWindowParams.type = LayoutParams.FIRST_SYSTEM_WINDOW;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                //FLAG_NOT_TOUCH_MODAL 允许悬浮窗之外的事件传递
                //FLAG_NOT_FOCUSABLE 不获取焦点，也就是手机点击返回 这样的事件 该悬浮窗接受不到
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                //用于确定悬浮窗的对齐方式，一般设为左上角对齐，这样当拖动悬浮窗的时候方便计算坐标。
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                //width值用于指定悬浮窗的宽度
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
               // x值用于确定悬浮窗的位置，如果要横向移动悬浮窗，就需要改变这个值。
                smallWindowParams.x = screenWidth;
                //y值用于确定悬浮窗的位置，如果要纵向移动悬浮窗，就需要改变这个值。
                smallWindowParams.y = screenHeight / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow,smallWindowParams);
        }

        MyWindowManager.updatePlayer(context,intent);
    }

    public static void removeSmallWindow(Context context) {
        if (smallWindow != null){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    public static void setPlayer(XmPlayerManager xmPlayerManager){
        playerManager = xmPlayerManager;
    }
    /**
     * @param context
     * @param intent
     */
    public static void updatePlayer(Context context, Intent intent) {

        ArrayList<Track> trackList = intent.getParcelableArrayListExtra("trackList");
        int pos = intent.getIntExtra("pos",0);
        Album album = intent.getParcelableExtra("album");

        if (smallWindow != null ) {
            TextView trackTitle = (TextView) smallWindow.findViewById(R.id.message);
            ImageButton play = (ImageButton) smallWindow.findViewById(R.id.play_or_pause);
            ImageView sound_cover = (ImageView) smallWindow.findViewById(R.id.sound_cover);
            final LinearLayout player_ll = (LinearLayout) smallWindow.findViewById(R.id.player_ll);

            trackTitle.setText(album.getAlbumTitle()+"——"+NewsUtils.formatTrackUpdateTime(trackList.get(pos).getUpdatedAt())+"期");
            Picasso.with(context).load(album.getCoverUrlSmall()).into(sound_cover);

            sound_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (View.VISIBLE == player_ll.getVisibility()) {
                        player_ll.setVisibility(View.GONE);
                    }else {
                        player_ll.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        if (playerManager != null){
            playerManager.playList(trackList,pos);
        }
    }


    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 当前可用内存。
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        Log.d(TAG, "getAvailableMemory: "+mi.availMem);
        return mi.availMem;
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null ;
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
        // 系统内存信息文件
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            Log.d(TAG, "getUsedPercentValue: memoryLine.indexOf"+memoryLine.indexOf("MemTotal:"));
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));

            //TODO
            String[] arrayOfString = memoryLine.split("\\s+");
            long initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;

            Log.d(TAG, "getUsedPercentValue2: initial_memory"+initial_memory);
            Log.d(TAG, "getUsedPercentValue2:totalMemorySize "+totalMemorySize);
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }
}
