package cn.hjl.newspush.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.callback.RequestCallBack;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.rxpacked._Subscriber;
import cn.hjl.newspush.utils.Log;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoDetailModel {
    @Inject
    public PhotoDetailModel(){

    }

    public Subscription saveImageAndGetUri(final Context context, final String url, final RequestCallBack callBack){
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(context)
                            .load(url)
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                if (bitmap == null) {
                    subscriber.onError(new Exception("下载图片失败"));
                }
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        }).flatMap(new Func1<Bitmap, Observable<Uri>>() {
            @Override
            public Observable<Uri> call(Bitmap bitmap) {
                return getUriObservable(bitmap, url);
            }
        }).compose(TransformUtils.<Uri>defaultSchedulers())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(context.getResources().getString(R.string.error_try_again));
                    }

                    @Override
                    public void onNext(Uri uri) {
                        callBack.success(uri);
                    }
                });
    }

    @NotNull
    private Observable<Uri> getUriObservable(Bitmap bitmap, String url){
        File file = getImageFile(bitmap, url);
        if (file == null){
            Observable.error(new NullPointerException("Save image file failed!"));
        }
        Uri uri = Uri.fromFile(file);
        // 通知图库更新 //Update the System --> MediaStore.Images.Media --> 更新ContentUri
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        App.getInstance().sendBroadcast(scannerIntent);
        return Observable.just(uri);
    }

    private File getImageFile(Bitmap bitmap, String url){
        String fileName = Constants.SAVE_FILE_IMAGE_URL + url.hashCode() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        // 判断路径是否存在，不存在则创建
        if (!file.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return file;

    }
}
