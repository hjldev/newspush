package cn.hjl.newspush.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;

/**
 * Created by a2437 on 2016/9/20.
 * 主要是个人信息配置的一些东西
 */
public class MyUtils {

    // 是否是夜间模式
    public static boolean isNightMode(Context context){
        return (boolean) SPUtils.get(context, Constants.NIGHT_THEME_MODE, false);
    }
    // 设置夜间模式
    public static void setNightMode(Context context, boolean isNightMode){
        SPUtils.put(context, Constants.NIGHT_THEME_MODE, isNightMode);
    }

    // 是否初始化了频道数据
    public static boolean isInitChannelDb(Context context){
        return (boolean) SPUtils.get(context, Constants.IS_INIT_CHANNEL_DB, false);
    }
    // 设置初始化成功
    public static void InitChannelDb(Context context){
        SPUtils.put(context, Constants.IS_INIT_CHANNEL_DB, true);
    }

    /**
     * from yyyy-MM-dd HH:mm:ss to MM-dd HH:mm
     */
    public static String formatDate(String before) {
        String after;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(before);
            after = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            Log.e("转换新闻日期格式异常：" + e.toString());
            return before;
        }
        return after;
    }

    /**
     * 解决InputMethodManager内存泄露现象
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f;
        Object obj_get;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get
                            .getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        /*if (QLog.isColorLevel()) {
                            QLog.d(ReflecterHelper.class.getSimpleName(), QLog.CLR, "fixInputMethodManagerLeak break, context is not suitable, get_context=" + v_get.getContext()+" dest_context=" + destContext);
                        }*/
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static String analyzeNetworkError(Throwable e) {
        String errMsg = App.getInstance().getString(R.string.load_error);
        if (e instanceof HttpException) {
            int state = ((HttpException) e).code();
            if (state == 403) {
                errMsg = App.getInstance().getString(R.string.load_failed);
            }
        }
        return errMsg;
    }

    public static void callAllSubscription(List<Subscription> subscriptions){
        if (subscriptions != null && subscriptions.size() > 0){
            for (Subscription subscription : subscriptions){
                if (subscription != null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
            }
        }
    }

    public static int getColor(int nightColor, int dayColor) {
        int color;
        if (!MyUtils.isNightMode(App.getInstance())) {
            color = nightColor;
        } else {
            color = dayColor;
        }
        return color;
    }
}
