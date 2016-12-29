package cn.hjl.newspush.mvp.view;

import java.util.List;

import cn.hjl.newspush.enums.LoadStatusType;
import cn.hjl.newspush.mvp.entity.VideoData;

/**
 * Created by fastabler on 2016/12/29.
 */

public interface VideoListView extends BaseView {
    String getVideoType();
    int getPage();
    void setVideoList(List<VideoData> videoDataList, @LoadStatusType.StatusType int loadType);
}
