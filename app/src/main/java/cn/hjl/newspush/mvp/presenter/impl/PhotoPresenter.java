package cn.hjl.newspush.mvp.presenter.impl;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback._RequestCallBack;
import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.PhotoGirl;
import cn.hjl.newspush.mvp.model.PhotoModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.PhotoView;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoPresenter extends BasePresenterImpl<PhotoView> {

    private PhotoModel photoModel;

    @Inject
    public PhotoPresenter(PhotoModel photoModel){
        this.photoModel = photoModel;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        loadPhotoList();

    }

    public void loadPhotoList(){
        photoModel.loadPhoto(mContext, 20, mView.getPage(), new _RequestCallBack<List<PhotoGirl>>(mView) {
            @Override
            public void _success(List<PhotoGirl> data) {
                mView.setPhotoList(data, LoadStatusType.TYPE_REFRESH_SUCCESS);
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                mView.setPhotoList(null, LoadStatusType.TYPE_REFRESH_ERROR);
            }
        });
    }

    public void refreshData(){
        mView.resetPage();
        loadPhotoList();
    }

    public void loadMoreData(){
        mView.loadMore();
        loadPhotoList();
    }

}
