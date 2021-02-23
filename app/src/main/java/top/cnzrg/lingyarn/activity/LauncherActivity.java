package top.cnzrg.lingyarn.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import top.cnzrg.lingyarn.R;
import top.cnzrg.lingyarn.activity.MainActivity;

public class LauncherActivity extends BasePermissionActivity {
    // 动态跟着类名改 TAG
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private Handler mHandler = new Handler();

    // 权限请求码
    private static final int REQUEST_CODE = 1001;

    // 要申请的权限
    private static String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // 6.0以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission(permissions, REQUEST_CODE);
        } else {
            init();
        }

    }

    /**
     * 跳转到主页面，并且把当前页面关闭掉
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: 点击启动界面马上进主页面:" + event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解决点击启动页面，然后快速点返回键，界面又出现的bug
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void init() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 两秒后执行到这里
                // 执行在主线程中,因为该Handler在主线程new
                startMainActivity();
                Log.i(TAG, "run:当前线程名称== " + Thread.currentThread().getName());
            }
        }, 2000);
    }
}
