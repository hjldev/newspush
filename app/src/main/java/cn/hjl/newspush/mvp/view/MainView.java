package cn.hjl.newspush.mvp.view;

import java.util.List;

import cn.hjl.newspush.mvp.entity.NewsChannelItem;

/**
 * Created by fastabler on 2016/11/5.
 */
public interface MainView extends BaseView{
    void setChannelDate(List<NewsChannelItem> msg);
}
