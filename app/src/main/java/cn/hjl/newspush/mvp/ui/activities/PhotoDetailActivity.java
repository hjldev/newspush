package cn.hjl.newspush.mvp.ui.activities;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.mvp.ui.BaseActivity;
import cn.hjl.newspush.utils.ImageUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by fastabler on 2016/12/23.
 */

public class PhotoDetailActivity extends BaseActivity {

    @BindView(R.id.photo_view)
    PhotoView photoView;


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
        setTitle(R.string.action_pic);
        Bundle args = getArguments();
        if (args != null){
            String url = args.getString(Constants.PHOTO_DETAIL_URL);
            ImageUtils.showImage(this, photoView, url);
        }
    }
}
