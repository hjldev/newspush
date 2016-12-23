package cn.hjl.newspush.mvp.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.mvp.presenter.impl.PhotoDetailPresenter;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.utils.ImageUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoDetailActivity extends BaseActivity {

    @BindView(R.id.photo_view)
    PhotoView photoView;
    @Inject
    PhotoDetailPresenter presenter;



    private String url = "";


    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_detail;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initViews() {
        mPresenter = presenter;
        mPresenter.attachView(this);
        setTitle(R.string.action_pic);
        Bundle args = getArguments();
        if (args != null){
            url = args.getString(Constants.PHOTO_DETAIL_URL);
            ImageUtils.showImage(this, photoView, url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                presenter.share(url);
                break;
            case R.id.action_save:
                presenter.savePicture(url);
                break;
            case R.id.action_set_wallpaper:
                presenter.setWallpager(url);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
