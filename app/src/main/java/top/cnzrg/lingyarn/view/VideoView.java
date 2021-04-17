package top.cnzrg.lingyarn.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * FileName: VideoView
 * Author: ZRG
 * Date: 2021/4/17 15:49
 */
public class VideoView extends android.widget.VideoView {
    public VideoView(Context context) {
        this(context, null);
    }

    /**
     * 当这类在布局文件的时候，系统通过该构造方法实例化该类
     *
     * @param context
     * @param attrs
     */
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候使用
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置视频宽高
     * by ZRG
     *
     * @param width
     * @param height
     */
    public void setVideoSize(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;

        setLayoutParams(params);
    }
}
