package cn.hjl.newspush.mvp.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.ApiConstants;
import cn.hjl.newspush.common.HostType;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import cn.hjl.newspush.mvp.model.base.NewsListInteractor;
import cn.hjl.newspush.repository.network.RetrofitManager;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked._Subscriber;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.utils.MyUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by fastabler on 2016/11/9.
 * 用于对首页列表请求数据对操作
 */
public class NewsListModel implements NewsListInteractor<List<NewsSummary>> {
    @Inject
    public NewsListModel(){

    }
    @Override
    public Subscription loadNewsList(Context context, String type, final String id, int startPage, final RequestCallBack<List<NewsSummary>> listener) {
            return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsListObservable(type, id, startPage)
                    .flatMap(new Func1<Map<String, List<NewsSummary>>, Observable<NewsSummary>>() {
                        @Override
                        public Observable<NewsSummary> call(Map<String, List<NewsSummary>> map) {
                            if (id.endsWith(ApiConstants.HOUSE_ID)) {
                                // 房产实际上针对地区的它的id与返回key不同
                                return Observable.from(map.get("北京"));
                            }
                            return Observable.from(map.get(id));
                        }
                    })
                    .map(new Func1<NewsSummary, NewsSummary>() {
                        @Override
                        public NewsSummary call(NewsSummary newsSummary) {
                            String ptime = MyUtils.formatDate(newsSummary.getPtime());
                            newsSummary.setPtime(ptime);
                            return newsSummary;
                        }
                    })
//                .toList()
                    .distinct()
                    .toSortedList(new Func2<NewsSummary, NewsSummary, Integer>() {
                        @Override
                        public Integer call(NewsSummary newsSummary, NewsSummary newsSummary2) {
                            return newsSummary2.getPtime().compareTo(newsSummary.getPtime());
                        }
                    })
                    .compose(TransformUtils.<List<NewsSummary>>defaultSchedulers())
                    .subscribe(new _Subscriber<List<NewsSummary>>(context, true) {
                        @Override
                        protected void _onNext(List<NewsSummary> newsSummaries) {
                            listener.success(newsSummaries);
                        }

                        @Override
                        protected void _onError(String message) {
                            listener.onError(message);
                        }

                        @Override
                        public void unSubscribe() {
                            super.unSubscribe();
                            listener.onError("取消网络请求");
                        }
                    });


    }
}
