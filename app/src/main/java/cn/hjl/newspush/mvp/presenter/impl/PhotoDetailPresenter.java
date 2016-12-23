package cn.hjl.newspush.mvp.presenter.impl;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import cn.hjl.newspush.App;
import cn.hjl.newspush.R;
import cn.hjl.newspush.callback._RequestCallBack;
import cn.hjl.newspush.mvp.model.PhotoDetailModel;
import cn.hjl.newspush.mvp.presenter.BasePresenterImpl;
import cn.hjl.newspush.mvp.view.BaseView;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoDetailPresenter extends BasePresenterImpl<BaseView> {

    private PhotoDetailModel detailModel;

    @Inject
    public PhotoDetailPresenter(PhotoDetailModel detailModel){
        this.detailModel = detailModel;
    }

    public void share(String imageUrl) {
        detailModel.saveImageAndGetUri(mContext, imageUrl, new _RequestCallBack<Uri>(mView) {
            @Override
            public void _success(Uri data) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, data);
                intent.setType("image/jpeg");
                mContext.startActivity(Intent.createChooser(intent, App.getInstance().getString(R.string.action_share)));
            }
        });

    }

    public void savePicture(String imageUrl){
        detailModel.saveImageAndGetUri(mContext, imageUrl, new _RequestCallBack<Uri>(mView) {
            @Override
            public void _success(Uri data) {
                mView.showMsg(mContext.getResources().getString(R.string.picture_already_save_to, data.getPath()));
            }
        });
    }

    public void setWallpager(String imageUrl){
        detailModel.saveImageAndGetUri(mContext, imageUrl, new _RequestCallBack<Uri>(mView) {
            @Override
            public void _success(Uri imageUri) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    File wallpaperFile = new File(imageUri.getPath());
                    Uri contentURI = getImageContentUri(mContext, wallpaperFile.getAbsolutePath());
//                    Uri uri1 = getImageContentUri(mActivity, imageUri.getPath());
                    mContext.startActivity(wallpaperManager.getCropAndSetWallpaperIntent(contentURI));
                } else {
                    try {
                        wallpaperManager.setStream(mContext.getContentResolver().openInputStream(imageUri));
                        mView.showMsg(App.getInstance().getString(R.string.set_wallpaper_success));
                    } catch (IOException e) {
                        mView.showMsg(e.getMessage());
                    }
                }
            }
        });

    }

    // http://stackoverflow.com/questions/23207604/get-a-content-uri-from-a-file-uri
    public Uri getImageContentUri(Context context, String absPath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }
}
