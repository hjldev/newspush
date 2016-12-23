package cn.hjl.newspush.mvp.model;

import android.content.Context;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.ApiConstants;
import cn.hjl.newspush.mvp.entity.DaoSession;
import cn.hjl.newspush.mvp.entity.NewsChannelItem;
import cn.hjl.newspush.mvp.entity.NewsChannelItemDao;
import cn.hjl.newspush.mvp.model.base.SubChannelInteractor;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked._Subscriber;
import cn.hjl.newspush.utils.Log;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by fastabler on 2016/11/8.
 * 用于数据库对操作
 */
public class SubChannelModel implements SubChannelInteractor<List<NewsChannelItem>> {

    private DaoSession daoSession;
    private RxQuery<NewsChannelItem> channelSubQuery;
    private RxQuery<NewsChannelItem> channelUnSubQuery;
    private NewsChannelItemDao channelDao;

    @Inject
    public SubChannelModel() {
        daoSession = App.getInstance().getDaoSession();
        channelDao = daoSession.getNewsChannelItemDao();
        channelSubQuery = daoSession.getNewsChannelItemDao().queryBuilder().where(NewsChannelItemDao.Properties.NewsChannelSelect.eq(true)).orderAsc(NewsChannelItemDao.Properties.NewsChannelIndex).rx();
        channelUnSubQuery = daoSession.getNewsChannelItemDao().queryBuilder().where(NewsChannelItemDao.Properties.NewsChannelSelect.eq(false)).orderAsc(NewsChannelItemDao.Properties.NewsChannelIndex).rx();
    }

    /**
     * 数据库的初始化操作，index小的在前面。订阅当最小数
     * @param callback
     * @return
     */
    @Override
    public Subscription lodeNewsChannels(final RequestCallBack<List<NewsChannelItem>> callback) {


        final List<String> channelName = Arrays.asList(App.getInstance().getResources()
                .getStringArray(R.array.news_channel_name));
        final List<String> channelId = Arrays.asList(App.getInstance().getResources()
                .getStringArray(R.array.news_channel_id));
        return channelSubQuery.list()
                .map(new Func1<List<NewsChannelItem>, List<NewsChannelItem>>() {
                    @Override
                    public List<NewsChannelItem> call(List<NewsChannelItem> newsChannelItems) {
                        if (null == newsChannelItems || newsChannelItems.size() == 0){
                            List<NewsChannelItem> channels = new ArrayList<NewsChannelItem>();
                            for (int i = 0; i < channelName.size(); i++) {
                                NewsChannelItem entity = new NewsChannelItem(channelName.get(i), channelId.get(i)
                                        , ApiConstants.getType(channelId.get(i)), i <= 5, i, i == 0);
                                channelDao.insert(entity);
                                if(entity.getNewsChannelSelect()){
                                    channels.add(entity);
                                }
                            }
                            return channels;
                        }
                        return newsChannelItems;
                    }
                })
                .compose(TransformUtils.<List<NewsChannelItem>>defaultSchedulers())
                .subscribe(new Subscriber<List<NewsChannelItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(App.getInstance().getString(R.string.db_error));
                    }

                    @Override
                    public void onNext(List<NewsChannelItem> newsChannelItems) {
                        callback.success(newsChannelItems);
                    }
                });
    }

    @Override
    public Subscription lodeUnSubChannels(final RequestCallBack<List<NewsChannelItem>> callback) {
        daoSession = App.getInstance().getDaoSession();


        return channelUnSubQuery.list()
                .compose(TransformUtils.<List<NewsChannelItem>>defaultSchedulers())
                .subscribe(new Subscriber<List<NewsChannelItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(App.getInstance().getString(R.string.db_error));
                    }

                    @Override
                    public void onNext(List<NewsChannelItem> newsChannelItems) {
                        callback.success(newsChannelItems);
                    }
                });
    }

    public Subscription subDb(final RequestCallBack<List<NewsChannelItem>> callback, Context context, final List<NewsChannelItem>... subItems){
        return Observable.concat(Observable.from(subItems[0]), Observable.from(subItems[1]))
                .toList()
                .map(new Func1<List<NewsChannelItem>, List<NewsChannelItem>>() {
                    @Override
                    public List<NewsChannelItem> call(List<NewsChannelItem> newsChannelItems) {
                        for (int i = 0; i < newsChannelItems.size(); i++){
                            newsChannelItems.get(i).setNewsChannelIndex(i);
                        }
                        channelDao.insertOrReplaceInTx(newsChannelItems);
                        return newsChannelItems;
                    }
                })
                .compose(TransformUtils.<List<NewsChannelItem>>defaultSchedulers())
                .subscribe(new _Subscriber<List<NewsChannelItem>>(context) {

                    @Override
                    protected void _onNext(List<NewsChannelItem> newsChannelItems) {

                    }

                    @Override
                    protected void _onError(String message) {

                    }
                });

    }


}
