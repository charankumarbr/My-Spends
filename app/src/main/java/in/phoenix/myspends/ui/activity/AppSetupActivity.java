package in.phoenix.myspends.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import in.phoenix.myspends.BuildConfig;
import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.CurrencyListAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public class AppSetupActivity extends BaseActivity {

    private ListView mLvCurrencies;

    private TextView mCTvStatus;

    private ProgressBar mPbLoading;

    private CurrencyListAdapter mAdapter;

    private ArrayList<Currency> mCurrencies;

    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setup);
        Toolbar toolbar = findViewById(R.id.aas_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_currency);
        getCurrencyList();
        mLvCurrencies = findViewById(R.id.aas_listview_currency);
        mCTvStatus = findViewById(R.id.aas_ctextview_status);
        mPbLoading = findViewById(R.id.aas_progress_bar_loading);
        initLayout();
    }

    private void getCurrencyList() {
        //mPbLoading.setVisibility(View.VISIBLE);
        AppDialog.showDialog(AppSetupActivity.this, "Fetching currencies...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mCurrencies = AppUtil.getAllCurrency();
                    currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_SUCCESS);

                } catch (IOException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_FAILURE);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_FAILURE);
                }
            }
        }).start();
    }

    private final Handler currencyHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!isFinishing()) {
                if (msg.what == AppConstants.CURRENCY_HANDLER_SUCCESS) {
                    displayCurrencyList(true);

                } else {
                    displayCurrencyList(false);
                }
            }
        }
    };

    private void displayCurrencyList(boolean status) {
        //mPbLoading.setVisibility(View.GONE);
        AppDialog.dismissDialog();
        if (status && null != mCurrencies && mCurrencies.size() > 0) {
            mLvCurrencies.setVisibility(View.VISIBLE);
            mAdapter = new CurrencyListAdapter(AppSetupActivity.this, mCurrencies);
            mLvCurrencies.setAdapter(mAdapter);
            mLvCurrencies.setOnItemClickListener(currencyClickListener);

        } else {
            mCTvStatus.setVisibility(View.VISIBLE);
        }
    }

    private final AdapterView.OnItemClickListener currencyClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            mPosition = position;
            mAdapter.setSelectedPosition(position);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            if (mPosition < 0) {
                AppUtil.showSnackbar(mViewComplete, "Please select the currency!");

            } else {
                Currency selectedCurrency = (Currency) mLvCurrencies.getAdapter().getItem(mPosition);
                FirebaseDB.initDb().setCurrency(selectedCurrency);
                AppPref.getInstance().putString(AppConstants.PrefConstants.CURRENCY, selectedCurrency.getCurrencySymbol());
                startActivity(new Intent(AppSetupActivity.this, MainActivity.class));
                AppPref.getInstance().putInt(AppConstants.PrefConstants.APP_SETUP, BuildConfig.VERSION_CODE);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}