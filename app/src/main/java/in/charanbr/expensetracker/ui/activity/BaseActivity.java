package in.charanbr.expensetracker.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import in.charanbr.expensetracker.R;

public class BaseActivity extends AppCompatActivity {

    protected View mViewComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void initLayout() {
        mViewComplete = findViewById(R.id.lfn_layout_complete);
    }
}
