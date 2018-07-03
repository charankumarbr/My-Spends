package in.phoenix.myspends.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 3/2/2017.
 */
public class ViewExpenseActivity extends BaseActivity {

    public static final String VIEW_NAME_NOTE = "view:name:note";
    public static final String VIEW_NAME_AMOUNT = "view:name:amount";
    public static final String VIEW_NAME_TYPE = "view:name:type";
    private NewExpense mExpense = null;

    private CustomTextView mCTvAmount;
    private CustomTextView mCTvExpenseOn;
    private CustomTextView mCTvAddedOn;
    private CustomTextView mCTvLastUpdatedOn;
    private CustomTextView mCTvPaidBy;
    private CustomTextView mCTvNote;
    private CustomTextView mCTvCategory;

    private int mResultCode = RESULT_CANCELED;

    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((null == getIntent()) || !getIntent().hasExtra(AppConstants.Bundle.EXPENSE)) {
            onDestroy();
        }
        /*mExpensePrimaryKey = getIntent().getIntExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY, -1);
        if (mExpensePrimaryKey < 0) {
            onDestroy();
        }
        isNew = (mExpensePrimaryKey == 0);*/

        setContentView(R.layout.activity_view_expense);
        init();

        mExpense = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE);

        /*if (!isNew) {
            getParticularExpense();
        }*/

        viewExpense();
        /*mCTvAddedOn.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewExpense();
            }
        }, 200);*/
    }

    private void init() {
        initLayout();
        Toolbar toolbar = findViewById(R.id.ave_toolbar);
        toolbar.setTitle("Tracked Expense");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setElevation(0f);

        mCTvAmount = findViewById(R.id.ave_ctextview_amount);
        mCTvExpenseOn = findViewById(R.id.ave_ctextview_expense_on);
        mCTvAddedOn = findViewById(R.id.ave_ctextview_created_on);
        mCTvLastUpdatedOn = findViewById(R.id.ave_ctextview_updated_on);
        mCTvPaidBy = findViewById(R.id.ave_cTextView_paid_by);
        mCTvNote = findViewById(R.id.ave_cTextView_note);
        mCTvCategory = findViewById(R.id.ave_cTextView_category);

        mPbLoading = findViewById(R.id.ave_pb_loading);
    }

    /*private void getParticularExpense() {
        //mExpense = DBManager.getExpense(mExpensePrimaryKey);
        viewExpense();
    }*/

    private void viewExpense() {
        if (null != mExpense) {
            AppLog.d("ViewExpense", "Spend:" + mExpense.toString());
            mCTvAmount.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)
                    + " " + AppUtil.getStringAmount(String.valueOf(mExpense.getAmount())));
            mCTvNote.setText(TextUtils.isEmpty(mExpense.getNote()) ? AppConstants.BLANK_NOTE_TEMPLATE : mExpense.getNote());
            mCTvPaidBy.setText(AppUtil.getPaidByForKey(mExpense.getPaymentTypeKey()));

            ViewCompat.setTransitionName(mCTvAmount, VIEW_NAME_AMOUNT);
            ViewCompat.setTransitionName(mCTvNote, VIEW_NAME_NOTE);
            ViewCompat.setTransitionName(mCTvPaidBy, VIEW_NAME_TYPE);

            mCTvCategory.setText(MySpends.getCategoryName(mExpense.getCategoryId()));

            ExpenseDate expenseDate = new ExpenseDate(mExpense.getExpenseDate());
            mCTvExpenseOn.setText(getString(R.string.expense_on) + " " + expenseDate.getFormattedDate());

            /*boolean isAddedOnDiffDate = expenseDate.isSameExpenseDate(mExpense.getCreatedOn());
            boolean isUpdated = mExpense.getCreatedOn() != mExpense.getUpdatedOn();*/

            AppLog.d("ViewExpense", "ExpenseDate:" + expenseDate.getTimeInMillis() + ":: Created Date:" + mExpense.getCreatedOn() + ":: Expense:" + mExpense.getExpenseDate());
            if (mExpense.isAddedOnDiffDate()) {
                try {
                    mCTvAddedOn.setText(getString(R.string.added_on) + " " + AppUtil.dateDBToString(mExpense.getCreatedOn()));
                    mCTvAddedOn.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    mCTvAddedOn.setVisibility(View.GONE);
                }
            }

            if (mExpense.isUpdated()) {
                try {
                    mCTvLastUpdatedOn.setText(getString(R.string.last_updated_on) + " " + AppUtil.dateDBToString(mExpense.getUpdatedOn()));
                    mCTvLastUpdatedOn.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    mCTvLastUpdatedOn.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.menu_edit_expense) {
            Intent editExpenseIntent = new Intent(ViewExpenseActivity.this, NewExpenseActivity.class);
            editExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE, mExpense);
            startActivityForResult(editExpenseIntent, AppConstants.EDIT_EXPENSE_CODE);
            return true;

        } else if (item.getItemId() == R.id.menu_delete_expense) {
            confirmExpenseDeletion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmExpenseDeletion() {
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(ViewExpenseActivity.this);
        deleteBuilder.setTitle(R.string.delete_expense);
        deleteBuilder.setMessage(R.string.confirm_delete_message);
        deleteBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (AppUtil.isConnected()) {
                    deleteExpense(mExpense.getId());

                } else {
                    AppUtil.showToast(getString(R.string.no_internet));
                }
            }
        });
        deleteBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteBuilder.setCancelable(true);
        deleteBuilder.create().show();
    }

    private void deleteExpense(String key) {
        //mPbLoading.setVisibility(View.VISIBLE);
        AppDialog.showDialog(ViewExpenseActivity.this, "Deleting expense...");
        FirebaseDB.initDb().deleteFsExpense(key, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                //mPbLoading.setVisibility(View.GONE);
                AppDialog.dismissDialog();
                AppUtil.showToast(R.string.expense_deleted_successfully);
                setResult(RESULT_OK);
                ActivityCompat.finishAfterTransition(ViewExpenseActivity.this);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //mPbLoading.setVisibility(View.GONE);
                AppDialog.dismissDialog();
                AppUtil.showSnackbar(mViewComplete, "Unable to delete.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.EDIT_EXPENSE_CODE) {
            if (resultCode == RESULT_OK) {
                mResultCode = RESULT_OK;
                if (null != data && data.hasExtra(AppConstants.Bundle.EXPENSE) &&
                        null != data.getParcelableExtra(AppConstants.Bundle.EXPENSE)) {
                    mExpense = data.getParcelableExtra(AppConstants.Bundle.EXPENSE);
                    viewExpense();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(mResultCode);
        ActivityCompat.finishAfterTransition(ViewExpenseActivity.this);
    }
}
