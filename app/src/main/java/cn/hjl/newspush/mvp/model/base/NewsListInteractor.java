package cn.hjl.newspush.mvp.model.base;

import android.content.Context;

import cn.hjl.newspush.callback.RequestCallBack;
import rx.Subscription;

/**
 * Created by fastabler on 2016/11/9.
 */
public interface NewsListInteractor<T> {
    Subscription loadNewsList(Context context, String type, String id, int startPage, RequestCallBack<T> listener);
}
