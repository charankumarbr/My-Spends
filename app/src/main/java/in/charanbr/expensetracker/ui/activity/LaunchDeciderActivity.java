package in.charanbr.expensetracker.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppPref;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public class LaunchDeciderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int appSetup = AppPref.getInstance().getInt(AppConstants.PrefConstants.APP_SETUP);
        Intent nextIntent;
        if (appSetup == -1) {
            nextIntent = new Intent(LaunchDeciderActivity.this, AppSetupActivity.class);

        } else {
            nextIntent = new Intent(LaunchDeciderActivity.this, MainActivity.class);
        }
        startActivity(nextIntent);
        finish();
    }
}
