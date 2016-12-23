package cn.hjl.newspush.repository.network;

import java.util.List;
import java.util.Map;

import cn.hjl.newspush.mvp.entity.NewsDetail;
import cn.hjl.newspush.mvp.entity.NewsSummary;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by fastabler on 2016/11/9.
 */
public interface NewsService {
    @GET("nc/article/{type}/{id}/{startpage}-20.html")
    Observable<Map<String, List<NewsSummary>>> getNewsList(@Header("Cache-Control") String cacheControl,
                                                           @Path("type") String type,
                                                           @Path("id") String id,
                                                           @Path("startpage") int startpage);

    @GET("nc/article/{postId}/full.html")
    Observable<Map<String, NewsDetail>> getNewDetail(
            @Header("Cache-Control") String cacheControl,
            @Path("postId") String postId);

    @GET
    Observable<ResponseBody> getNewsBodyHtmlPhoto(
            @Url String photoPath);
    //@Url，它允许我们直接传入一个请求的URL。这样以来我们可以将上一个请求的获得的url直接传入进来，baseUrl将被无视
    // baseUrl 需要符合标准，为空、""、或不合法将会报错

//    @GET("data/福利/{size}/{page}")
//    Observable<GirlData> getPhotoList(
//            @Path("size") int size,
//            @Path("page") int page);
}
