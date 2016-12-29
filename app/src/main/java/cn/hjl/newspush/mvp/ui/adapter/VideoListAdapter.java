package cn.hjl.newspush.mvp.ui.adapter;

import javax.inject.Inject;

import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.VideoData;
import cn.hjl.newspush.mvp.model.VideoModel;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseViewHolder;
import cn.hjl.newspush.utils.ImageUtils;

/**
 * Created by fastabler on 2016/12/25.
 */

public class VideoListAdapter extends BaseQuickAdapter<VideoData, BaseViewHolder> {

    @Inject
    public VideoListAdapter(){
        super(R.layout.item_video_list, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoData item) {
        helper.setNetImage(R.id.video_cover_iv, item.getCover())
                .setText(R.id.video_title_tv, item.getTitle())
                .setText(R.id.video_duration_tv, item.getSectiontitle());
    }
}
