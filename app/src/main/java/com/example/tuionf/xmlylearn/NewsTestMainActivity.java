package com.example.tuionf.xmlylearn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsTestMainActivity extends AppCompatActivity {

    private TabLayout newsTablayout;
    private ViewPager newsAlbumViewPager;
    private List<Tag> tabNameList = new ArrayList<>();
    private AlbumFragment albumFragment;
    private static final String TAG = "NewsTestMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_test_main);

        getTabName();
        newsTablayout = (TabLayout) findViewById(R.id.news_tablayout);
        newsAlbumViewPager = (ViewPager) findViewById(R.id.news_album_vp);
        newsTablayout.setupWithViewPager(newsAlbumViewPager);
        newsAlbumViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                albumFragment =
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getTabName() {
        Tag tag0 = new Tag();
        tag0.setTagName("热门");
        tabNameList.add(0,tag0);
        Map<String, String> map5 = new HashMap<String, String>();
        map5.put(DTransferConstants.CATEGORY_ID, ""+8);
        map5.put(DTransferConstants.TYPE, 0+"");
        CommonRequest.getTags(map5, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(@Nullable TagList tagList) {
                tabNameList.addAll(1,tagList.getTagList());
                newsAlbumViewPager.setAdapter(new SlidingPagerAdapter(getSupportFragmentManager()));
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    class SlidingPagerAdapter extends FragmentPagerAdapter {
        public SlidingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            albumFragment = AlbumFragment.newInstance(tabNameList.get(position).getTagName());
            return albumFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNameList.get(position).getTagName();
        }

        @Override
        public int getCount() {
            return tabNameList.size();
        }
    }

}
