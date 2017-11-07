package com.example.tuionf.xmlylearn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

/**
 * @author tuionf
 * @date 2017/10/26
 * @email 596019286@qq.com
 * @explain
 */

public class AlbumAdapter extends BaseAdapter {

    private Context mContext;
    private AlbumList albumsList = new AlbumList();

    public AlbumAdapter(Context context, AlbumList albumsList) {
        super();
        this.mContext = context;
        this.albumsList = albumsList;
    }

    @Override
    public int getCount() {
        if (albumsList == null ) {
            return 0;
        }
        return albumsList.getAlbums().size();
    }

    @Override
    public Object getItem(int position) {
        return albumsList.getAlbums().get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.album_content_item, parent, false);
            holder = new ViewHolder();
            holder.content = (ViewGroup) convertView;
            holder.cover = (ImageView) convertView.findViewById(R.id.album_img);
            holder.title = (TextView) convertView.findViewById(R.id.album_title);
            holder.intro = (TextView) convertView.findViewById(R.id.album_intro);
            holder.playCount = (TextView) convertView.findViewById(R.id.play_count);
            holder.trackCount = (TextView) convertView.findViewById(R.id.include_track_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Album album = albumsList.getAlbums().get(position);
        holder.title.setText(album.getAlbumTitle());
        holder.intro.setText(album.getAlbumIntro());
        holder.playCount.setText(NewsUtils.formatPlayCountNum(album.getPlayCount()+""));
        Log.e("hhp", "getView: "+album.getFreeTrackCount() +"---"+album.getIncludeTrackCount());
        holder.trackCount.setText(album.getIncludeTrackCount()+"");
//        holder.cover.
        if (!TextUtils.isEmpty(album.getCoverUrlMiddle())){
            Picasso.with(mContext).load(album.getCoverUrlMiddle()).into(holder.cover);
        }else {
            holder.cover.setImageResource(R.mipmap.ic_launcher);
        }
        return convertView;
    }

    public void updateData(AlbumList mAlbumsList){
        if (null != albumsList && null != albumsList.getAlbums()){
            albumsList.getAlbums().addAll(mAlbumsList.getAlbums());
        }else {
            albumsList = mAlbumsList;
        }
        notifyDataSetChanged();
    }
}


