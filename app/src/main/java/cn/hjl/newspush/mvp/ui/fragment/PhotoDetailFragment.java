package cn.hjl.newspush.mvp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hjl.newspush.R;
import cn.hjl.newspush.common.Constants;
import cn.hjl.newspush.mvp.ui.BaseFragment;
import cn.hjl.newspush.rxpacked.TransformUtils;
import cn.hjl.newspush.utils.ImageUtils;
import rx.Observable;
import rx.Subscriber;
import uk.co.senab.photoview.PhotoView;

public class PhotoDetailFragment extends BaseFragment {

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;

    private String mImgSrc;

    public static PhotoDetailFragment newsInstance(String imgSrc){
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PHOTO_DETAIL_IMGSRC, imgSrc);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgSrc = getArguments().getString(Constants.PHOTO_DETAIL_IMGSRC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initViews(view);
        return view;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initViews(View view) {
        initPhotoView();
        setPhotoViewClickEvent();
    }

    private void initPhotoView() {
        mSubscriptions.add(Observable.timer(100, TimeUnit.MILLISECONDS) // 直接使用glide加载的话，activity切换动画时背景短暂为默认背景色
                .compose(TransformUtils.<Long>defaultSchedulers())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
//                        Glide.with(App.getInstance()).load(mImgSrc).asBitmap()
//                                .format(DecodeFormat.PREFER_ARGB_8888)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(mPhotoView);
                        ImageUtils.showImage(getContext(), mPhotoView, mImgSrc);

                    }
                }));
    }

    private void setPhotoViewClickEvent() {
//        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float v, float v1) {
//                handleOnTabEvent();
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//                handleOnTabEvent();
//            }
//        });
    }

    private void handleOnTabEvent() {
//        RxBus.getDefault().post(new PhotoDetailOnClickEvent());
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news_photo_detail;
    }

}
