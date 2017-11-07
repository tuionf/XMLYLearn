package com.example.tuionf.xmlylearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link .} interface
 * to handle interaction events.
 * Use the {@link TrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackFragment extends Fragment {

    private static final String ARG_PARAM1 = "tag";
    private static final String ARG_PARAM2 = "album";

    private String mParam1 = NewsAlbumTrackActivity.INTRO;
    private Album mAlbum;
    private ListView mListView;
    private TextView intro;
    private LinearLayout intro_ll;
    private Context mContext;
    private CommonRequest mXimalaya;
    private TrackAdapter mTrackAdapter;
    private TrackList trackList = null;
    private int mPageId = 1;
    private LinearLayout change_order_ll;
    private ImageView change_order;
    private boolean isChangeOrder = false;
    //“asc”表示喜马拉雅正序，”desc”表示喜马拉雅倒序，”time_asc”表示时间升序，”time_desc”表示时间降序，默认为”asc”
    private String order = "asc";
    // 懒加载相关 start
    private boolean isVisible;
    // 标志位，标志已经初始化完成，防止空指针的异常
    private boolean isViewCreated;
    // 懒加载相关 end
    private static final String TAG = "TrackFragment";

    public TrackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tag Parameter 1.
     * @return A new instance of fragment AlbumFragment.
     */

    public static TrackFragment newInstance(String tag, Album album) {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tag);
        args.putParcelable(ARG_PARAM2,album);
        Log.e(TAG, "newInstance: "+tag );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mAlbum = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mListView = (ListView) view.findViewById(R.id.album_listview);
        intro = (TextView) view.findViewById(R.id.intro);
        intro_ll = (LinearLayout) view.findViewById(R.id.intro_ll);
        change_order_ll = (LinearLayout) view.findViewById(R.id.change_order_ll);
        change_order = (ImageView) view.findViewById(R.id.change_order);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        mContext = getActivity();
        mXimalaya = CommonRequest.getInstanse();
        Log.e(TAG, "onActivityCreated: id==thread=="+Thread.currentThread().getId() );
          intro.setText(TextUtils.isEmpty(mAlbum.getAlbumIntro())? "":mAlbum.getAlbumIntro());
//        intro.setText("这是假的简介");
        if (NewsAlbumTrackActivity.LIST.equals(mParam1)){
            intro_ll.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            change_order_ll.setVisibility(View.VISIBLE);
        }else{
            intro_ll.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            change_order_ll.setVisibility(View.GONE);
        }


        mTrackAdapter = new TrackAdapter(mContext,trackList);
        mListView.setAdapter(mTrackAdapter);

        Log.d("hhp", "getUserVisibleHint()--"+getUserVisibleHint());

        if (getUserVisibleHint()) {
            loadListData();
        }

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int count = view.getCount();
                    count = count - 5 > 0 ? count - 5 : count - 1;
                    if (view.getLastVisiblePosition() > count && (trackList == null || mPageId < trackList.getTotalPage())) {
                        loadListData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "---"+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, FloatWindowService.class);

                intent.putParcelableArrayListExtra("trackList", (ArrayList<? extends Parcelable>) trackList.getTracks());

                intent.putExtra("pos",position);
                intent.putExtra("album",mAlbum);
                mContext.startService(intent);
            }
        });

        change_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChangeOrder = true;
                if ("asc".equals(order)) {
                    order = "desc";
                }else {
                    order = "asc";
                }
                mPageId = 1;
                loadListData();
                Toast.makeText(mContext, "---", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadListData() {

        if (NewsAlbumTrackActivity.INTRO.equals(mParam1)){
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbum.getId()+"");
        map.put(DTransferConstants.SORT, order);
        map.put(DTransferConstants.PAGE, mPageId+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList mTrackList) {
                mPageId++;
                if (trackList == null || isChangeOrder) {
                    trackList = mTrackList;
                }else {
                    trackList.getTracks().addAll(mTrackList.getTracks());
                }

                mTrackAdapter.updateData(isChangeOrder,mTrackList);
                isChangeOrder = false;
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Log.d("hhp", "setUserVisibleHint: isVisibleToUser--"+isVisibleToUser+"---isViewCreated---"+isViewCreated );

        if (isVisibleToUser && isViewCreated) {
            isVisible = true;
            lazyLoad();
        }else {
            isVisible = false;
        }
    }

    private void lazyLoad() {
        if (!isVisible || !isViewCreated) {
            return;
        }
        loadListData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
