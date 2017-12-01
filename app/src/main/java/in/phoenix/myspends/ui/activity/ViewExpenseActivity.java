package in.phoenix.myspends.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.database.DBManager;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 3/2/2017.
 */
public class ViewExpenseActivity extends BaseActivity {

    private boolean isNew = false;

    //private int mExpensePrimaryKey = -1;

    private NewExpense mExpense = null;
    private ExpenseDate mExpenseDate = null;

    private CustomTextView mCTvAmount;
    private CustomTextView mCTvExpenseOn;
    private CustomTextView mCTvAddedOn;
    private CustomTextView mCTvLastUpdatedOn;
    private CustomTextView mCTvPaidBy;
    private CustomTextView mCTvNote;

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

        mCTvAddedOn.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewExpense();
            }
        }, 200);
    }

    private void init() {
        initLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.ave_toolbar);
        toolbar.setTitle(isNew ? "New Expense" : "Tracked Expense");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setElevation(0f);

        mCTvAmount = (CustomTextView) findViewById(R.id.ave_ctextview_amount);
        mCTvExpenseOn = (CustomTextView) findViewById(R.id.ave_ctextview_expense_on);
        mCTvAddedOn = (CustomTextView) findViewById(R.id.ave_ctextview_created_on);
        mCTvLastUpdatedOn = (CustomTextView) findViewById(R.id.ave_ctextview_updated_on);
        mCTvPaidBy = (CustomTextView) findViewById(R.id.ave_cTextView_paid_by);
        mCTvNote = (CustomTextView) findViewById(R.id.ave_cTextView_note);

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
            mExpenseDate = new ExpenseDate(mExpense.getExpenseDate());
            boolean isAddedOnDiffDate = mExpenseDate.isSameExpenseDate(mExpense.getCreatedOn());
            boolean isUpdated = mExpense.getCreatedOn() != mExpense.getUpdatedOn();

            mCTvExpenseOn.setText(getString(R.string.expense_on) + " " + mExpenseDate.getFormattedDate());
            AppLog.d("ViewExpense", "ExpenseDate:" + mExpenseDate.getTimeInMillis() + ":: Created Date:" + mExpense.getCreatedOn() + ":: Expense:" + mExpense.getExpenseDate());
            if (!isAddedOnDiffDate) {
                try {
                    mCTvAddedOn.setText(getString(R.string.added_on) + " " + AppUtil.dateDBToString(mExpense.getCreatedOn()));
                    mCTvAddedOn.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    e.printStackTrace();
                    mCTvAddedOn.setVisibility(View.GONE);
                }
            }

            if (isUpdated) {
                try {
                    mCTvLastUpdatedOn.setText(getString(R.string.last_updated_on) + " " + AppUtil.dateDBToString(mExpense.getUpdatedOn()));
                    mCTvLastUpdatedOn.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    e.printStackTrace();
                    mCTvLastUpdatedOn.setVisibility(View.GONE);
                }
            }

            mCTvPaidBy.setText(AppUtil.getPaidByForKey(mExpense.getPaymentTypeKey()));
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
        mPbLoading.setVisibility(View.VISIBLE);
        FirebaseDB.initDb().removeExpense(key, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                mPbLoading.setVisibility(View.GONE);
                if (null == databaseError) {
                    AppUtil.showToast(R.string.expense_deleted_successfully);
                    setResult(RESULT_OK);
                    finish();

                } else {
                    AppUtil.showSnackbar(mViewComplete, "Unable to delete the expense.");
                }
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
        finish();
    }
}
