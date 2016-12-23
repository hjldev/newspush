package cn.hjl.newspush.mvp.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.PhotoGirl;
import cn.hjl.newspush.mvp.presenter.impl.PhotoPresenter;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.mvp.ui.adapter.PhotoAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.OnItemClickListener;
import cn.hjl.newspush.mvp.view.PhotoView;
import cn.hjl.newspush.utils.Log;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, PhotoView, BaseQuickAdapter.RequestLoadMoreListener{

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Inject
    PhotoAdapter adapter;

    @Inject
    PhotoPresenter presenter;

    private int page = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {
        setTitle(R.string.action_pic);
        mPresenter = presenter;
        mPresenter.attachView(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initRecycler();

    }

    private void initRecycler() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnLoadMoreListener(this);


        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d("tagsOnClick");
                String url = ((PhotoGirl)adapter.getItem(position)).getUrl();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PHOTO_DETAIL_URL, url);
                startNewActivity(view, R.id.photo_iv, PhotoDetailActivity.class, bundle);
            }

        });
    }

    @Override
    public void onRefresh() {
        presenter.loadPhotoList();
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public void resetPage() {
        this.page = 1;
    }

    @Override
    public void loadMore() {
        ++page;
    }

    @Override
    public void setPhotoList(List<PhotoGirl> photoGirls, @LoadStatusType.StatusType int loadType) {
        switch (loadType){
            case LoadStatusType.TYPE_REFRESH_SUCCESS:
                adapter.loadMoreComplete();
                swipeRefreshLayout.setRefreshing(false);
                adapter.addData(photoGirls);

                break;
            case LoadStatusType.TYPE_REFRESH_ERROR:
                adapter.loadMoreFail();
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        presenter.loadMoreData();
    }
}
