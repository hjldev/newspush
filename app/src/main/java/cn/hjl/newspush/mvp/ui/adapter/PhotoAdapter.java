package cn.hjl.newspush.mvp.ui.adapter;

import javax.inject.Inject;

import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.PhotoGirl;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseViewHolder;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoAdapter extends BaseQuickAdapter<PhotoGirl, BaseViewHolder> {

    @Inject
    public PhotoAdapter(){
        super(R.layout.item_photo, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoGirl item) {
        helper.setNetImage(R.id.photo_iv, item.getUrl());
    }
}
