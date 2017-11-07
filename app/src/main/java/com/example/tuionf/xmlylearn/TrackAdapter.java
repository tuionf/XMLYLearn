package com.example.tuionf.xmlylearn;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

/**
 * @author tuionf
 * @date 2017/10/26
 * @email 596019286@qq.com
 * @explain
 */

public class TrackAdapter extends BaseAdapter {

    private Context mContext;
    private TrackList mTrackList;

    public TrackAdapter(Context context, TrackList trackList) {
        super();
        this.mContext = context;
        this.mTrackList = trackList;
    }

    @Override
    public int getCount() {
        if (mTrackList == null ) {
            return 0;
        }
        return mTrackList.getTracks().size();
    }

    @Override
    public Object getItem(int position) {
        return mTrackList.getTracks().get(position);
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
            holder.title = (TextView) convertView.findViewById(R.id.track_title);
            holder.playCount = (TextView) convertView.findViewById(R.id.play_count);
            holder.trackTime = (TextView) convertView.findViewById(R.id.track_time);
            holder.trackUpdateTime = (TextView) convertView.findViewById(R.id.track_update_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Track track = mTrackList.getTracks().get(position);
        holder.title.setText(track.getTrackTitle());
        holder.playCount.setText(track.getPlayCount()+"");
        holder.trackTime.setText(track.getDuration()+"");
        holder.trackUpdateTime.setText(NewsUtils.formatUpdateTime(track.getUpdatedAt()));
        if (!TextUtils.isEmpty(track.getCoverUrlMiddle())){
            Picasso.with(mContext).load(track.getCoverUrlMiddle()).into(holder.cover);
        }else {
            holder.cover.setImageResource(R.mipmap.ic_launcher);
        }
        return convertView;
    }

    public void updateData(boolean isChangeOrder,TrackList albumsList){
        if (null != mTrackList && null != mTrackList.getTracks() && !isChangeOrder){
            mTrackList.getTracks().addAll(albumsList.getTracks());
        }else {
            mTrackList = albumsList;
        }
        notifyDataSetChanged();
    }

}
