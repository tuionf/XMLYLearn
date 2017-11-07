package com.example.tuionf.xmlylearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link .} interface
 * to handle interaction events.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {

    private static final String ARG_PARAM1 = "tag";

    private String mParam1;
    private ListView mListView;
    private Context mContext;
    private CommonRequest mXimalaya;
    private AlbumAdapter mAlbumAdapter;
    private boolean mLoading = false;
    private AlbumList albumsList = null;
    private int mPageId = 1;
    // 懒加载相关 start
    private boolean isVisible;
    // 标志位，标志已经初始化完成，防止空指针的异常
    private boolean isViewCreated;
    // 懒加载相关 end
    private static final String TAG = "AlbumFragment";

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tag Parameter 1.
     * @return A new instance of fragment AlbumFragment.
     */

    public static AlbumFragment newInstance(String tag) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tag);
        Log.e(TAG, "newInstance: "+tag );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mListView = (ListView) view.findViewById(R.id.album_listview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        mContext = getActivity();
        mXimalaya = CommonRequest.getInstanse();
        mAlbumAdapter = new AlbumAdapter(mContext,albumsList);
        mListView.setAdapter(mAlbumAdapter);

        Log.d("hhp", "getUserVisibleHint()--"+getUserVisibleHint());

        if (getUserVisibleHint()) {
            loadData();
        }

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int count = view.getCount();
                    count = count - 5 > 0 ? count - 5 : count - 1;
                    if (view.getLastVisiblePosition() > count && (albumsList == null || mPageId < albumsList.getTotalPage())) {
                        loadData();
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
                Intent i = new Intent(mContext,NewsAlbumTrackActivity.class);
                i.putExtra("album",albumsList.getAlbums().get(position));
                startActivity(i);
            }
        });

    }

    private void loadData() {
//        if (mLoading) {
//            return;
//        }
//        mLoading = true;



        Map<String ,String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID ,"8");
        //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        map.put(DTransferConstants.CALC_DIMENSION ,1+"");
        map.put(DTransferConstants.PAGE, "" + mPageId);
        Log.e(TAG, "loadData:tag-- "+getArguments().getString(ARG_PARAM1) );

        if (!"热门".equals(getArguments().getString(ARG_PARAM1))){
            map.put(DTransferConstants.TAG_NAME ,getArguments().getString(ARG_PARAM1));
        }

        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList mAlbumList) {
                for (int i = 0; i < mAlbumList.getAlbums().size(); i++) {
                    Log.e(TAG, "onSuccess:--- "+mAlbumList.getAlbums().get(i).getAlbumTitle() );
                }

                mPageId++;
                if (albumsList == null) {
                    albumsList = mAlbumList;
                } else {
                    albumsList.getAlbums().addAll(mAlbumList.getAlbums());
                }

//                mAlbumAdapter.notifyDataSetChanged();
                mAlbumAdapter.updateData(mAlbumList);
//                mLoading = false;
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
        loadData();
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
