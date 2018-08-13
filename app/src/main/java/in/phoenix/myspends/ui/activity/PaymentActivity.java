package in.phoenix.myspends.ui.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.PaymentTypeAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

public class PaymentActivity extends BaseActivity implements PaymentTypeAdapter.OnStatusChangedListener
        , AddPaymentTypeFragment.OnPaymentTypeListener, PaymentTypeParser.PaymentTypeParserListener {

    private ListView mLvPayment = null;

    private PaymentTypeAdapter mPaymentAdapter = null;

    private ProgressBar mPbLoading = null;

    private ProgressDialog mDialog = null;

    private int mPaymentTypeCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.ap_toolbar);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }*/
        toolbar.setTitle("Payment Types");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initLayout();

        mLvPayment = findViewById(R.id.ap_listview_payment);
        mPbLoading = findViewById(R.id.ap_pb_loading);
        //getLoaderManager().initLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);

        getPaymentTypes();
    }

    private void getPaymentTypes() {
        //mPbLoading.setVisibility(View.VISIBLE);
        AppDialog.showDialog(PaymentActivity.this, "Fetching Payment types...");
        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("PaymentType", "Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(PaymentActivity.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onPaymentTypesParsed(null, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //mPbLoading.setVisibility(View.GONE);
                AppDialog.dismissDialog();
                mPaymentTypeCount = -1;
                if (null != databaseError) {
                    AppLog.d("PaymentType", "Payment Types Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("PaymentType", "Payment Types Error!");
                }
                AppUtil.showToast("Unable to fetch payment types.");
                finish();
            }
        });
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ExpenseCursorLoader(PaymentActivity.this, AppConstants.LoaderConstants.LOADER_PAYMENT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data) {
            if (null == mLvPayment.getAdapter()) {
                mPaymentAdapter = new PaymentTypeAdapter(PaymentActivity.this, data);
                mLvPayment.setAdapter(mPaymentAdapter);

            } else {
                mPaymentAdapter.swapCursor(data);
                mPaymentAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (null != mPaymentAdapter) {
            mPaymentAdapter.swapCursor(null);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.menu_add_payment_type) {
            if (mPaymentTypeCount <= 0) {
                AppUtil.showToast("Unable to add new Payment Types. Please try later.");

            } else if (mPaymentTypeCount == AppConstants.MAX_PAYMENT_TYPE_COUNT) {
                AppUtil.showToast("Reached maximum count of Payment Types.");

            } else {
                showPaymentTypeDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPaymentTypeDialog() {
        AddPaymentTypeFragment addPaymentTypeFragment = AddPaymentTypeFragment.newInstance();
        addPaymentTypeFragment.show(getSupportFragmentManager(), "AddPaymentTFragment");
    }

    @Override
    public void onStatusChanged(String paymentTypeKey, boolean isChecked) {

        AppLog.d("PaymentActivity", "onStatusChanged Key:" + paymentTypeKey + " :: isChecked:" + isChecked);
        if (null == mDialog) {
            mDialog = new ProgressDialog(PaymentActivity.this);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("Changing status...");
            mDialog.show();
            AppLog.d("PaymentActivity", "onStatusChanged 1");
            FirebaseDB.initDb().togglePaymentType(paymentTypeKey, isChecked, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    AppLog.d("PaymentActivity", "onStatusChanged 2");
                    if (null == databaseError) {
                        AppLog.d("PaymentActivity", "onStatusChanged 3");
                        //onPaymentTypesParsed(MySpends.getAllPaymentTypes(), true);
                        getPaymentTypes();
                        AppLog.d("PaymentActivity", "onStatusChanged 4");

                    } else {
                        AppLog.d("PaymentActivity", "onStatusChanged 5 : Error:" + databaseError.getDetails());
                        AppUtil.showToast("Unable to change the status.");
                    }

                    if (!isFinishing()) {
                        if (null != mDialog) {
                            mDialog.dismiss();
                        }
                        mDialog = null;
                    }
                }
            });
        }
    }

    @Override
    public void onPaymentTypeAdded() {
        mViewComplete.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag("AddPaymentTFragment");
                if (dialogFragment instanceof AddPaymentTypeFragment) {
                    ((AddPaymentTypeFragment) dialogFragment).dismissAllowingStateLoss();
                }

                AppUtil.showSnackbar(mViewComplete, "Payment type added!");
                //onPaymentTypesParsed(MySpends.getAllPaymentTypes(), true);
                getPaymentTypes();
            }
        }, 600);
    }

    @Override
    public void onPaymentTypesParsed(ArrayList<PaymentType> paymentTypes, boolean isCashPaymentTypeAdded) {
        //mPbLoading.setVisibility(View.GONE);
        AppDialog.dismissDialog();

        if (null == paymentTypes && !isCashPaymentTypeAdded) {
            AppUtil.showToast("Unable to fetch payment types!");
            finish();
            return;
        }

        if (null == paymentTypes) {
            paymentTypes = new ArrayList<>();
        }

        if (!isCashPaymentTypeAdded) {
            paymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        mPaymentTypeCount = paymentTypes.size();

        if (null == mPaymentAdapter) {
            mPaymentAdapter = new PaymentTypeAdapter(PaymentActivity.this, paymentTypes);
            mLvPayment.setAdapter(mPaymentAdapter);

        } else {
            mPaymentAdapter.setData(paymentTypes);
        }
    }
}
