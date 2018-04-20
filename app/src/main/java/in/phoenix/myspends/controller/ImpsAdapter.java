package in.phoenix.myspends.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import in.phoenix.myspends.R;
import in.phoenix.myspends.ui.fragment.ImpFragment;

/**
 * Created by Charan.Br on 4/20/2018.
 */

public class ImpsAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String[] mImps;
    public ImpsAdapter(Context context, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        mContext = context;
        mImps = mContext.getResources().getStringArray(R.array.imps);
    }

    @Override
    public Fragment getItem(int position) {
        return ImpFragment.newInstance(mImps[position]);
    }

    @Override
    public int getCount() {
        return mImps.length;
    }
}
