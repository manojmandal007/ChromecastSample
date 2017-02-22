package com.robo.sample.chromecastapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manoj on 21/2/17.
 */

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoListDTO> mVideoItemList = new ArrayList<>();
    private OnVideoItemClickListener mClickListener;

    public interface OnVideoItemClickListener {
        void onVideoItemClick(int pos);
    }

    VideoListAdapter(List<VideoListDTO> videoItemList, OnVideoItemClickListener listener) {
        mVideoItemList = videoItemList;
        mClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View newsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_row, parent, false);
        return new VideoListHolder(newsView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        VideoListHolder newsHolder = ((VideoListHolder) holder);
        setNews(newsHolder, position);

    }

    private void setNews(VideoListHolder holder, int pos) {
        if (mVideoItemList.size() > pos) {
            VideoListDTO item = mVideoItemList.get(pos);
            holder.mSongName.setText(item.songName);
            initListener(holder, pos);
        }
    }

    private void initListener(VideoListHolder holder, final int pos) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onVideoItemClick(pos);
            }
        });

    }

    private static class VideoListHolder extends RecyclerView.ViewHolder {

        TextView mSongName;


        VideoListHolder(View view) {
            super(view);
            mSongName = (TextView) view.findViewById(R.id.song_name);

        }
    }

    @Override
    public int getItemCount() {
        return mVideoItemList.size();
    }

}