package com.example.tuionf.xmlylearn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsAlbumActivity extends AppCompatActivity {

    private ListView albumLv;
    private List<Album> albumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        albumLv = (ListView) findViewById(R.id.album_lv);

        getAlbums();
    }

    private void getAlbums() {
        String album_ids = "269179,3395197,254106,5258660";
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_IDS, album_ids);

        CommonRequest.getBatch(map, new IDataCallBack<BatchAlbumList>() {
            @Override
            public void onSuccess(@Nullable BatchAlbumList batchAlbumList) {
                albumList = batchAlbumList.getAlbums();
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }


}
