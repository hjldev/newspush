package cn.hjl.newspush.mvp.view;

import cn.hjl.newspush.mvp.entity.NewsDetail;

/**
 * Created by fastabler on 2016/12/16.
 */

public interface NewsDetailView extends BaseView{
    void setNewsDetail(NewsDetail newsDetail);
    String getPostId();
}
