package cn.hjl.newspush.mvp.model.base;

import cn.hjl.newspush.callback.RequestCallBack;

/**
 * Created by fastabler on 2016/12/2.
 */

public interface UnSubChannelInteractor<T> {
    void loadUnSubData(RequestCallBack<T> callBack);
}
