package cn.hjl.newspush.mvp.model;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.HostType;
import cn.hjl.newspush.mvp.entity.VideoData;
import cn.hjl.newspush.repository.network.RetrofitManager;
import cn.hjl.newspush.rxpacked.TransformUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fastabler on 2016/12/29.
 */

public class VideoListModel {

    @Inject
    public VideoListModel(){

    }

    public Subscription getVideoList(final String videoType, int startPage, final RequestCallBack<List<VideoData>> callBack) {
        return RetrofitManager.getInstance(HostType.VIDEO_HOST)
                .getVideoList(videoType, startPage)
                .flatMap(new Func1<Map<String, List<VideoData>>, Observable<VideoData>>() {
                    @Override
                    public Observable<VideoData> call(Map<String, List<VideoData>> stringListMap) {
                        return Observable.from(stringListMap.get(videoType));
                    }
                })
                .map(new Func1<VideoData, VideoData>() {
                    @Override
                    public VideoData call(VideoData videoData) {
                        String videoLength = getLengthStr(videoData.getLength());
                        videoData.setSectiontitle(videoLength);
                        return videoData;
                    }
                })
                .toList()
                .compose(TransformUtils.<List<VideoData>>defaultSchedulers())
                .subscribe(new Subscriber<List<VideoData>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("--------------------- onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("--------------------- onError:"+e.getMessage());
                        callBack.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<VideoData> videoDatas) {
                        System.out.println("--------------------- onNext:"+videoDatas.size());
                        for (VideoData v:videoDatas) {
                            System.out.println(v.getCover() +" ,"+v.getTitle());
                        }
                        callBack.success(videoDatas);
                    }
                });

    }

    public static String getLengthStr(long length) {
        int hour = (int) (length / (60 * 60));
        int hourLast = (int) (length % (60 * 60));
        int minute = hourLast / 60;
        int second = hourLast % 60;
        StringBuffer sb = new StringBuffer();
        if(hour != 0){
            sb.append(zeroize(hour));
            sb.append(":");
        }

        if(minute != 0){
            sb.append(zeroize(minute));
            sb.append(":");
        }else{
            sb.append("00:");
        }

        if(second != 0){
            sb.append(zeroize(second));
        }else{
            sb.append("00");
        }

        return sb.toString();
    }

    public static String zeroize(int time){
        if(time < 10){
            return "0" + time ;
        }
        return String.valueOf(time);
    }
}
