package in.phoenix.myspends.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import in.phoenix.myspends.BuildConfig;
import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.CurrencyListAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppCrashLogger;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;
import in.phoenix.myspends.util.KotUtil;

//import javax.inject.Inject;
/*import in.phoenix.myspends.components.AppSetupComponent;
import in.phoenix.myspends.components.DaggerAppSetupComponent;
import in.phoenix.myspends.components.DaggerMySpendsComponent;
import in.phoenix.myspends.components.MySpendsComponent;*/
/*import in.phoenix.myspends.modules.AppSetupModule;
import in.phoenix.myspends.modules.ContextModule;*/

/**
 * Created by Charan.Br on 4/10/2017.
 */

public class AppSetupActivity extends BaseActivity {

    private ListView mLvCurrencies;

    private TextView mCTvStatus;

    private ProgressBar mPbLoading;

    //@Inject
    CurrencyListAdapter mAdapter;

    private ArrayList<Currency> mCurrencies;

    private int mPosition = -1;

    private View aasLayoutSearch;
    private EditText aasEtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setup);

        Toolbar toolbar = findViewById(R.id.aas_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_currency);

        mPbLoading = findViewById(R.id.aas_progress_bar_loading);
        mLvCurrencies = findViewById(R.id.aas_listview_currency);
        mCTvStatus = findViewById(R.id.aas_ctextview_status);
        aasEtSearch = findViewById(R.id.aasEtSearch);
        aasLayoutSearch = findViewById(R.id.aasLayoutSearch);

        getCurrencyList();

        initLayout();
        aasEtSearch.addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (aasEtSearch.getText().length() >= 3) {
                filterCurrency(aasEtSearch.getText().toString());

            } else {
                filterCurrency(null);
            }
        }
    };

    private void filterCurrency(String searchTerm) {
        if (searchTerm != null) {
            ArrayList<Currency> searched = KotUtil.filterCurrency(searchTerm, mCurrencies);
            if ((searched != null) && searched.size() > 0) {
                mAdapter.setData(searched);

            } else {
                AppUtil.showToast("No match found.");
            }
        } else {
            mAdapter.setData(mCurrencies);
        }
    }

    private void getCurrencyList() {
        mPbLoading.setVisibility(View.VISIBLE);
        AppDialog.showDialog(AppSetupActivity.this, "Fetching currencies...");
        new Thread(() -> {
            try {
                mCurrencies = AppUtil.getAllCurrency();
                currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_SUCCESS);

            } catch (IOException e) {
                AppCrashLogger.INSTANCE.reportException(e);
                e.printStackTrace();
                currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_FAILURE);

            } catch (JSONException e) {
                AppCrashLogger.INSTANCE.reportException(e);
                e.printStackTrace();
                currencyHandler.sendEmptyMessage(AppConstants.CURRENCY_HANDLER_FAILURE);
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
        mPbLoading.setVisibility(View.GONE);
        AppDialog.dismissDialog();
        if (status && null != mCurrencies && mCurrencies.size() > 0) {

            mAdapter = new CurrencyListAdapter(AppSetupActivity.this, mCurrencies);
            /*AppSetupComponent appSetupComponent = DaggerAppSetupComponent.builder()
                    .appSetupModule(new AppSetupModule(AppSetupActivity.this, mCurrencies))
                    .build();
            appSetupComponent.inject(AppSetupActivity.this);*/

            mLvCurrencies.setVisibility(View.VISIBLE);
            mCTvStatus.setVisibility(View.GONE);
            //mAdapter = new CurrencyListAdapter(AppSetupActivity.this);
            if (mAdapter != null) {
                //mAdapter.setCurrencies(mCurrencies);
                mLvCurrencies.setAdapter(mAdapter);
            }
            mLvCurrencies.setOnItemClickListener(currencyClickListener);
            aasLayoutSearch.setVisibility(View.VISIBLE);

        } else {
            mCTvStatus.setVisibility(View.VISIBLE);
            mLvCurrencies.setVisibility(View.GONE);
            aasLayoutSearch.setVisibility(View.GONE);
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
                final Currency selectedCurrency = (Currency) mLvCurrencies.getAdapter().getItem(mPosition);
                FirebaseDB.initDb().setCurrency(selectedCurrency, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        /*MySpendsComponent mySpendsComponent = DaggerMySpendsComponent.builder().contextModule
                                (new ContextModule(AppSetupActivity.this)).build();*/
                        if (null == databaseError) {
                            AppPref.getInstance()
                                    .putString(AppConstants.PrefConstants.CURRENCY, selectedCurrency.getCurrencySymbol());
                            startActivity(new Intent(AppSetupActivity.this, MainActivity.class));
                            AppPref.getInstance()
                                    .putInt(AppConstants.PrefConstants.APP_SETUP, BuildConfig.VERSION_CODE);
                            finish();

                        } else {
                            int errorCode = databaseError.getCode();
                            if (errorCode == DatabaseError.NETWORK_ERROR || errorCode == DatabaseError.DISCONNECTED) {
                                AppUtil.showToast(R.string.no_internet);

                            } else if (errorCode == DatabaseError.INVALID_TOKEN || errorCode == DatabaseError.EXPIRED_TOKEN) {
                                AppUtil.showToast("Session Expired/Invalid. Please login again.");

                                AppPref.getInstance().clearAll();
                                MySpends.clearAll();
                                FirebaseDB.onLogout();
                                AppUtil.removeDynamicShortcut();
                                Intent newIntent = new Intent(AppSetupActivity.this, LaunchDeciderActivity.class);
                                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(newIntent);
                                finish();
                            }
                        }
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}