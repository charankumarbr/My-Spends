package in.phoenix.trackmyspends.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.phoenix.trackmyspends.R;

public class BaseActivity extends AppCompatActivity {

    View mViewComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_open_translate,
                R.anim.activity_close_scale);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);
        super.onPause();
    }

    void initLayout() {
        mViewComplete = findViewById(R.id.lfn_layout_complete);
    }
}
