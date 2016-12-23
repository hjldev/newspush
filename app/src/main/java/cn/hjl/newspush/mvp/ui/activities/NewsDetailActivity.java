package cn.hjl.newspush.mvp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.mvp.entity.NewsDetail;
import cn.hjl.newspush.mvp.presenter.impl.NewsDetailPresenter;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.mvp.view.NewsDetailView;
import cn.hjl.newspush.utils.ImageUtils;
import cn.hjl.newspush.utils.MyUtils;
import cn.hjl.newspush.widget.URLImageGetter;

/**
 * Created by fastabler on 2016/12/12.
 */

public class NewsDetailActivity extends BaseActivity implements NewsDetailView{

    @BindView(R.id.news_detail_photo_iv)
    ImageView mNewsDetailPhotoIv;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.news_detail_from_tv)
    TextView mNewsDetailFromTv;
    @BindView(R.id.news_detail_body_tv)
    TextView mNewsDetailBodyTv;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Inject
    NewsDetailPresenter mNewsDetailPresenter;

    private String mNewsId;
    private String mNewsRes;

    private String mNewsTitle;
    private String mShareLink;

    private URLImageGetter mUrlImageGetter;

    @Override
    public int getLayoutId() {
        return  R.layout.activity_news_detail;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {
        Bundle bundle = getArguments();
        mNewsId = bundle.getString(Constants.NEWS_POST_ID);
        mNewsRes = bundle.getString(Constants.NEWS_IMG_RES);

        mPresenter = mNewsDetailPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public void setNewsDetail(NewsDetail newsDetail) {
        mShareLink = newsDetail.getShareLink();
        mNewsTitle = newsDetail.getTitle();
        String newsSource = newsDetail.getSource();
        String newsTime = MyUtils.formatDate(newsDetail.getPtime());
        String newsImgSrc = getImgSrcs(newsDetail);


        setToolBarLayout(mNewsTitle);
        mNewsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));
        ImageUtils.showImage(this, mNewsDetailPhotoIv, newsImgSrc);
        setBody(newsDetail);
    }

    private void setBody(NewsDetail newsDetail) {
        int imgTotal = newsDetail.getImg().size();
        String newsBody = newsDetail.getBody();
        if (isShowBody(newsBody, imgTotal)) {
            mUrlImageGetter = new URLImageGetter(mNewsDetailBodyTv, newsBody, imgTotal);
            mNewsDetailBodyTv.setText(Html.fromHtml(newsBody, mUrlImageGetter, null));
        } else {
            mNewsDetailBodyTv.setText(Html.fromHtml(newsBody));
        }
    }

    private boolean isShowBody(String newsBody, int imgTotal) {
        return imgTotal >= 2 && newsBody != null;
    }

    @Override
    public String getPostId() {
        return mNewsId;
    }

    private String getImgSrcs(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        String imgSrc;
        if (imgSrcs != null && imgSrcs.size() > 0) {
            imgSrc = imgSrcs.get(0).getSrc();
        } else {
            imgSrc = getIntent().getStringExtra(Constants.NEWS_IMG_RES);
        }
        return imgSrc;
    }

    private void setToolBarLayout(String newsTitle) {
        mToolbarLayout.setTitle(newsTitle);
        mToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.color_white));
        mToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }

    @Override
    protected void onDestroy() {
        cancelUrlImageGetterSubscription();
        super.onDestroy();

    }

    private void cancelUrlImageGetterSubscription() {
        try {
            if (mUrlImageGetter != null && mUrlImageGetter.mSubscription != null
                    && !mUrlImageGetter.mSubscription.isUnsubscribed()) {
                mUrlImageGetter.mSubscription.unsubscribe();
            }
        } catch (Exception e) {
        }
    }
}
