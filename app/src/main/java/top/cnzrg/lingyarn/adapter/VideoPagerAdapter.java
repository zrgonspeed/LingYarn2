package top.cnzrg.lingyarn.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.domain.MediaItem;
import top.cnzrg.lingyarn.pager.VideoPager;
import top.cnzrg.lingyarn.util.TimeUtils;

/**
 * FileName: VideoPagerAdapter
 * Author: ZRG
 * Date: 2020/11/8 12:37
 */
public class VideoPagerAdapter extends BaseAdapter {
    private Context context;
    private List<MediaItem> mediaItems;

    private transient TimeUtils timeUtils;

    public VideoPagerAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.timeUtils = new TimeUtils();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoPagerAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_item_videopager, null);

            viewHolder = new VideoPagerAdapter.ViewHolder();
            viewHolder.iv_default = convertView.findViewById(R.id.iv_default);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = convertView.findViewById(R.id.tv_size);
            viewHolder.tv_duration = convertView.findViewById(R.id.tv_duration);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VideoPagerAdapter.ViewHolder) convertView.getTag();
        }

        // 根据position得到列表中的数据
        MediaItem mediaItem = mediaItems.get(position);

        // 视频名称
        viewHolder.tv_name.setText(mediaItem.getName());

        // 视频大小 字节byte -> 兆字节MB
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()) + "");

        // 视频时间 毫秒转秒
        viewHolder.tv_duration.setText(timeUtils.stringForTime((int) mediaItem.getDuration()));

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_default;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}
