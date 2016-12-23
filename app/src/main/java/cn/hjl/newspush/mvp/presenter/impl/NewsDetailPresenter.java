package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.callback._RequestCallBack;
import cn.hjl.newspush.mvp.entity.NewsDetail;
import cn.hjl.newspush.mvp.model.NewsDetailModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.NewsDetailView;

/**
 * Created by fastabler on 2016/12/16.
 */

public class NewsDetailPresenter extends BasePresenterImpl<NewsDetailView> {

    private NewsDetailModel newsDetailModel;

    @Inject
    public NewsDetailPresenter(NewsDetailModel newsDetailModel){
        this.newsDetailModel = newsDetailModel;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        newsDetailModel.getNewsDetail(context, new _RequestCallBack<NewsDetail>(mView) {

            @Override
            public void _success(NewsDetail data) {
                mView.setNewsDetail(data);
            }
        }, mView.getPostId());
    }
}
