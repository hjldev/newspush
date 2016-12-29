package cn.hjl.newspush.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.VideoData;
import cn.hjl.newspush.mvp.presenter.impl.VideoListPresenter;
import cn.hjl.newspush.mvp.ui.BaseFragment;
import cn.hjl.newspush.mvp.ui.activities.VideoDetailActivity;
import cn.hjl.newspush.mvp.ui.adapter.VideoListAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.OnItemClickListener;
import cn.hjl.newspush.mvp.view.VideoListView;
import cn.hjl.newspush.rxpacked.TransformUtils;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by fastabler on 2016/12/24.
 */

public class VideoListFragment extends BaseFragment implements VideoListView, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @Inject
    VideoListAdapter mVideoListAdapter;
    @Inject
    VideoListPresenter presenter;
    private String mVideoType;
    private int startPage = 10;

    public static VideoListFragment newInstance(String channelId) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIDEO_CHANNLE_ID, channelId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initViews(View view) {
        mPresenter = presenter;
        mPresenter.attachView(this);
        mVideoListAdapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mVideoListAdapter);
        mVideoListAdapter.setOnLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                VideoData data = (VideoData) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("url", data.getMp4_url());
                startNewActivity(view, R.id.video_cover_iv, VideoDetailActivity.class, bundle);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.recyclerview_base;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoType = getArguments().getString(Constants.VIDEO_CHANNLE_ID);
        }
    }

    @Override
    public String getVideoType() {
        return mVideoType;
    }

    @Override
    public int getPage() {
        return startPage;
    }

    @Override
    public void setVideoList(final List<VideoData> videoDataList, @LoadStatusType.StatusType int loadType) {
        switch (loadType) {
            case LoadStatusType.TYPE_REFRESH_SUCCESS:
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                Observable.just(isRefreshing)
                        .compose(TransformUtils.<Boolean>defaultSchedulers())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    mVideoListAdapter.removeAll();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                startPage += 10;
                                mVideoListAdapter.addData(videoDataList);
                                mVideoListAdapter.loadMoreComplete();
                            }
                        });
                break;
            case LoadStatusType.TYPE_REFRESH_ERROR:
                swipeRefreshLayout.setRefreshing(false);
                mVideoListAdapter.loadMoreFail();
                break;
        }

    }

    @Override
    public void onLoadMoreRequested() {
        presenter.loadData();
    }

    @Override
    public void onRefresh() {
        startPage = 10;
        presenter.loadData();
    }
}
