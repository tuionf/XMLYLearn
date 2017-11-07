package com.example.tuionf.xmlylearn;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by tuionf on 2016/9/6.
 */

public class FloatWindowSmallView extends LinearLayout {

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    //播放器
    private XmPlayerManager mPlayerManager;
    private ArrayList<Track> trackList;
    private int pos = 0;

    private static final String TAG = "FloatWindowSmallView";

    public FloatWindowSmallView(final Context context, final Intent intent) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.activity_float_player, this);
        View view = findViewById(R.id.ll);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        TextView trackTitle = (TextView) findViewById(R.id.message);
        ImageButton play = (ImageButton) findViewById(R.id.play_or_pause);
        ImageView sound_cover = (ImageView) findViewById(R.id.sound_cover);

        trackList = intent.getParcelableArrayListExtra("trackList");
        pos = intent.getIntExtra("pos",0);
        Album album = intent.getParcelableExtra("album");

        XmPlayerConfig.getInstance(context).setUseSystemLockScreen(true);
        mPlayerManager = XmPlayerManager.getInstance(context);
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);

                //设置播放器模式
                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                Toast.makeText(context, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

        trackTitle.setText(album.getAlbumTitle()+"—"+NewsUtils.formatTrackUpdateTime(trackList.get(pos).getUpdatedAt())+"期");
        Picasso.with(context).load(album.getCoverUrlSmall()).into(sound_cover);

        MyWindowManager.setPlayer(mPlayerManager);

        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
                playTrackList();
            }
        });


    }

    public void playTrackList() {
        if (mPlayerManager.isPlaying()) {
            mPlayerManager.pause();
        } else {
            mPlayerManager.playList(trackList,pos);
        }
    }



    //手指触摸移动


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();

                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();

                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            //TODO
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
//        private void openBigWindow() {
//            MyWindowManager.createBigWindow(getContext());
//            MyWindowManager.removeSmallWindow(getContext());
//        }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            Log.e(TAG, "onPlayStart: " );
        }

        @Override
        public void onPlayPause() {
            Log.e(TAG, "onPlayPause: " );
        }

        @Override
        public void onPlayStop() {
            Log.e(TAG, "onPlayStop: " );

        }

        @Override
        public void onSoundPlayComplete() {

        }

        @Override
        public void onSoundPrepared() {

        }

        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingStop() {

        }

        @Override
        public void onBufferProgress(int i) {

        }

        @Override
        public void onPlayProgress(int i, int i1) {

        }

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }
    };
}