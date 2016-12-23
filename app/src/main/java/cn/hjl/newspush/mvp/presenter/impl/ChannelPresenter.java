package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.model.SubChannelModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.ChannelView;

/**
 * Created by fastabler on 2016/12/3.
 * 用于对数据库的操作
 */

public class ChannelPresenter extends BasePresenterImpl<ChannelView> {
    private SubChannelModel subChannelModel;

    @Inject
    public ChannelPresenter(SubChannelModel subChannelModel) {
        this.subChannelModel = subChannelModel;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadChannel();
    }

    public void loadChannel() {
        mSubscription.add(subChannelModel.lodeNewsChannels(new RequestCallBack<List<NewsChannelItem>>() {
            @Override
            public void success(List<NewsChannelItem> data) {
                mView.subChannel(data);
            }

            @Override
            public void onError(String errorMsg) {
                mView.showMsg(errorMsg);
            }
        }));

        mSubscription.add(subChannelModel.lodeUnSubChannels(new RequestCallBack<List<NewsChannelItem>>() {
            @Override
            public void success(List<NewsChannelItem> data) {
                mView.unsubChannel(data);
            }

            @Override
            public void onError(String errorMsg) {
                mView.showMsg(errorMsg);
            }
        }));
    }

    public void subChannelUpdate(List<NewsChannelItem>... subItem) {
        mSubscription.add(subChannelModel.subDb(new RequestCallBack<List<NewsChannelItem>>() {
            @Override
            public void success(List<NewsChannelItem> data) {

            }

            @Override
            public void onError(String errorMsg) {

            }
        }, mContext, subItem));
    }


}
