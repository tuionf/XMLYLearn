package com.example.tuionf.xmlylearn;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsMainActivity extends AppCompatActivity implements View.OnClickListener{

    private XmPlayerManager mPlayerManager;
    private List<Track> tracks;
    private static final String TAG = "NewsMainActivity";
    private TextView mTextView;
    private ImageButton mBtnPreSound;
    private ImageButton mBtnPlay;
    private ImageButton mBtnNextSound;
//    private SeekBar mSeekBar;
    private ImageView mSoundCover;
    private ProgressBar mProgress;
    private Button mBtncreatefloat;
    private Button mBtnMore;
    private ListView mListView;
    private TrackAdapter mTrackAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

//        NBSAppAgent.setLicenseKey("d50f3ef59a0c4b45a223cc571d1d91d1").withLocationServiceEnabled(true).
//                start(this.getApplicationContext());

        initView();
        initPlayer();
        getPlayList();
//        playList();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.message);
        mBtnPreSound = (ImageButton) findViewById(R.id.pre_sound);
        mBtnPlay = (ImageButton) findViewById(R.id.play_or_pause);
        mBtnNextSound = (ImageButton) findViewById(R.id.next_sound);
//        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSoundCover = (ImageView) findViewById(R.id.sound_cover);
        mProgress = (ProgressBar) findViewById(R.id.buffering_progress);
        mBtncreatefloat = (Button) findViewById(R.id.btn_create_float);
        mBtnMore = (Button) findViewById(R.id.btn_more);
        mListView = (ListView) findViewById(R.id.list);

        mBtncreatefloat.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
    }

    private void initPlayer() {
        CommonRequest mXimalaya = CommonRequest.getInstanse();

//        是否播放时在锁屏界面上显示专辑图片等信息
        XmPlayerConfig.getInstance(this).setUseSystemLockScreen(true);
        mPlayerManager = XmPlayerManager.getInstance(this);
        Notification mNotification = XmNotificationCreater.getInstanse(this).initNotification(this.getApplicationContext(), NewsMainActivity.class);
        mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
//		mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);

                //设置播放器模式
                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                Toast.makeText(NewsMainActivity.this, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnPreSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPlayerManager.playPre();
            }
        });

        mBtnNextSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playNext();
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick:--- " );
             if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
            }
        });

        mTrackAdapter = new TrackAdapter();
        mListView.setAdapter(mTrackAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                mPlayerManager.playList(tracks, position);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: "+"担待是哪个  ===  " + mPlayerManager.hasNextSound()  + "      " + mPlayerManager.getPlayList().size() + "    " );
                    }
                } ,2000);
            }
        });
    }

    private void getPlayList() {
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put(DTransferConstants.ALBUM_ID, "3395197");
        map2.put(DTransferConstants.SORT, "asc");
        map2.put(DTransferConstants.PAGE, "2");
//        根据专辑ID获取专辑下的声音列表
//        sort String 否 “asc”表示喜马拉雅正序，”desc”表示喜马拉雅倒序，”time_asc”表示时间升序，”time_desc”表示时间降序，默认为”asc”
//        page	Int	否	当前第几页，不填默认为1
//        count	Int	否	每页多少条，默认20，最多不超过200
        CommonRequest.getTracks(map2, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                Log.e(TAG, "onSuccess: "+"AlbumId"+trackList.getAlbumId()+"---"+trackList.getAlbumTitle()+"idid"+trackList.getCategoryId() );
                tracks = trackList.getTracks();
//                for (int i = 0; i < trackList.getTracks().size(); i++) {
//                    Log.e(TAG, "onSuccess: title"+ trackList.getTracks().get(i).getTrackTitle());
//                    Log.e(TAG, "onSuccess: track_intro"+ trackList.getTracks().get(i).getTrackIntro());
//                    Log.e(TAG, "onSuccess: duration"+ trackList.getTracks().get(i).getDuration());
//                }
                mTrackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        //获取某个分类下的元数据列表
        Map<String, String> map3 = new HashMap<String, String>();
        map3.put(DTransferConstants.CATEGORY_ID, ""+8);
        CommonRequest.getMetadataList(map3, new IDataCallBack<MetaDataList>() {
            @Override
            public void onSuccess(@Nullable MetaDataList metaDataList) {

                for (int i = 0; i < metaDataList.getMetaDatas().size(); i++) {
                    Log.e(TAG, "onSuccess: ---"+metaDataList.getMetaDatas().get(i).getDisplayName());

                    for (int j = 0; j < metaDataList.getMetaDatas().get(i).getAttributes().size(); j++) {
                        Attributes att = metaDataList.getMetaDatas().get(i).getAttributes().get(j);
                        Log.e(TAG, "onSuccess: -Key--"+att.getAttrKey());
                        Log.e(TAG, "onSuccess: -Value--"+att.getAttrValue());
                        Log.e(TAG, "onSuccess: -Name--"+att.getDisplayName());

                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });


        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CALC_DIMENSION, 1 + "");
        map.put(DTransferConstants.CATEGORY_ID, 8+ "");
        map.put(DTransferConstants.METADATA_ATTRIBUTES, "97:穿越");
//        CommonRequest.getMetadataAlbumList(map, new IDataCallBack<AlbumList>() {
//            @Override
//            public void onSuccess(@Nullable AlbumList albumList) {
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//        });

        Map<String, String> map4 = new HashMap<String, String>();
        CommonRequest.getCategories(map4, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
//                for (int i = 0; i < categoryList.getCategories().size(); i++) {
//                    Log.e(TAG, "onSuccess: 分类---"+categoryList.getCategories().get(i).getCategoryName());
//                    Log.e(TAG, "onSuccess: 分类---"+categoryList.getCategories().get(i).getId());
//                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        Map<String, String> map5 = new HashMap<String, String>();
        map5.put(DTransferConstants.CATEGORY_ID, ""+8);
        map5.put(DTransferConstants.TYPE, 0+"");
        CommonRequest.getTags(map5, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(@Nullable TagList tagList) {
//                for (int i = 0; i < tagList.getTagList().size(); i++) {
//                    Log.e(TAG, "onSuccess: tag---"+tagList.getTagList().get(i) );
//                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        Map<String ,String> map6 = new HashMap<String, String>();
        map6.put(DTransferConstants.CATEGORY_ID ,"8");
        map6.put(DTransferConstants.CALC_DIMENSION ,"1");
        map6.put(DTransferConstants.TAG_NAME ,"理财");
        CommonRequest.getAlbumList(map6, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList albumList) {
                for (int i = 0; i < albumList.getAlbums().size(); i++) {
                    Log.e(TAG, "onSuccess:--- "+albumList.getAlbums().get(i).getAlbumTitle() );
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private void playList() {
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                mPlayerManager.playList(tracks,1);
            }
        });
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {
        @Override
        public void onPlayStart() {
            Log.e(TAG, "onPlayStart: " );
//            mBtnPlay.setImageResource(R.mipmap.widget_pause_normal);
        }

        @Override
        public void onPlayPause() {
            Log.e(TAG, "onPlayPause: " );
//            mBtnPlay.setImageResource(R.mipmap.widget_play_normal);
        }

        @Override
        public void onPlayStop() {
            Log.e(TAG, "onPlayStop: " );
            //更换按钮图片资源
//            mBtnPlay.setImageResource(R.mipmap.widget_play_normal);
        }

        @Override
        public void onSoundPlayComplete() {
            Log.e(TAG, "onSoundPlayComplete: " );
//            mBtnPlay.setImageResource(R.mipmap.widget_play_normal);
//            XmPlayerManager.getInstance(mContext).pause();
        }

        @Override
        public void onSoundPrepared() {
            Log.e(TAG, "onSoundPrepared");
//            mSeekBar.setEnabled(true);
//            mProgress.setVisibility(View.GONE);
        }

        //试听结束后回调
        @Override
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
            Log.e(TAG, "onSoundSwitch: " );
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                } else if (model instanceof Schedule) {
                    Schedule program = (Schedule) model;
                    title = program.getRelatedProgram().getProgramName();
                    coverUrl = program.getRelatedProgram().getBackPicUrl();
                } else if (model instanceof Radio) {
                    Radio radio = (Radio) model;
                    title = radio.getRadioName();
                    coverUrl = radio.getCoverUrlLarge();
                }
                mTextView.setText(title);
            }
        }

        //开始缓冲
        @Override
        public void onBufferingStart() {
            Log.e(TAG, "onBufferingStart: " );
//            mSeekBar.setEnabled(false);
//            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBufferingStop() {
            Log.e(TAG, "onBufferingStop: " );
//            mSeekBar.setEnabled(true);
//            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onBufferProgress(int position) {
//            mSeekBar.setSecondaryProgress(position);
            Log.e(TAG, "onBufferProgress: ---"+position );
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            Log.e(TAG, "onPlayProgress: "+duration+"--"+currPos );
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    title = ((Track) info).getTrackTitle();
                } else if (info instanceof Schedule) {
                    title = ((Schedule) info).getRelatedProgram().getProgramName();
                } else if (info instanceof Radio) {
                    title = ((Radio) info).getRadioName();
                }
            }
            mTextView.setText(title + "[" + ToolUtil.formatTime(currPos) + "/" + ToolUtil.formatTime(duration) + "]");
            if (duration != 0) {
//                mSeekBar.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        @Override
        public boolean onError(XmPlayerException e) {
            Log.e(TAG, "onError " + e.getMessage());
            mBtnPlay.setImageResource(R.mipmap.widget_play_normal);
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        XmPlayerManager.release();
        CommonRequest.release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_create_float:
                Intent intent = new Intent(NewsMainActivity.this, FloatWindowService.class);
                startService(intent);
                break;
             case R.id.btn_more:
                 Intent intent1 = new Intent(NewsMainActivity.this, NewsAlbumActivity.class);
                 startActivity(intent1);
                break;
             default:
                break;
        }

    }

    public class TrackAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (tracks == null ) {
                return 0;
            }
            return tracks.size();
        }

        @Override
        public Object getItem(int position) {
            return tracks.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.track_content_item, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.album_img);
//                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Track sound = tracks.get(position);
//            holder.title.setText(sound.getTrackTitle());
//            holder.intro.setText(sound.getAnnouncer() == null ? sound.getTrackTags() : sound.getAnnouncer().getNickname());
//            x.image().bind(holder.cover, sound.getCoverUrlLarge());
            PlayableModel curr = mPlayerManager.getCurrSound();
            if (sound.equals(curr)) {
                holder.content.setBackgroundResource(R.color.selected_bg);
            } else {
                holder.content.setBackgroundColor(Color.WHITE);
            }
            return convertView;
        }
    }
}
