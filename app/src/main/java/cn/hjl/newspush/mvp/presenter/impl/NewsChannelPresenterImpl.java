package cn.hjl.newspush.mvp.presenter.impl;


import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.model.SubChannelModel;
import cn.hjl.newspush.mvp.model.base.SubChannelInteractor;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.MainView;
import rx.Subscription;

/**
 * Created by fastabler on 2016/11/8.
 * 获得订阅频道
 */
public class NewsChannelPresenterImpl extends BasePresenterImpl<MainView> {
    private SubChannelInteractor<List<NewsChannelItem>> mNewsInteractor;
    @Inject
    public NewsChannelPresenterImpl(SubChannelModel newsInteractor){
        this.mNewsInteractor = newsInteractor;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadNewsChannels();
    }


    public void loadNewsChannels() {
        mSubscription.add(mNewsInteractor.lodeNewsChannels(new RequestCallBack<List<NewsChannelItem>>() {

            @Override
            public void success(List<NewsChannelItem> data) {
                mView.setChannelDate(data);
            }

            @Override
            public void onError(String errorMsg) {

            }
        }));

    }

}
