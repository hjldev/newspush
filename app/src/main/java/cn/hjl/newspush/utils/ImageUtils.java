package cn.hjl.newspush.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import cn.hjl.newspush.R;

/**
 * Created by fastabler on 2016/11/10.
 */
public class ImageUtils {
//    public static void showImage(Context context, ImageView view, String url){
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .centerCrop()
//                .error(R.mipmap.ic_launcher)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .format(DecodeFormat.PREFER_ARGB_8888)
//                .placeholder(R.mipmap.ic_launcher)
//                .into(view);
//    }

    public static void showImage(Context context, ImageView view, String url){
        Picasso.with(context).load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(view);
    }
}
