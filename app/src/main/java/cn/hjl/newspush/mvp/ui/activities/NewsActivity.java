package cn.hjl.newspush.mvp.ui.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.presenter.impl.NewsChannelPresenterImpl;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.mvp.ui.adapter.pageradapter.NewsFragmentPagerAdapter;
import cn.hjl.newspush.mvp.ui.fragment.NewsListFragment;
import cn.hjl.newspush.mvp.view.MainView;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked.rxbus.RxBus;
import rx.Subscriber;

public class NewsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    private NewsFragmentPagerAdapter adapter;


    @Inject
    NewsChannelPresenterImpl presenter;

    private List<Fragment> mNewsFragmentList = new ArrayList<>();
    private List<String> channelNames = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {

        setStatusBarTranslucent();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mPresenter = presenter;
        mPresenter.attachView(this);

        // 当进行添加或者删除频道时通过RxBus动态修改
        mSubscriptions.add(RxBus.getDefault().toObservable(NewsChannelItem.class)
                .compose(TransformUtils.<NewsChannelItem>defaultSchedulers())
                .subscribe(new Subscriber<NewsChannelItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(NewsChannelItem newsChannelItem) {
                        if (newsChannelItem.getNewsChannelSelect()) {
                            adapter.addFragment(NewsListFragment.newsInstance(newsChannelItem), newsChannelItem.getNewsChannelName());
                        } else {
                            adapter.remove(newsChannelItem.getNewsChannelName());
                            mViewPager.setAdapter(adapter);
                            mTabs.setupWithViewPager(mViewPager);

                        }
                    }
                }));
    }


    @Override
    public void setChannelDate(List<NewsChannelItem> channelDate) {
        if (channelDate != null) {
            setNewsList(channelDate);
        }
    }

    private void setNewsList(List<NewsChannelItem> newsChannels) {
        channelNames = new ArrayList<>();
        for (NewsChannelItem newsChannel : newsChannels) {
            NewsListFragment baseListFragment = NewsListFragment.newsInstance(newsChannel);
            mNewsFragmentList.add(baseListFragment);
            channelNames.add(newsChannel.getNewsChannelName());
        }
        setViewPager(channelNames, mNewsFragmentList);
    }

    private void setViewPager(List<String> channelNames, List<Fragment> mNewsFragmentList) {
        adapter = new NewsFragmentPagerAdapter(
                getSupportFragmentManager(), channelNames, mNewsFragmentList);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startNewActivity(PhotoActivity.class);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

        } else if (id == R.id.nav_video) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.add_channel_iv)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_channel_iv:
                startNewActivity(ChannelActivity.class);
                break;
        }
    }



}
