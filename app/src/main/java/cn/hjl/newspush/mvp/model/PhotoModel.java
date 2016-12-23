package cn.hjl.newspush.mvp.model;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.HostType;
import cn.hjl.newspush.mvp.entity.GirlData;
import cn.hjl.newspush.mvp.entity.PhotoGirl;
import cn.hjl.newspush.repository.network.RetrofitManager;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked._Subscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoModel {
    @Inject
    public PhotoModel(){

    }

    public Subscription loadPhoto(Context context, int size, int page, final RequestCallBack<List<PhotoGirl>> listener){
        return RetrofitManager.getInstance(HostType.GANK_GIRL_PHOTO)
                .getPhotoListObservable(size, page)
                .map(new Func1<GirlData, List<PhotoGirl>>() {
                    @Override
                    public List<PhotoGirl> call(GirlData girlData) {
                        return girlData.getResults();
                    }
                })
                .compose(TransformUtils.<List<PhotoGirl>>defaultSchedulers())
                .subscribe(new _Subscriber<List<PhotoGirl>>(context, true) {
                    @Override
                    protected void _onNext(List<PhotoGirl> photoGirls) {
                        listener.success(photoGirls);
                    }

                    @Override
                    protected void _onError(String message) {
                        listener.onError(message);
                    }
                });
    }
}
