package cn.hjl.newspush.mvp.view;

import java.util.List;

import cn.hjl.newspush.mvp.entity.VideoChannel;

/**
 * Created by fastabler on 2016/11/5.
 */
public interface VideoView extends BaseView{
    void setVideoListChannel(List<VideoChannel> msg);
}
