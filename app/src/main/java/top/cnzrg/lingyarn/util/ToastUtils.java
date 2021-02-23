package top.cnzrg.lingyarn.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * FileName: ToastUtils
 * Author: xw_z
 * Date: 2018/3/27 0027
 */
public class ToastUtils {
    /**
     *    * Toast实例，用于对本页出现的所有Toast进行处理
     *    
     */
    private static Toast myToast;

    /**
     *    * 此处是一个封装的Toast方法，可以取消掉上一次未完成的，直接进行下一次Toast
     *    * @param mContext mContext
     *    * @param text 需要toast的内容
     *    
     */
    public static void toast(Context context, String text) {
        if (myToast != null) {
            myToast.cancel();
        }

        // 去除miui系统的吐司自带应用名的问题
        myToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        myToast.setText(text);

        // 设置吐司位置
        setToastLocation(context);

        myToast.show();
    }

    /**
     * @param context
     * @param text
     */
    public static void toastLong(Context context, String text) {
        if (myToast != null) {
            myToast.cancel();
        }

        myToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        myToast.setText(text);
        myToast.show();

        // 设置吐司位置
        setToastLocation(context);

        myToast.show();
    }

    /**
     * 设置吐司默认位置
     *
     * @param context
     */
    private static void setToastLocation(Context context) {
        if (context instanceof Activity) {
            // 获取屏幕高度
            int height = ViewUtils.getScreenHeight(((Activity) context).getWindowManager());

            // 这里给了一个1/4屏幕高度的y轴偏移量
            myToast.setGravity(Gravity.TOP, 0, height / 5);
        }
    }
}