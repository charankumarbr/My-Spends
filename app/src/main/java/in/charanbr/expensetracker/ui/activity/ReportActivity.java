package in.charanbr.expensetracker.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.charanbr.expensetracker.R;

public class ReportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ar_in_toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}
