package in.phoenix.myspends.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;

/**
 * Created by Charan.Br on 12/22/2017.
 */

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.ap_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        CustomTextView cTvData = findViewById(R.id.ap_tv_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cTvData.setText(user.getDisplayName());
        cTvData = null;
        cTvData = findViewById(R.id.ap_tv_email);
        cTvData.setText(user.getEmail());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
