package cn.hjl.newspush.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fastabler on 2016/11/9.
 */
public class LoadStatusType {
    public static final int TYPE_REFRESH_SUCCESS = 1;
    public static final int TYPE_REFRESH_ERROR = 2;
    public static final int TYPE_REFRESH_EMPTY = 3;
    public static final int TYPE_REFRESH_COMPLETE = 4;


    @IntDef({TYPE_REFRESH_SUCCESS, TYPE_REFRESH_ERROR, TYPE_REFRESH_EMPTY, TYPE_REFRESH_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusType {
    }

}
