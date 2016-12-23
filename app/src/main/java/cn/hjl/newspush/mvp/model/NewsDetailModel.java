package cn.hjl.newspush.mvp.model;

import android.content.Context;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.HostType;
import cn.hjl.newspush.mvp.entity.NewsDetail;
import cn.hjl.newspush.repository.network.RetrofitManager;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked._Subscriber;
import cn.hjl.newspush.utils.MyUtils;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fastabler on 2016/12/16.
 */

public class NewsDetailModel {

    @Inject
    public NewsDetailModel(){

    }

    public Subscription getNewsDetail(Context context, final RequestCallBack<NewsDetail> callBack, final String postId){
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsDetailObservable(postId)
                .map(new Func1<Map<String, NewsDetail>, NewsDetail>() {
                    @Override
                    public NewsDetail call(Map<String, NewsDetail> map) {
                        NewsDetail newsDetail = map.get(postId);
                        changeNewsDetail(newsDetail);
                        return newsDetail;
                    }
                })
                .compose(TransformUtils.<NewsDetail>defaultSchedulers())
                .subscribe(new _Subscriber<NewsDetail>(context, true) {
                    @Override
                    protected void _onNext(NewsDetail newsDetail) {
                        callBack.success(newsDetail);
                    }

                    @Override
                    protected void _onError(String message) {
                        callBack.onError(message);

                    }
                });
    }

    private void changeNewsDetail(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        if (isChange(imgSrcs)) {
            String newsBody = newsDetail.getBody();
            newsBody = changeNewsBody(imgSrcs, newsBody);
            newsDetail.setBody(newsBody);
        }
    }

    private boolean isChange(List<NewsDetail.ImgBean> imgSrcs) {
        return imgSrcs != null && imgSrcs.size() >= 2;
    }

    private String changeNewsBody(List<NewsDetail.ImgBean> imgSrcs, String newsBody) {
        for (int i = 0; i < imgSrcs.size(); i++) {
            String oldChars = "<!--IMG#" + i + "-->";
            String newChars;
            if (i == 0) {
                newChars = "";
            } else {
                newChars = "<img src=\"" + imgSrcs.get(i).getSrc() + "\" />";
            }
            newsBody = newsBody.replace(oldChars, newChars);

        }
        return newsBody;
    }
}
