package cn.hjl.newspush.common;

/**
 * Created by a2437 on 2016/9/20.
 * 配置个人的一些常量
 */
public class Constants {
    // 是否是夜间模式
    public static final String NIGHT_THEME_MODE = "night_theme_mode";
    // 是否初始化了频道数据库
    public static final String IS_INIT_CHANNEL_DB = "init_channel_db";

    // activity间传递的key
    public static final String ACTIVITY_ARGS = "args";

    // 频道数据库
    public static final String DB_NAME = "NewsChannelTable";

    public static final String NEWS_ID = "news_id";
    public static final String NEWS_TYPE = "news_type";
    public static final String CHANNEL_POSITION = "channel_position";

    // 跳转详情
    public static final String NEWS_POST_ID = "news_post_id";
    public static final String NEWS_IMG_RES = "news_img_res";

    public static final String PHOTO_DETAIL = "photo_detail";
    public static final String PHOTO_DETAIL_IMGSRC = "photo_detail_imgsrc";

    // 跳转动画的标识
    public static final String TRANSITION_ANIMATION_NEWS_PHOTOS = "transition_animation_news_photos";


    public static final String PHOTO_DETAIL_URL = "photo_detail_url";

    public static final String SAVE_FILE_IMAGE_URL = "/news_push/photo/";
}
