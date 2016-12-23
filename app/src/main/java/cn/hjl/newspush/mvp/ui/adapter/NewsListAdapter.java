package cn.hjl.newspush.mvp.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseMultiItemQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseQuickAdapter;
import cn.hjl.newspush.mvp.ui.adapter.base.BaseViewHolder;
import cn.hjl.newspush.mvp.ui.fragment.NewsListFragment;
import cn.hjl.newspush.utils.DensityUtils;
import cn.hjl.newspush.utils.ScreenUtils;

/**
 * Created by fastabler on 2016/11/9.
 */
public class NewsListAdapter extends BaseMultiItemQuickAdapter<NewsSummary, BaseViewHolder> {

    private int PhotoThreeHeight;
    private int PhotoTwoHeight;
    private int PhotoOneHeight;

    @Inject
    public NewsListAdapter() {
        super(null);
        PhotoThreeHeight = (int) DensityUtils.dp2px(90);
        PhotoTwoHeight = (int) DensityUtils.dp2px(120);
        PhotoOneHeight = (int) DensityUtils.dp2px(150);
        addItemType(NewsSummary.TEXT, R.layout.item_news);
        addItemType(NewsSummary.IMG, R.layout.item_news_photo);
//        super(R.layout.item_news, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsSummary item) {
        switch (helper.getItemViewType()){
            case NewsSummary.TEXT:
                String title = item.getLtitle();
                if (title == null) {
                    title = item.getTitle();
                }
                helper.setText(R.id.news_summary_title_tv, title)
                        .setNetImage(R.id.news_summary_photo_iv, item.getImgsrc())
                        .setText(R.id.news_summary_digest_tv, item.getDigest())
                        .setText(R.id.news_summary_ptime_tv, item.getPtime());
                break;
            case NewsSummary.IMG:

                helper.setText(R.id.news_summary_title_tv, item.getTitle())
                        .setText(R.id.news_summary_ptime_tv, item.getPtime());
                ViewGroup.LayoutParams layoutParams = helper.getView(R.id.news_summary_photo_iv_group).getLayoutParams();

                String leftSrc = null;
                String middleSrc = null;
                String rightSrc = null;
                if (item.getAds() != null){
                    List<NewsSummary.AdsBean> adsBeanList = item.getAds();
                    int size = adsBeanList.size();
                    if (size >=3){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                        middleSrc = adsBeanList.get(1).getImgsrc();
                        rightSrc = adsBeanList.get(2).getImgsrc();
                        helper.setText(R.id.news_summary_title_tv, App.getInstance().getString(R.string.photo_collections, adsBeanList.get(0).getTitle()));
                        layoutParams.height = PhotoThreeHeight;
                    } else if (size >= 2){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                        middleSrc = adsBeanList.get(1).getImgsrc();
                        layoutParams.height = PhotoTwoHeight;
                    } else if (size >= 1){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                        layoutParams.height = PhotoOneHeight;
                    }

                } else if (item.getImgextra() != null){
                    List<NewsSummary.ImgextraBean> adsBeanList = item.getImgextra();
                    int size = adsBeanList.size();
                    if (size >=3){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                        middleSrc = adsBeanList.get(1).getImgsrc();
                        rightSrc = adsBeanList.get(2).getImgsrc();
                        layoutParams.height = PhotoThreeHeight;
                    } else if (size >= 2){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                        middleSrc = adsBeanList.get(1).getImgsrc();
                        layoutParams.height = PhotoTwoHeight;
                    } else if (size >= 1){
                        leftSrc = adsBeanList.get(0).getImgsrc();
                    }
                } else {
                    leftSrc = item.getImgsrc();

                    layoutParams.height = PhotoOneHeight;
                }

                setPhotoImageView(helper, leftSrc, middleSrc, rightSrc);
                helper.getView(R.id.news_summary_photo_iv_group).setLayoutParams(layoutParams);

                break;
        }


    }

    private void setPhotoImageView(BaseViewHolder helper, String leftSrc, String middleSrc, String rightSrc) {
        if (leftSrc != null){
            helper.setNetImage(R.id.news_summary_photo_iv_left, leftSrc);
        } else {
            hidePhoto(helper, R.id.news_summary_photo_iv_left);
        }
        if (middleSrc != null){
            helper.setNetImage(R.id.news_summary_photo_iv_middle, middleSrc);
        } else {
            hidePhoto(helper, R.id.news_summary_photo_iv_middle);
        }
        if (rightSrc != null){
            helper.setNetImage(R.id.news_summary_photo_iv_right, rightSrc);
        } else {
            hidePhoto(helper, R.id.news_summary_photo_iv_right);
        }
    }

    private void hidePhoto(BaseViewHolder helper, int imgId) {
        helper.getView(imgId).setVisibility(View.GONE);
    }
}
