package top.cnzrg.lingyarn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.Serializable;
import java.util.ArrayList;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.domain.MediaItem;
import top.cnzrg.lingyarn.util.LogUtil;
import top.cnzrg.lingyarn.util.TimeUtils;
import top.cnzrg.lingyarn.util.ToastUtils;

/**
 * FileName: SystemVideoPlayer
 * Author: ZRG
 * Date: 2020/11/8 13:32
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final String TAG = SystemVideoPlayer.class.getSimpleName();
    private VideoView videoview;
    private Uri uri;

    private LinearLayout llPlayerStatus;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btVoice;
    private SeekBar seekbarVoice;
    private Button btSwitchPlayer;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvEndTime;
    private LinearLayout llPlayerController;
    private Button btExit;
    private Button btPre;
    private Button btStartPause;
    private Button btNext;
    private Button btSwitchFull;

    private TimeUtils utils;

    private ArrayList<MediaItem> mediaItems;
    private int position;

    /**
     * 监听电量变化
     */
    private MyReceiver receiver;

    // 视频播放进度
    private final static int PROGRESS = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS:

                    // 得到当前的视频播放进度
                    int currentPosition = videoview.getCurrentPosition();

                    // SeekBar.setProgress(当前进度)
                    seekbarVideo.setProgress(currentPosition);

                    // 更新时间文本
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    // 更新系统时间
                    tvSystemTime.setText(TimeUtils.getSystemTime());

                    // 每秒更新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };

    private void findViews() {
        llPlayerStatus = findViewById(R.id.ll_player_status);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btVoice = (Button) findViewById(R.id.bt_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btSwitchPlayer = (Button) findViewById(R.id.bt_switch_player);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        llPlayerController = (LinearLayout) findViewById(R.id.ll_player_controller);
        btExit = (Button) findViewById(R.id.bt_exit);
        btPre = (Button) findViewById(R.id.bt_pre);
        btStartPause = (Button) findViewById(R.id.bt_start_pause);
        btNext = (Button) findViewById(R.id.bt_next);
        btSwitchFull = (Button) findViewById(R.id.bt_switch_full);
        videoview = findViewById(R.id.videoview);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate()--");
        // 去除系统状态栏
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_system_video_player);

        findViews();

        initEvent();

        initData();

        setData();

        // 设置控制面板
        // videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (uri != null) {
            // 设置视频名称
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        } else if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            videoview.setVideoPath(mediaItem.getData());
        } else {
            ToastUtils.toastLong(this, "同学你没有传数据");
        }
    }

    private void initData() {
        // 时间转换工具
        utils = new TimeUtils();

        // 注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

        // 得到播放地址，考虑网络视频
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); // 0 - 100
            LogUtil.d("当前电量:" + level);
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setBackgroundResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void initEvent() {
        btExit.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btPre.setOnClickListener(this);
        btVoice.setOnClickListener(this);
        btStartPause.setOnClickListener(this);
        btSwitchFull.setOnClickListener(this);
        btSwitchPlayer.setOnClickListener(this);

        // 监听准备好
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        // 播放出错监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        // 播放完成监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 手指滑动的时55555555555555555555555555555候,引起SeekBar的进度变化
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的，就为true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }
        }

        /**
         * 手指触碰的时候
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指离开的时候
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart()--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart()--");

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume()--");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause()--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop()--");
    }

    @Override
    protected void onDestroy() {
        // 先释放子类的资源
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        LogUtil.e("onDestroy()--");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == btExit) {

        } else if (v == btNext) {

        } else if (v == btPre) {

        } else if (v == btStartPause) {
            if (videoview.isPlaying()) {
                // 设置视频暂停
                videoview.pause();
                // 按钮图标设置为播放
                btStartPause.setBackgroundResource(R.drawable.selector_btn_start);
            } else {
                // 设置视频播放
                videoview.start();
                // 按钮图标设置为暂停
                btStartPause.setBackgroundResource(R.drawable.selector_btn_pause);
            }

        } else if (v == btSwitchFull) {

        } else if (v == btSwitchPlayer) {

        } else if (v == btVoice) {

        }
    }

    /**
     * 准备好的监听
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        /**
         * 当底层解码准备好的时候
         *
         * @param mp
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();
            // 视频总时长,关联总长度
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);

            tvEndTime.setText(utils.stringForTime(duration));

            // 发消息
            mHandler.sendEmptyMessage(PROGRESS);
        }
    }

    /**
     * 播放出错监听
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            ToastUtils.toast(SystemVideoPlayer.this, "播放出错！");
            return false;
        }
    }

    /**
     * 播放完成监听
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            ToastUtils.toastLong(SystemVideoPlayer.this, "播放完成了 = " + uri);
        }
    }
}
