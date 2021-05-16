package top.cnzrg.lingyarn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.domain.MediaItem;
import top.cnzrg.lingyarn.util.LogUtil;
import top.cnzrg.lingyarn.util.TimeUtils;
import top.cnzrg.lingyarn.util.ToastUtils;
import top.cnzrg.lingyarn.view.VideoView;

/**
 * FileName: SystemVideoPlayer
 * Author: ZRG
 * Date: 2020/11/8 13:32
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final String TAG = SystemVideoPlayer.class.getSimpleName();
    private VideoView videoview;
    private Uri uri;

    private ConstraintLayout media_controller;

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
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;

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

    private GestureDetector detector;
    private boolean showMediaController = true;

    private final static int HIDE_MEDIACONTROLLER = 2;
    private final static int TIME_HIDE_MEDIACONTROLLER = 3000;

    private final static int DEF_SCREEN = 0;
    private final static int FULL_SCREEN = 1;
    private boolean isFullScreen = false;
    private int maxVolume;
    private int currentVolume;

    private void findViews() {
        media_controller = findViewById(R.id.media_controller);

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
        videoview = (VideoView)findViewById(R.id.videoview);


        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate()--");
        // 去除系统状态栏
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_system_video_player);

        initData();

        findViews();

        initEvent();

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

        setButtonState();

        // 默认显示控制面板
        showMediaController();
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

        // 手势识别器
        detector = new GestureDetector(this, new MyGestureDetector());

        // 获取屏幕宽高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        // 音量相关
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
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

        // 音量条
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
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
            playNextVideo();
        } else if (v == btPre) {
            playPreVideo();
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
            if (isFullScreen) {
                setScreenType(DEF_SCREEN);
            } else {
                setScreenType(FULL_SCREEN);
            }


        } else if (v == btSwitchPlayer) {

        } else if (v == btVoice) {
            isMute = !isMute;
            updateVoice(currentVolume, isMute);
        }

        mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, TIME_HIDE_MEDIACONTROLLER);
    }

    // 屏幕宽高
    private int screenWidth;
    private int screenHeight;

    // 视频原生宽高
    private int videoWidth;
    private int videoHeight;


    private void setScreenType(int type) {
        switch (type) {
            case FULL_SCREEN:
                // 1. 设置视频画面大小
                // 2. 设置按钮状态
                videoview.setVideoSize(screenWidth, screenHeight);
                btSwitchFull.setBackgroundResource(R.drawable.selector_btn_switch_full_default);
                isFullScreen = true;
                break;
            case DEF_SCREEN:
                int width = 0;
                int height = 0;

                if ( videoWidth * screenHeight  < screenWidth * videoHeight ) {
                    width = screenHeight * videoWidth / videoHeight;
                } else if ( videoWidth * screenHeight  > screenWidth * videoHeight ) {
                    height = screenWidth * videoHeight / videoWidth;
                }

                videoview.setVideoSize(width, height);

                btSwitchFull.setBackgroundResource(R.drawable.selector_btn_switch_full);
                isFullScreen = false;
                break;
        }
    }

    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                btStartPause.setBackgroundResource(R.drawable.selector_btn_pause);

                // 设置按钮状态
                setButtonState();
            }

        } else if (uri != null) {
            setButtonState();
        }

    }

    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                btStartPause.setBackgroundResource(R.drawable.selector_btn_pause);

                // 设置按钮状态
                setButtonState();
            }

        } else if (uri != null) {
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                // 两个按钮都不能点击
                btPre.setEnabled(false);
                btPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btNext.setEnabled(false);
                btNext.setBackgroundResource(R.drawable.btn_next_gray);
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    // pre 不能点击
                    btPre.setEnabled(false);
                    btPre.setBackgroundResource(R.drawable.btn_pre_gray);

                    // next 可以点击
                    btNext.setEnabled(true);
                    btNext.setBackgroundResource(R.drawable.selector_btn_next);
                } else {
                    // pre 可以点击
                    btPre.setEnabled(true);
                    btPre.setBackgroundResource(R.drawable.selector_btn_pre);

                    // next 不能点击
                    btNext.setEnabled(false);
                    btNext.setBackgroundResource(R.drawable.btn_next_gray);
                }
            } else {
                if (position == mediaItems.size() - 1) {
                    // next 不能点击
                    btNext.setEnabled(false);
                    btNext.setBackgroundResource(R.drawable.btn_next_gray);
                } else if (position == 0) {
                    // pre 不能点击
                    btPre.setEnabled(false);
                    btPre.setBackgroundResource(R.drawable.btn_pre_gray);
                } else {
                    // 两个都可以点击
                    btPre.setEnabled(true);
                    btPre.setBackgroundResource(R.drawable.selector_btn_pre);
                    btNext.setEnabled(true);
                    btNext.setBackgroundResource(R.drawable.selector_btn_next);
                }

            }
        } else if (uri != null) {
            // 两个按钮都不能点击
            btPre.setEnabled(false);
            btPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btNext.setEnabled(false);
            btNext.setBackgroundResource(R.drawable.btn_next_gray);
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

            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            LogUtil.i("视频宽度：" + videoWidth + "  视频高度：" + videoWidth);
//            videoview.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
            setScreenType(DEF_SCREEN);

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
//            ToastUtils.toastLong(SystemVideoPlayer.this, "播放完成了");
            playNextVideo();
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            ToastUtils.toast(getApplicationContext(), "长按了");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            ToastUtils.toast(getApplicationContext(), "双击了");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            ToastUtils.toast(getApplicationContext(), "单击了");

            if (showMediaController) {
                hideMediaController();
            } else {
                showMediaController();
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    private float startY;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   // 手指按下
                // 1.按下记录值
                startY = event.getY();

                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                LogUtil.d("screenHeight == " + screenHeight + "  screenWidth == " + screenWidth);
                // 竖屏的时候 w == 720   横屏时候 h = 720
                touchRang = Math.min(screenHeight, screenWidth);
                mHandler.removeMessages(HIDE_MEDIACONTROLLER);

                break;

            case MotionEvent.ACTION_UP:     // 手指离开
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                break;

            case MotionEvent.ACTION_MOVE:   // 手指移动
                // 2.移动的记录相关值
                float endY = event.getY();
                float distanceY = startY - endY;

                // 改变声音 = (滑动屏幕的距离:总距离) * 音量最大值
                float delta = (distanceY / touchRang) * maxVolume;

                // 最终声音 = 原来的 + 改变声音
                int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVolume);
                if (delta != 0) {
                    isMute = false;
                    updateVoice(voice, false);

                }
                break;
        }



        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            updateVoice(currentVolume, false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updateVoice(currentVolume, false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    // 点击隐藏与显示
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        showMediaController = true;

        mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, TIME_HIDE_MEDIACONTROLLER);
    }

    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        showMediaController = false;

        mHandler.removeMessages(HIDE_MEDIACONTROLLER);
    }


    private AudioManager am;
    private boolean isMute = false;
    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updateVoice(progress, isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, TIME_HIDE_MEDIACONTROLLER);
        }
    }

    private void updateVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  0);
            seekbarVoice.setProgress(0);
        } else  {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress,  0);
            seekbarVoice.setProgress(progress);
        }
        currentVolume = progress;
    }

}
