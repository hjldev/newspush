package cn.hjl.newspush.mvp.ui.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.BindView;
import cn.hjl.newspush.R;
import cn.hjl.newspush.mvp.ui.BaseActivity;

/**
 * Created by fastabler on 2016/12/29.
 */

public class VideoDetailActivity extends BaseActivity {

    @BindView(R.id.video_view)
    VideoView videoView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        Bundle args = getArguments();
        String url = args.getString("url");
        Uri uri = Uri.parse(url);
        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                toastor.showSingletonToast("play completion");
            }
        });

        //设置视频路径
        videoView.setVideoURI(uri);

        //开始播放视频
        videoView.start();

    }
}
