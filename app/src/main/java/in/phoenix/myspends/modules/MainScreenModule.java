package in.phoenix.myspends.modules;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.ui.activity.MainActivity;

/**
 * Author: Charan Kumar
 * Date: 2019-05-03
 */
@Module
public class MainScreenModule {

    private MainActivity mainActivity;
    private ArrayList<NewExpense> spends;
    private View.OnClickListener clickListener;

    public MainScreenModule(MainActivity mainActivity,
                            ArrayList<NewExpense> spends,
                            View.OnClickListener clickListener) {
        this.mainActivity = mainActivity;
        this.spends = spends;
        this.clickListener = clickListener;
    }

    @Provides
    public Context getContext() {
        return mainActivity;
    }

    @Provides
    public ArrayList<NewExpense> getSpends() {
        return spends;
    }

    @Provides
    public View.OnClickListener getClickListener() {
        return clickListener;
    }

}
