package cn.hjl.newspush.mvp.ui.adapter.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;
    private List<Fragment> mNewsFragmentList;
    private FragmentManager fm;


    public void addFragment(Fragment fragment, String title){
        mTitles.add(title);
        this.mNewsFragmentList.add(fragment);
        notifyDataSetChanged();
    }

    public void remove(String title){
        int index = mTitles.indexOf(title);
        mTitles.remove(index);
        mNewsFragmentList.remove(index);
        notifyDataSetChanged();
    }

    public NewsFragmentPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> newsFragmentList) {
        super(fm);
        this.fm = fm;
        mTitles = titles;
        mNewsFragmentList = newsFragmentList;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mNewsFragmentList.get(position);
    }


    @Override
    public int getCount() {
        return mNewsFragmentList.size();
    }

    @Override
    public long getItemId(int position) {
        // 获取当前数据的hashCode
        int hashCode = mNewsFragmentList.get(position).hashCode();
        return hashCode;
    }

}
