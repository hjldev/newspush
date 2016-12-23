package cn.hjl.newspush.mvp.ui.activities;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.presenter.impl.ChannelPresenter;
import cn.hjl.newspush.mvp.presenter.impl.NewsChannelPresenterImpl;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.mvp.ui.adapter.ChannelAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.OnItemClickListener;
import cn.hjl.newspush.mvp.view.ChannelView;
import cn.hjl.newspush.rxpacked.rxbus.RxBus;
import cn.hjl.newspush.utils.Log;
import rx.Subscriber;

/**
 * Created by fastabler on 2016/12/1.
 */

public class ChannelActivity extends BaseActivity implements ChannelView{

    @BindView(R.id.news_channel_mine_rv)
    RecyclerView mineRecyclerView;
    @BindView(R.id.news_channel_more_rv)
    RecyclerView moreRecyclerView;

    @Inject
    ChannelAdapter mineAdapter;
    @Inject
    ChannelAdapter moreAdapter;

    @Inject
    ChannelPresenter presenter;

    @Inject
    RxDao<NewsChannelItem, String> rxDao;

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_channel;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {
        setTitle(R.string.channel_title);
        mPresenter = presenter;
        mPresenter.attachView(this);

        initRecyclerView(mineRecyclerView, true);
        initRecyclerView(moreRecyclerView, false);

    }

    private void initRecyclerView(RecyclerView recyclerview, boolean b) {
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        if (b){
            recyclerview.setAdapter(mineAdapter);
            recyclerview.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                    NewsChannelItem item = (NewsChannelItem) adapter.getItem(position);
                    boolean isFixed = item.getNewsChannelFixed();
                    if (!isFixed){
                        item.setNewsChannelSelect(false);
                        item.setNewsChannelIndex(moreAdapter.getData().size());
                        postRxBus(item);
                        moreAdapter.addData(item);
                        mineAdapter.remove(position);
                    }
                }
            });
        } else {
            recyclerview.setAdapter(moreAdapter);
            recyclerview.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {
                    NewsChannelItem item = (NewsChannelItem) adapter.getItem(position);
                    item.setNewsChannelSelect(true);
                    item.setNewsChannelIndex(mineAdapter.getData().size());
                    postRxBus(item);
                    mineAdapter.addData(item);
                    moreAdapter.remove(position);
                }
            });
        }

    }

    private void postRxBus(NewsChannelItem item) {
//        mSubscriptions.add(rxDao.insertOrReplace(item)
//                .subscribe(new Subscriber<NewsChannelItem>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(NewsChannelItem newsChannelItem) {
                        RxBus.getDefault().post(item);
//                    }
//                }));
    }

    @Override
    public void subChannel(List<NewsChannelItem> items) {
        mineAdapter.addData(items);
    }

    @Override
    public void unsubChannel(List<NewsChannelItem> items) {
        moreAdapter.addData(items);
    }


    // 结束掉页面之后，对数据库进行重新的修改
    @Override
    public void finish() {
        presenter.subChannelUpdate(mineAdapter.getData(), moreAdapter.getData());
        super.finish();
    }

}
