package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import cn.hjl.newspush.mvp.model.NewsListModel;
import cn.hjl.newspush.mvp.model.base.NewsListInteractor;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.NewsListView;

/**
 * Created by fastabler on 2016/11/9.
 * 获取新闻列表
 */
public class NewsListPresenterImpl extends BasePresenterImpl<NewsListView>{

    private String newsType;
    private String newsId;
    private int startPage;
    private NewsListInteractor<List<NewsSummary>> mNewsListModel;

    @Inject
    public NewsListPresenterImpl(NewsListModel newsListModel){
        this.mNewsListModel = newsListModel;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadNewData();
    }

    public void loadNewData() {
        mNewsListModel.loadNewsList(mContext, newsType, newsId, startPage, new RequestCallBack<List<NewsSummary>>() {
            @Override
            public void success(List<NewsSummary> data) {
                mView.setNewsList(data, LoadStatusType.TYPE_REFRESH_SUCCESS);
            }

            @Override
            public void onError(String errorMsg) {
                mView.showMsg(errorMsg);
                mView.setNewsList(null, LoadStatusType.TYPE_REFRESH_ERROR);
            }

        });
    }

    public void setNewsTypeAndId(String newsType, String newsId) {
        this.newsType = newsType;
        this.newsId = newsId;
    }

    public void refreshData() {
        startPage = 0;
        loadNewData();
    }

    public void loadMore() {
        startPage += 20;
        loadNewData();
    }

}
