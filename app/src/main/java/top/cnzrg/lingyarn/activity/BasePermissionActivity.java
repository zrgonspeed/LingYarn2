package top.cnzrg.lingyarn.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import top.cnzrg.lingyarn.util.ToastUtils;

/**
 * FileName: BasePermissionActivity
 * Author: ZRG
 * Date: 2019/5/8 18:35
 */
public abstract class BasePermissionActivity extends Activity {
    private static int REQUEST_CODE;
    private static String tag = "BasePermissionActivity";

    /**
     * 子类中具体实现初始化方法
     */
    protected abstract void init();

    protected void initPermission(String[] permissions, int requestCode) {
        REQUEST_CODE = requestCode;

        // 要申请的权限
        /*String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };*/

        // 调用permissionAllGranted方法，判断是否需要申请权限
        boolean permission = permissionAllGranted(permissions);

        if (!permission) {
            // 没有权限时
            // 请求权限
            Log.i(tag, "请求权限");
            Log.i(tag, "this是:" + this);
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        } else {
            //有权限时
            init();
        }
    }

    // 权限是否被授权了
    protected boolean permissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则不能完成你需要的功能就返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults.length > 0) { // 将这里的requestCode改成你的任意数字，上边的一致就行
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;//是否授权，可以根据permission作为标记

            if (granted) {
                // 授权了
                init();
            } else {
                // 没有授予，比如可以给用户弹窗告诉用户，你拒绝了权限，所以不能实现某个功能，如果想实现你可以跳转到设置，如果不想实现那么直接把这个弹窗取消
                ToastUtils.toastLong(getApplicationContext(), "您拒绝了授权");
                finish();
            }
        }
    }
}
