package in.phoenix.myspends.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import in.phoenix.myspends.R;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppConstants;

public class BaseActivity extends AppCompatActivity {

    View mViewComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*overridePendingTransition(R.anim.activity_open_translate,
                R.anim.activity_close_scale);*/
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        /*overridePendingTransition(R.anim.activity_open_scale,
                R.anim.activity_close_translate);*/
        super.onPause();
    }

    void initLayout() {
        mViewComplete = findViewById(R.id.lfn_layout_complete);
    }

    protected void display2BtnDialog(Context context, String message, @AppConstants.DialogConstants int action) {
        AppDialog.display2BtnDialog(context, message, action);
    }
}
