package cn.hjl.newspush.mvp.ui.adapter;

import javax.inject.Inject;

import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseViewHolder;

/**
 * Created by fastabler on 2016/12/3.
 */

public class ChannelAdapter extends BaseQuickAdapter<NewsChannelItem, BaseViewHolder> {

    @Inject
    public ChannelAdapter() {
        super(R.layout.item_channel, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsChannelItem item) {
        helper.setText(R.id.news_channel_tv, item.getNewsChannelName());
    }
}
