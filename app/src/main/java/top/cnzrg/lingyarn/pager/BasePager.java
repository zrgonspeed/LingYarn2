package top.cnzrg.lingyarn.pager;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * FileName: BasePager
 * Author: ZRG
 * Date: 2019/6/28 1:52
 * <p>
 * 作用：基类
 * <p>
 * VideoPager
 * AudioPager
 * NetVideoPager
 * NetAudioPager
 * 这四个都继承 BasePager
 */
public abstract class BasePager implements Serializable {
    protected transient final Context context;
    public  boolean isInitData = false;

    private transient View rootView;

    public BasePager(Context context) {
        this.context = context;
        this.rootView = initView();
    }

    /**
     * 强制子类实现该方法
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 当子页面需要初始化数据，联网请求数据或绑定数据的时候，要重写该方法
     */
    public void initData() {
    }

    public View getRootView() {
        return rootView;
    }

}
