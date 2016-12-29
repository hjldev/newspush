package cn.hjl.newspush.mvp.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.VideoChannel;
import cn.hjl.newspush.mvp.presenter.impl.VideoPresenter;
import cn.hjl.newspush.mvp.ui.BaseActivity;
//import cn.hjl.newspush.mvp.ui.fragment.VideoListFragment;
import cn.hjl.newspush.mvp.ui.fragment.VideoListFragment;
import cn.hjl.newspush.mvp.view.VideoView;
import cn.hjl.newspush.utils.TabLayoutUtil;

/**
 * Created by fastabler on 2016/12/24.
 */

public class VideoActivity extends BaseActivity implements VideoView{

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @Inject
    VideoPresenter presenter;

    private ArrayList<Fragment> mVideoFragmentList = new ArrayList<>();
    private List<String> mChannelNames;
    private String mCurrentViewPagerName;
    private int mCurrentItem = 0;

    @Override
    public int getLayoutId() {
        return R.layout.app_bar_main;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {
        setTitle("视频");
        mPresenter = presenter;
        mPresenter.attachView(this);

    }

    @Override
    public void setVideoListChannel(List<VideoChannel> videoChannelList) {
        final List<String> channelNames = new ArrayList<>();

        if(videoChannelList != null){
            setNewsList(videoChannelList,channelNames);
            setViewPager(channelNames);
        }
    }

    private void setNewsList(List<VideoChannel> videoChannelList, List<String> channelNames) {
        mVideoFragmentList.clear();
        for (VideoChannel videoChannel: videoChannelList) {
            VideoListFragment videoListFragment = createListFragment(videoChannel);
            mVideoFragmentList.add(videoListFragment);
            channelNames.add(videoChannel.getVideoChannelName());
        }
    }

    private VideoListFragment createListFragment(VideoChannel videoChannel){
        return VideoListFragment.newInstance(videoChannel.getVideoChannelID());
    }

    private void setViewPager(List<String> channelNames) {
        PostFragmentPagerAdapter adapter = new PostFragmentPagerAdapter(
                getSupportFragmentManager(),channelNames,mVideoFragmentList);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        TabLayoutUtil.dynamicSetTabLayoutMode(mTabLayout);

    }

    class PostFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<String> mTitles;
        private List<Fragment> mPostFragmentList;


        public PostFragmentPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> postFragmentList) {
            super(fm);
            mTitles = titles;
            mPostFragmentList = postFragmentList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mPostFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mPostFragmentList == null ? 0 : mPostFragmentList.size();
        }
    }
}
