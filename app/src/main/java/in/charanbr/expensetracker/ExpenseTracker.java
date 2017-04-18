package in.charanbr.expensetracker;

import android.app.Application;
import android.content.Context;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public class ExpenseTracker extends Application {

    public static Context APP_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;
    }
}
