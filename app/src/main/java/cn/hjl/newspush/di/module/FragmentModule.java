/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.hjl.newspush.di.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import cn.hjl.newspush.di.scope.ContextLife;
import cn.hjl.newspush.di.scope.PerActivity;
import cn.hjl.newspush.di.scope.PerFragment;
import cn.hjl.newspush.mvp.model.NewsListModel;
import cn.hjl.newspush.mvp.model.SubChannelModel;
import cn.hjl.newspush.mvp.presenter.impl.NewsChannelPresenterImpl;
import cn.hjl.newspush.mvp.presenter.impl.NewsListPresenterImpl;
import cn.hjl.newspush.mvp.ui.activities.NewsActivity;
import cn.hjl.newspush.mvp.ui.fragment.NewsListFragment;
import cn.hjl.newspush.utils.Toastor;
import dagger.Module;
import dagger.Provides;

/**
 * @author 咖枯
 * @version 1.0 2016/6/24
 */
@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @PerFragment
    @ContextLife("Activity")
    public Context provideActivityContext() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    public Activity provideActivity() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    public Fragment provideFragment() {
        return mFragment;
    }


//    @Provides
//    @PerFragment
//    public NewsListModel providSubChanneleModel(){
//        return new NewsListModel();
//    }
//
//    @Provides
//    @PerFragment
//    public NewsListPresenterImpl provideNewsChannelPresenter(NewsListModel model){
//        return new NewsListPresenterImpl((NewsListFragment) mFragment, model);
//    }

}
