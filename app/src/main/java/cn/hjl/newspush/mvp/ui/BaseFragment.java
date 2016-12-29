package cn.hjl.newspush.mvp.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cn.hjl.newspush.App;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.di.component.DaggerFragmentComponent;
import cn.hjl.newspush.di.component.FragmentComponent;
import cn.hjl.newspush.di.module.FragmentModule;
import cn.hjl.newspush.di.scope.ContextLife;
import cn.hjl.newspush.mvp.presenter.BasePresenter;
import cn.hjl.newspush.mvp.view.BaseView;
import cn.hjl.newspush.utils.MyUtils;
import cn.hjl.newspush.utils.Toastor;
import rx.Subscription;

/**
 * Created by fastabler on 2016/11/5.
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView{

    public FragmentComponent getFragmentComponent() {
        return mFragmentComponent;
    }

    protected FragmentComponent mFragmentComponent;
    protected T mPresenter;

    protected abstract void initInjector();

    private View mFragmentView;

    public abstract void initViews(View view);

    public abstract int getLayoutId();

    @Inject
    protected Toastor toastor;

    // 在使用rxjava时用于取消订阅
    protected List<Subscription> mSubscriptions = new ArrayList<>();

    private boolean isFirstLoad = true;
    private boolean isInvisible = false;
    private boolean isReady = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(((App)getActivity().getApplication()).getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
        initInjector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null){
            mFragmentView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mFragmentView);
            initViews(mFragmentView);
            isReady = true;
            delayLoad();
//            mPresenter.onCreate(getActivity());
        }
        return mFragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            isInvisible = true;
        } else {
            isInvisible = false;
        }
        delayLoad();
    }

    /**
     * 进行懒加载，当第一次显示、可见且对象初始化完成初始化加载，此后不再执行oncreate方法
     */
    protected void delayLoad(){
        if (!isFirstLoad || !isInvisible || !isReady){
            return;
        }
        if (mPresenter != null){
            mPresenter.onCreate(getContext());

        }
        isFirstLoad = false;
    }

    @Override
    public void showMsg(String msg) {
        toastor.showSingletonToast(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);

        if (mPresenter != null){
            mPresenter.onDestroy();
        }

        MyUtils.callAllSubscription(mSubscriptions);

    }

    protected void startNewActivity(Class<? extends BaseActivity> cls){
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    protected void startNewActivity(Class<? extends BaseActivity> cls, Bundle bundle){
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(Constants.ACTIVITY_ARGS, bundle);
        startActivity(intent);
    }


    protected void startNewActivityForResult(Class<? extends BaseActivity> cls, int requestCode){
        Intent intent = new Intent(getActivity(), cls);
        startActivityForResult(intent, requestCode);
    }

    protected void startNewActivityForResult(Class<? extends BaseActivity> cls, int requestCode, Bundle bundle){
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(Constants.ACTIVITY_ARGS, bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 转场动画
     * @param view
     * @param resId
     * @param cls
     * @param bundle
     */
    protected void startNewActivity(View view, int resId, Class<? extends BaseActivity> cls, Bundle bundle){
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(Constants.ACTIVITY_ARGS, bundle);
        ImageView newsSummaryPhotoIv = (ImageView) view.findViewById(resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(), newsSummaryPhotoIv, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);
            startActivity(intent, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    }

}
