package cn.hjl.newspush.callback;

/**
 * Created by fastabler on 2016/11/8.
 */
public interface RequestCallBack<T> {

    void success(T data);
    void onError(String errorMsg);
}
