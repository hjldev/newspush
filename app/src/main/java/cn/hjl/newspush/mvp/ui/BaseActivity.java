package cn.hjl.newspush.mvp.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.di.component.ActivityComponent;
import cn.hjl.newspush.di.component.DaggerActivityComponent;
import cn.hjl.newspush.di.module.ActivityModule;
import cn.hjl.newspush.di.scope.ContextLife;
import cn.hjl.newspush.mvp.presenter.BasePresenter;
import cn.hjl.newspush.mvp.view.BaseView;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.utils.MyUtils;
import cn.hjl.newspush.utils.Toastor;
import rx.Subscription;

/**
 * Created by fastabler on 2016/11/4.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView{

    protected ActivityComponent mActivityComponent;

    public ActivityComponent getmActivityComponent() {
        return mActivityComponent;
    }

    protected T mPresenter;

    public abstract int getLayoutId();

    public abstract void initInjector();

    public abstract void initViews();

    protected Toolbar toolbar;

    @Inject
    protected Toastor toastor;

    // 在使用rxjava时用于取消订阅
    protected List<Subscription> mSubscriptions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName());
        // dagger2的注入
        initActivityComponent();

        int layoutId = getLayoutId();
        // 设置布局
        setContentView(layoutId);
        // dagger2的注入
        initInjector();
        // 绑定ButterKnife，使用注解
        ButterKnife.bind(this);
        // 抽象初的类，用来初始化头部toolbar
        initToolBar();
        // 初始化view
        initViews();
        // 初始化presenter，一般用于进行网络请求
        if (mPresenter != null){
            mPresenter.onCreate(this);
        }
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((App) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    // TODO:适配4.4
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);
        if (mPresenter != null){
            mPresenter.onDestroy();
        }
        MyUtils.fixInputMethodManagerLeak(this);
        MyUtils.callAllSubscription(mSubscriptions);
    }

    @Override
    public void showMsg(String msg) {
        toastor.showSingletonToast(msg);
    }

    protected void startNewActivity(Class<? extends BaseActivity> cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void startNewActivity(Class<? extends BaseActivity> cls, Bundle bundle){
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.ACTIVITY_ARGS, bundle);
        startActivity(intent);
    }


    protected void startNewActivityForResult(Class<? extends BaseActivity> cls, int requestCode){
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    protected void startNewActivityForResult(Class<? extends BaseActivity> cls, int requestCode, Bundle bundle){
        Intent intent = new Intent(this, cls);
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
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.ACTIVITY_ARGS, bundle);
        ImageView newsSummaryPhotoIv = (ImageView) view.findViewById(resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, newsSummaryPhotoIv, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);
            startActivity(intent, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }


    protected Bundle getArguments(){
        Bundle bundle = getIntent().getBundleExtra(Constants.ACTIVITY_ARGS);
        if (bundle == null){
            throw new NullPointerException("arguments is null");
        }
        return bundle;
    }

}
