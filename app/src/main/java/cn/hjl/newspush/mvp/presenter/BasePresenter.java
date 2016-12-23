package cn.hjl.newspush.mvp.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.hjl.newspush.mvp.view.BaseView;


/**
 * Created by fastabler on 2016/11/8.
 */
public interface BasePresenter {
    void onCreate(Context context);
    void attachView(@NonNull BaseView baseView);
    void onDestroy();

}
