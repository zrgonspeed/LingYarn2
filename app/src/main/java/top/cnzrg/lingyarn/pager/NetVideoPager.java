package top.cnzrg.lingyarn.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import top.cnzrg.lingyarn.util.LogUtil;

/**
 * FileName: VideoPager
 * Author: ZRG
 * Date: 2019/6/28 1:59
 * 作用: 网络视频页面
 */
public class NetVideoPager extends BasePager {

    private  transient TextView textView;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视频initView()");

        textView = new TextView(context);
        textView.setTextSize(25f);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频initData()");
        textView.setText("网络视频页面");
    }
}
