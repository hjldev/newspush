package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback._RequestCallBack;
import cn.hjl.newspush.mvp.entity.VideoChannel;
import cn.hjl.newspush.mvp.model.VideoModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.MainView;
import cn.hjl.newspush.mvp.view.VideoView;

/**
 * Created by fastabler on 2016/12/24.
 */

public class VideoPresenter extends BasePresenterImpl<VideoView> {

    private VideoModel videoModel;

    @Inject
    public VideoPresenter(VideoModel model){
        this.videoModel = model;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadVideoChannel();

    }

    public void loadVideoChannel() {
        videoModel.getVideoChannelList(mContext, new _RequestCallBack<List<VideoChannel>>(mView) {
            @Override
            public void _success(List<VideoChannel> data) {
                mView.setVideoListChannel(data);
            }
        });
    }
}
