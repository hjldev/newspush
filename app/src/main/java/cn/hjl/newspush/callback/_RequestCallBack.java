package cn.hjl.newspush.callback;

import cn.hjl.newspush.mvp.view.BaseView;

/**
 * Created by fastabler on 2016/12/16.
 */

public abstract class _RequestCallBack<T> implements RequestCallBack<T> {

    private BaseView baseView;

    public _RequestCallBack(BaseView baseView){
        this.baseView = baseView;
    }

    @Override
    public void success(T data) {
        _success(data);
    }

    @Override
    public void onError(String errorMsg) {
        this.baseView.showMsg(errorMsg);
    }

    public abstract void _success(T data);
}
