package cn.hjl.newspush.mvp.view;

import java.util.List;

import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.PhotoGirl;

/**
 * Created by fastabler on 2016/12/23.
 */

public interface PhotoView extends BaseView {
    int getPage();
    void resetPage();
    void loadMore();
    void setPhotoList(List<PhotoGirl> photoGirls, @LoadStatusType.StatusType int loadType);
}
