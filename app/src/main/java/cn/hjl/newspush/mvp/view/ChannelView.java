package cn.hjl.newspush.mvp.view;

import java.util.List;

import cn.hjl.newspush.mvp.entity.NewsChannelItem;

/**
 * Created by fastabler on 2016/12/3.
 */

public interface ChannelView extends BaseView {
    void subChannel(List<NewsChannelItem> items);
    void unsubChannel(List<NewsChannelItem> items);
}
