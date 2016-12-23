package cn.hjl.newspush.mvp.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.mvp.view.BaseView;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.utils.MyUtils;
import rx.Subscription;

/**
 * Created by fastabler on 2016/11/8.
 */
public class BasePresenterImpl<T extends BaseView> implements BasePresenter {
    protected T mView;
    protected Context mContext;
    protected List<Subscription> mSubscription = new ArrayList<>();
    @Override
    public void onCreate(Context context) {
        this.mContext = context;
    }

    @Override
    public void attachView(@NonNull BaseView baseView) {
        mView = (T) baseView;
    }

    @Override
    public void onDestroy() {
        MyUtils.callAllSubscription(mSubscription);
    }

}
