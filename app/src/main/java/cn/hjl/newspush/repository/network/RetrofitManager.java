package cn.hjl.newspush.repository.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hjl.newspush.App;
import cn.hjl.newspush.common.ApiConstants;
import cn.hjl.newspush.common.HostType;
import cn.hjl.newspush.mvp.entity.GirlData;
import cn.hjl.newspush.mvp.entity.NewsDetail;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.utils.NetUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by fastabler on 2016/11/9.
 */
public class RetrofitManager {
    private NewsService mNewsService;
    /**
     * 设缓存有效期为一个月
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 30;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";

    private static volatile OkHttpClient mOkHttpClient;

    private static SparseArray<RetrofitManager> mRetrofitManager = new SparseArray<>(HostType.TYPE_COUNT);

    public RetrofitManager(@HostType.HostTypeChecker int hostType){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiConstants.getHost(hostType))
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mNewsService = retrofit.create(NewsService.class);

    }

    private OkHttpClient getOkHttpClient(){
        if (mOkHttpClient == null){
            synchronized (RetrofitManager.class){
                if (mOkHttpClient == null){
                    Cache cache = new Cache(new File(App.getInstance().getCacheDir(), "HttpCache"), 1024*1024*10);
                    mOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .readTimeout(6, TimeUnit.SECONDS)
                            .writeTimeout(6, TimeUnit.SECONDS)
                            .connectTimeout(6, TimeUnit.SECONDS)
//                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mLoggingInterceptor)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    private final Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
//            long t1 = System.nanoTime();
//            Log.i(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
//            long t2 = System.nanoTime();
//            Log.i(String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            try
            {
                //===>response log
                Log.i("========response'log=======");
                Response.Builder builder = response.newBuilder();
                Response clone = builder.build();
                Log.i("url : " + clone.request().url());
                Log.i("code : " + clone.code());
                Log.i("protocol : " + clone.protocol());
                if (!TextUtils.isEmpty(clone.message()))
                    Log.i("message : " + clone.message());
                    ResponseBody body = clone.body();
                    if (body != null)
                    {
                        MediaType mediaType = body.contentType();
                        if (mediaType != null)
                        {
                            Log.e("responseBody's contentType : " + mediaType.toString());
                            if (isText(mediaType))
                            {
                                String resp = body.string();
                                Log.i("responseBody's content : " + resp);

                                body = ResponseBody.create(mediaType, resp);
                                return response.newBuilder().body(body).build();
                            } else
                            {
                                Log.i( "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                            }
                        }
                    }

                Log.i("========response'log=======end");
            } catch (Exception e)
            {
//            e.printStackTrace();
            }
            return response;
        }
    };

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetUtils.isConnected(App.getInstance())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.d("no network");
            }
            Response originalResponse = chain.proceed(request);
            if (NetUtils.isConnected(App.getInstance())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };


    /**
     * @param hostType NETEASE_NEWS_VIDEO：1 （新闻，视频），GANK_GIRL_PHOTO：2（图片新闻）;
     *                 EWS_DETAIL_HTML_PHOTO:3新闻详情html图片)
     */
    public static RetrofitManager getInstance(int hostType) {
        RetrofitManager retrofitManager = mRetrofitManager.get(hostType);
        if (retrofitManager == null) {
            retrofitManager = new RetrofitManager(hostType);
            mRetrofitManager.put(hostType, retrofitManager);
            return retrofitManager;
        }
        return retrofitManager;
    }

    /**
     * 根据网络状况获取缓存的策略
     */
    @NonNull
    private String getCacheControl() {
        return NetUtils.isConnected(App.getInstance()) ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
    }

    /**
     * example：http://c.m.163.com/nc/article/headline/T1348647909107/0-20.html
     *
     * @param newsType ：headline为头条,house为房产，list为其他
     */
    public Observable<Map<String, List<NewsSummary>>> getNewsListObservable(
            String newsType, String newsId, int startPage) {
        return mNewsService.getNewsList(getCacheControl(), newsType, newsId, startPage);
    }


    /**
     * example：http://c.m.163.com/nc/article/BG6CGA9M00264N2N/full.html
     */
    public Observable<Map<String, NewsDetail>> getNewsDetailObservable(String postId) {

        return mNewsService.getNewDetail(getCacheControl(), postId);
    }

    public Observable<ResponseBody> getNewsBodyHtmlPhoto(String photoPath) {
        return mNewsService.getNewsBodyHtmlPhoto(photoPath);
    }

    public Observable<GirlData> getPhotoListObservable(int size, int page) {
        return mNewsService.getPhotoList(size, page);
    }
}
