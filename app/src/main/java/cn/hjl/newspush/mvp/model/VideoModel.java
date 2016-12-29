package cn.hjl.newspush.mvp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.ApiConstants;
import cn.hjl.newspush.mvp.entity.VideoChannel;
import cn.hjl.newspush.rxpacked.TransformUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by fastabler on 2016/12/24.
 */

public class VideoModel {

    @Inject
    public VideoModel(){

    }

    public Subscription getVideoChannelList(Context context, final RequestCallBack callBack){
        return Observable.create(new Observable.OnSubscribe<List<VideoChannel>>() {

            @Override
            public void call(Subscriber<? super List<VideoChannel>> subscriber) {
                List<VideoChannel> videoChannelList = new ArrayList<>();
                videoChannelList.add(new VideoChannel("热点", ApiConstants.VIDEO_HOT_ID));
                videoChannelList.add(new VideoChannel("娱乐", ApiConstants.VIDEO_ENTERTAINMENT_ID));
                videoChannelList.add(new VideoChannel("搞笑", ApiConstants.VIDEO_FUN_ID));
//                videoChannelList.add(new VideoChannel("精品", ApiConstants.VIDEO_CHOICE_ID));
                subscriber.onNext(videoChannelList);
                subscriber.onCompleted();
            }
        })
                .compose(TransformUtils.<List<VideoChannel>>defaultSchedulers())
                .subscribe(new Subscriber<List<VideoChannel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<VideoChannel> videoChannels) {
                        callBack.success(videoChannels);
                    }
                });
    }
}
