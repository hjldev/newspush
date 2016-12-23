package cn.hjl.newspush.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.entity.NewsPhotoDetail;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import cn.hjl.newspush.mvp.model.NewsListModel;
import cn.hjl.newspush.mvp.presenter.impl.NewsListPresenterImpl;
import cn.hjl.newspush.mvp.ui.BaseFragment;
import cn.hjl.newspush.mvp.ui.activities.NewsDetailActivity;
import cn.hjl.newspush.mvp.ui.activities.NewsPhotoDetailActivity;
import cn.hjl.newspush.mvp.ui.adapter.NewsListAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.OnItemClickListener;
import cn.hjl.newspush.mvp.view.NewsListView;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.utils.Toastor;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by fastabler on 2016/11/9.
 */
public class NewsListFragment extends BaseFragment implements NewsListView, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private String mNewsId;
    private String mNewsType;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Inject
    NewsListPresenterImpl newsListPresenter;
    @Inject
    NewsListAdapter newsListAdapter;

    public static NewsListFragment newsInstance(NewsChannelItem newsChannel){
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, newsChannel.getNewsChannelId());
        bundle.putString(Constants.NEWS_TYPE, newsChannel.getNewsChannelType());
        bundle.putInt(Constants.CHANNEL_POSITION, newsChannel.getNewsChannelIndex());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mNewsId = bundle.getString(Constants.NEWS_ID);
            mNewsType = bundle.getString(Constants.NEWS_TYPE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.recyclerview_base;
    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initViews(View view) {
        newsListPresenter.setNewsTypeAndId(mNewsType, mNewsId);
        mPresenter = newsListPresenter;
        mPresenter.attachView(this);

        newsListAdapter.openLoadAnimation();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(newsListAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        newsListAdapter.setOnLoadMoreListener(this);

        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewsSummary newsSummary = (NewsSummary) adapter.getItem(position);
                switch (newsSummary.getItemType()){
                    case NewsSummary.TEXT:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.NEWS_POST_ID, newsSummary.getPostid());
                        bundle.putString(Constants.NEWS_IMG_RES, newsSummary.getImgsrc());
                        startNewActivity(view, R.id.news_summary_photo_iv, NewsDetailActivity.class, bundle);
//                        startNewActivity(NewsDetailActivity.class, bundle);
                        break;
                    case NewsSummary.IMG:
                        NewsPhotoDetail newsPhotoDetail = getPhotoDetail(newsSummary);
                        Bundle bundlePhoto = new Bundle();
                        bundlePhoto.putParcelable(Constants.PHOTO_DETAIL, newsPhotoDetail);
                        startNewActivity(NewsPhotoDetailActivity.class, bundlePhoto);
                        break;
                }
            }
        });
    }

    private NewsPhotoDetail getPhotoDetail(NewsSummary newsSummary) {
        NewsPhotoDetail newsPhotoDetail = new NewsPhotoDetail();
        newsPhotoDetail.setTitle(newsSummary.getTitle());
        List<NewsPhotoDetail.Picture> pictureList = new ArrayList<>();

        if (newsSummary.getAds() != null) {
            for (NewsSummary.AdsBean entity : newsSummary.getAds()) {
                setValuesAndAddToList(pictureList, entity.getTitle(), entity.getImgsrc());
            }
        } else if (newsSummary.getImgextra() != null) {
            for (NewsSummary.ImgextraBean entity : newsSummary.getImgextra()) {
                setValuesAndAddToList(pictureList, null, entity.getImgsrc());
            }
        } else {
            setValuesAndAddToList(pictureList, null, newsSummary.getImgsrc());
        }

        newsPhotoDetail.setPictures(pictureList);
        return newsPhotoDetail;
    }

    private void setValuesAndAddToList(List<NewsPhotoDetail.Picture> pictureList, String title, String imgsrc) {
        NewsPhotoDetail.Picture picture = new NewsPhotoDetail.Picture();
        if (title != null) {
            picture.setTitle(title);
        }
        picture.setImgSrc(imgsrc);

        pictureList.add(picture);
    }

    @Override
    public void setNewsList(final List<NewsSummary> newsSummary, @LoadStatusType.StatusType int loadType) {
        switch (loadType) {
            case LoadStatusType.TYPE_REFRESH_SUCCESS:
                boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                mSubscriptions.add(Observable.just(isRefreshing)
                        .compose(TransformUtils.<Boolean>defaultSchedulers())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    newsListAdapter.removeAll();
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                newsListAdapter.addData(newsSummary);
                                newsListAdapter.loadMoreComplete();
                            }
                        }));
                break;
            case LoadStatusType.TYPE_REFRESH_ERROR:
                mSwipeRefreshLayout.setRefreshing(false);
                newsListAdapter.loadMoreFail();
                break;
            case LoadStatusType.TYPE_REFRESH_COMPLETE:
                newsListAdapter.addData(newsSummary);
                mSwipeRefreshLayout.setRefreshing(false);
                newsListAdapter.loadMoreComplete();
                break;
            case LoadStatusType.TYPE_REFRESH_EMPTY:
                mSwipeRefreshLayout.setRefreshing(false);
                newsListAdapter.loadMoreFail();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        newsListPresenter.refreshData();
    }

    @Override
    public void onLoadMoreRequested() {
        newsListPresenter.loadMore();
    }


    @Override
    public void showMsg(String msg) {
        toastor.showSingletonToast(msg);
    }
}
