package com.example.tuionf.xmlylearn;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import static com.example.tuionf.xmlylearn.R.id.change_order;

public class NewsAlbumTrackActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView album_intro,album_track_list;
    private TextView album_track_name,album_track_announcer;
    private ImageView album_track_logo;
    private Album album = null;
    private TrackFragment trackIntroFragment;
    private TrackFragment trackListFragment;
    private static final String TAG = "NewsAlbumTrackActivity";
    public static final String INTRO = "INTRO";
    public static final String LIST = "LIST";
    private String param = INTRO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_album_track);


        album_intro = (TextView) findViewById(R.id.album_intro);
        album_track_list = (TextView) findViewById(R.id.album_track_list);
        album_track_name = (TextView) findViewById(R.id.album_track_name);
        album_track_announcer = (TextView) findViewById(R.id.album_track_announcer);

        album_track_logo = (ImageView) findViewById(R.id.album_track_logo);


        getIntentData();

        album_intro.setOnClickListener(this);
        album_track_list.setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        trackIntroFragment = TrackFragment.newInstance(INTRO,album);
        trackListFragment = TrackFragment.newInstance(LIST,album);
        transaction.add(R.id.fragment_ll,trackIntroFragment);
        transaction.add(R.id.fragment_ll,trackListFragment);
        transaction.commit();
//        transaction.show(trackIntroFragment);

    }

    private void getIntentData() {
        album =  getIntent().getParcelableExtra("album");
        Log.e(TAG, "getIntentData: "+album.getAlbumIntro() );
        Log.e(TAG, "getIntentData: "+album.getAlbumTitle() );

        album_track_name.setText(album.getAlbumTitle());
        album_track_announcer.setText(album.getAnnouncer().getNickname());
        Picasso.with(this).load(album.getCoverUrlMiddle()).into(album_track_logo);

    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(v.getId()){
            case R.id.album_intro:
                Log.e(TAG, "onClick: intro" );

                if (trackListFragment != null){
                    transaction.hide(trackListFragment);
                }

//                transaction.add(R.id.fragment_ll,trackIntroFragment).commit();
                transaction.show(trackIntroFragment).commit();

                break;
            case R.id.album_track_list:
                Log.e(TAG, "onClick: album_track_list" );

                if (trackListFragment == null){
                    trackListFragment = TrackFragment.newInstance(LIST,album);
                }

                if (trackIntroFragment != null){
                    transaction.hide(trackIntroFragment);
                }

//                transaction.add(R.id.fragment_ll,trackListFragment).commit();
//                transaction.hide(trackIntroFragment);
                transaction.show(trackListFragment).commit();

                break;
            
            case change_order:
                Toast.makeText(this, "--", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }



    }
}
