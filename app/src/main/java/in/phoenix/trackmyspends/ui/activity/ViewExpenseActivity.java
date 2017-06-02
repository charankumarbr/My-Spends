package in.phoenix.trackmyspends.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.ParseException;

import in.phoenix.trackmyspends.R;
import in.phoenix.trackmyspends.customview.CustomTextView;
import in.phoenix.trackmyspends.database.DBManager;
import in.phoenix.trackmyspends.model.Expense;
import in.phoenix.trackmyspends.util.AppConstants;
import in.phoenix.trackmyspends.util.AppLog;
import in.phoenix.trackmyspends.util.AppPref;
import in.phoenix.trackmyspends.util.AppUtil;

/**
 * Created by Charan.Br on 3/2/2017.
 */
public class ViewExpenseActivity extends BaseActivity {

    private boolean isNew = false;

    private int mExpensePrimaryKey = -1;

    private Expense mExpense = null;

    private CustomTextView mCTvAmount;
    private CustomTextView mCTvExpenseOn;
    private CustomTextView mCTvAddedOn;
    private CustomTextView mCTvLastUpdatedOn;
    private CustomTextView mCTvPaidBy;
    private CustomTextView mCTvNote;

    private int mResultCode = RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((null == getIntent()) || !getIntent().hasExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY)) {
            onDestroy();
        }
        mExpensePrimaryKey = getIntent().getIntExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY, -1);
        if (mExpensePrimaryKey < 0) {
            onDestroy();
        }
        isNew = (mExpensePrimaryKey == 0);

        setContentView(R.layout.activity_view_expense);
        init();

        if (!isNew) {
            getParticularExpense();
        }
    }

    private void init() {
        initLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
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
    }

    private void getParticularExpense() {
        mExpense = DBManager.getExpense(mExpensePrimaryKey);
        if (null != mExpense) {
            mCTvAmount.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)
                    + " " + AppUtil.getStringAmount(mExpense.getAmount()));
            mCTvNote.setText(mExpense.getNote());
            boolean isAddedOnDiffDate = mExpense.getExpenseDate().isSameExpenseDate(mExpense.getCreatedOn());
            boolean isUpdated = !mExpense.getCreatedOn().equals(mExpense.getUpdatedOn());

            mCTvExpenseOn.setText(getString(R.string.expense_on) + " " + mExpense.getExpenseDate().getFormattedDate());
            if (isAddedOnDiffDate) {
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

            mCTvPaidBy.setText(DBManager.getPaymentTypeName(mExpense.getPaymentTypePriId()));
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
                int delCount = DBManager.removeExpense(mExpense.getId());
                AppLog.d(ViewExpenseActivity.this.getLocalClassName(), "Delete:" + delCount);
                if (delCount == 1) {
                    AppUtil.showToast(R.string.expense_deleted_successfully);
                }
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.EDIT_EXPENSE_CODE) {
            if (resultCode == RESULT_OK) {
                mResultCode = RESULT_OK;
                getParticularExpense();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(mResultCode);
        finish();
    }
}
