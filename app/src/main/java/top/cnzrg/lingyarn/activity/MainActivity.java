package top.cnzrg.lingyarn.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import java.util.ArrayList;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.pager.BasePager;
import top.cnzrg.lingyarn.fragment.ReplaceFragment;
import top.cnzrg.lingyarn.pager.AudioPager;
import top.cnzrg.lingyarn.pager.NetAudioPager;
import top.cnzrg.lingyarn.pager.NetVideoPager;
import top.cnzrg.lingyarn.pager.VideoPager;
import top.cnzrg.lingyarn.util.LogUtil;

/**
 * FileName: MainActivity
 * Author: ZRG
 * Date: 2019/6/24 14:57
 */
public class MainActivity extends FragmentActivity {
    // 底部标签栏
    private RadioGroup rg_bottom_tab;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;

    /**
     * 选中的位置
     */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.e("onCreate-");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        rg_bottom_tab = findViewById(R.id.rg_bottom_tab);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加本地视频页面-0
        basePagers.add(new AudioPager(this));//添加本地音乐页面-1
        basePagers.add(new NetVideoPager(this));//添加网络视频页面-2
        basePagers.add(new NetAudioPager(this));//添加网络音频页面-3

        //设置RadioGroup的监听
        rg_bottom_tab.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        // 默认选中 本地视频
        rg_bottom_tab.check(R.id.rb_video);

    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                default:    //本地视频
                    position = 0;
                    break;
                case R.id.rb_audio://音频
                    position = 1;
                    break;
                case R.id.rb_net_video://网络视频
                    position = 2;
                    break;
                case R.id.rb_net_audio://网络音频
                    position = 3;
                    break;
            }

            setFragment();

        }
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content, ReplaceFragment.newInstance(getBasePager()));
        //4.提交事务
        ft.commit();

    }

    /**
     * 根据位置得到对应的页面
     *
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        //联网请求或者绑定数据
        if (basePager != null && !basePager.isInitData) {
            basePager.initData();
            basePager.isInitData = true;
        }
        return basePager;
    }

}
