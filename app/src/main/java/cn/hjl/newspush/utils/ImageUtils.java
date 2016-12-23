package cn.hjl.newspush.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.hjl.newspush.R;

/**
 * Created by fastabler on 2016/11/10.
 */
public class ImageUtils {
    public static void showImage(Context context, ImageView view, String url){
        Glide.with(context)
                .load(url)
                .asBitmap()
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }
}
