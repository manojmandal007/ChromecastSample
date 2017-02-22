package com.robo.sample.chromecastapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by manoj on 21/2/17.
 */

public class VideoListFragment extends RecyclerViewFragment implements VideoListAdapter.OnVideoItemClickListener {

    private List<VideoListDTO> mVideoItemList;
    private OnVideoListClick mListClick;

    public interface OnVideoListClick {
        void openVideoDetailWindow(String songName, String songUrl);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mListClick = (OnVideoListClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName()
                    + " must implement OnVideoListClick Listener");
        }
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    @Override
    public boolean isSwipeToRefresh() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createListItems();
        setAdapter();
    }

    private void createListItems() {
        List<String> videoUrlList = Arrays.asList(getActivity().getResources().getStringArray(R.array.video_url));
        List<String> videoNameList = Arrays.asList(getActivity().getResources().getStringArray(R.array.video_name));
        mVideoItemList = new ArrayList<>();
        for (int pos = 0; pos < videoNameList.size(); pos++) {
            VideoListDTO dto = new VideoListDTO();
            dto.songName = videoNameList.get(pos);
            dto.url = videoUrlList.get(pos);
            mVideoItemList.add(dto);
        }
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(new VideoListAdapter(mVideoItemList, this));
    }

    @Override
    public void onVideoItemClick(int pos) {
        mListClick.openVideoDetailWindow(mVideoItemList.get(pos).songName, mVideoItemList.get(pos).url);
    }
}
