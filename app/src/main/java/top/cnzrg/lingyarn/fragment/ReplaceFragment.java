package top.cnzrg.lingyarn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import top.cnzrg.lingyarn.pager.BasePager;

/**
 * FileName: ReplaceFragment
 * Author: ZRG
 * Date: 2019/8/8 16:39
 */
public class ReplaceFragment extends Fragment {

    private BasePager basePager;

    public ReplaceFragment() {
    }

    public static ReplaceFragment newInstance(BasePager basePager) {
        ReplaceFragment newFragment = new ReplaceFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", basePager);
        newFragment.setArguments(bundle);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            basePager = (BasePager) args.getSerializable("key");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (basePager != null) {
            //各个页面的视图
            return basePager.getRootView();
        }
        return null;
    }
}
