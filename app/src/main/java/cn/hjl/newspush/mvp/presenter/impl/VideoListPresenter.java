package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback._RequestCallBack;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.VideoData;
import cn.hjl.newspush.mvp.model.VideoListModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.VideoListView;

/**
 * Created by fastabler on 2016/12/29.
 */

public class VideoListPresenter extends BasePresenterImpl<VideoListView> {

    private VideoListModel listModel;

    @Inject
    public VideoListPresenter(VideoListModel listModel){
        this.listModel= listModel;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadData();
    }

    public void loadData() {
        listModel.getVideoList(mView.getVideoType(), mView.getPage(), new _RequestCallBack<List<VideoData>>(mView) {
            @Override
            public void _success(List<VideoData> data) {
                mView.setVideoList(data, LoadStatusType.TYPE_REFRESH_SUCCESS);
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                mView.setVideoList(null, LoadStatusType.TYPE_REFRESH_ERROR);
            }
        });
    }
}
