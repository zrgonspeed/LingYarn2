package top.cnzrg.lingyarn.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.util.ToastUtils;

/**
 * FileName: TitleBar
 * Author: ZRG
 * Date: 2019/12/12 22:11
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private TextView tv_search;
    private RelativeLayout rl_game;
    private ImageView iv_history;

    private Context context;

    /**
     * 代码中new
     *
     * @param context
     */
    public TitleBar(Context context) {
        this(context, null);
    }

    /**
     * 布局文件反射创建
     *
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 获取孩子实例
        tv_search = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_history = (ImageView) getChildAt(3);

        // 设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                ToastUtils.toast(context, "点击了搜索框");
                break;

            case R.id.rl_game:
                ToastUtils.toast(context, "点击了游戏图标");
                break;

            case R.id.iv_history:
                ToastUtils.toast(context, "点击了历史图标");
                break;
        }
    }
}
