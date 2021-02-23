package top.cnzrg.lingyarn.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.activity.SystemVideoPlayer;
import top.cnzrg.lingyarn.adapter.VideoPagerAdapter;
import top.cnzrg.lingyarn.domain.MediaItem;
import top.cnzrg.lingyarn.util.LogUtil;
import top.cnzrg.lingyarn.util.TimeUtils;

/**
 * FileName: VideoPager
 * Author: ZRG
 * Date: 2019/6/28 1:59
 * 作用: 本地视频页面
 */
public class VideoPager extends BasePager {
    private static final String TAG = VideoPager.class.getSimpleName();

    private transient ListView lv_videos;
    private transient TextView tv_nomedia;
    private transient ProgressBar pb_loading;

    private transient ArrayList<MediaItem> mediaItems = new ArrayList<>();

    private transient VideoPagerAdapter videoPagerAdapter;

    private transient Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaItems != null && mediaItems.size() > 0) {
                // 有数据
                // 设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context, mediaItems);
                lv_videos.setAdapter(videoPagerAdapter);

                // 隐藏文本
                tv_nomedia.setVisibility(View.GONE);

            } else {
                // 没有数据
                // 显示文本，提示没有本地视频
                tv_nomedia.setVisibility(View.VISIBLE);
            }

            // ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地视频initView()");

        View view = View.inflate(context, R.layout.pager_video, null);
        lv_videos = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);

        lv_videos.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            Log.i(TAG, "mediaItem = " + mediaItem);

            // 1.调起系统的所有播放器，隐式意图，暂时不能播放
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//            context.startActivity(intent);

            // 2.调用自己写的播放器，显示意图
//            Intent intent = new Intent(context, SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//            context.startActivity(intent);

            // 3.传递播放列表
            Intent intent2 = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent2.putExtras(bundle);
            intent2.putExtra("position", position);
            context.startActivity(intent2);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频initData()");

        // 加载本地视频数据
        getDataFromLocal();
    }

    /**
     * 从本地的sdcard得到数据，包括本机内存
     * 方式1：遍历sdcard    （太慢）
     * 方式2：从内容提供者里面获取视频
     * 6.0以上需要动态权限
     */
    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 为了能看到加载本地视频的转圈圈
//                SystemClock.sleep(1000);

                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME, // 视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,    // 视频总时长
                        MediaStore.Video.Media.SIZE,        // 视频的文件大小
                        MediaStore.Video.Media.DATA,        // 视频的绝对地址
                        MediaStore.Video.Media.ARTIST,        // 歌曲的演唱者

                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                LogUtil.i("cursor: " + cursor);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);

                        MediaItem mediaItem = new MediaItem();
                        mediaItem.setArtist(artist);
                        mediaItem.setData(data);
                        mediaItem.setDuration(duration);
                        mediaItem.setSize(size);
                        mediaItem.setName(name);

                        mediaItems.add(mediaItem);

                        LogUtil.i("有视频了: " + mediaItem);
                    }
                    cursor.close();
                }

                // 发消息，Handler
                mHandler.sendEmptyMessage(666);

            }
        }).start();
    }
}
