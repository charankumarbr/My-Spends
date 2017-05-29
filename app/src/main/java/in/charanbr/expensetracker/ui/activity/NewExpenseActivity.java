package in.charanbr.expensetracker.ui.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Calendar;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.customview.MoneyValueFilter;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.model.Expense;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.model.PaymentType;
import in.charanbr.expensetracker.ui.fragment.AddPaymentTypeFragment;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppLog;
import in.charanbr.expensetracker.util.AppPref;
import in.charanbr.expensetracker.util.AppUtil;

/**
 * Created by Charan.Br on 4/11/2017.
 */

public final class NewExpenseActivity extends BaseActivity implements AddPaymentTypeFragment.OnPaymentTypeListener {

    private Expense mExpense = null;

    private ExpenseDate mExpenseDate;

    private FlexboxLayout mFlexboxLayout = null;

    private CustomTextView mCTvExpenseDate = null;

    private TextInputEditText mTIEtAmount = null;
    private TextInputEditText mTIEtNote = null;

    private CheckBox mCbAddAnotherExpense;

    private int mSelectedTypeId = -1;
    private int mOkStatus = RESULT_CANCELED;

    private boolean isNew = false;

    private View mVEditDate = null;

    private boolean mViaNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
            startActivity(new Intent(NewExpenseActivity.this, MainActivity.class));
            finish();

        } else {
            if (getIntent().hasExtra(AppConstants.Bundle.EXPENSE)) {
                mExpense = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE);
                isNew = false;

            } else if (getIntent().hasExtra(AppConstants.Bundle.EXPENSE_DATE)) {
                mExpenseDate = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE_DATE);
                isNew = true;

            } else {
                finish();
            }

            mViaNotification = getIntent().getBooleanExtra(AppConstants.Bundle.VIA_NOTIFICATION, false);
            AppLog.d("TimeInMillis", getIntent().getLongExtra("check", 100L) + ":: millisCheck");

            setContentView(R.layout.activity_new_expense);
            init();
        }
    }

    private void init() {
        initLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
        toolbar.setTitle(isNew ? "Add Expense" : "Edit Expense");
        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mCTvExpenseDate = (CustomTextView) findViewById(R.id.ane_ctextview_expense_date);
        CustomTextView cTvCurrencySymbol = (CustomTextView) findViewById(R.id.ane_ctextview_currency);
        cTvCurrencySymbol.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY));

        findViewById(R.id.ane_ctextview_add_new_payment).setOnClickListener(clickListener);

        mTIEtAmount = (TextInputEditText) findViewById(R.id.ane_tiedittext_expense_amount);
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(10);
        mTIEtAmount.setFilters(new InputFilter[]{new MoneyValueFilter(), lengthFilter});

        mTIEtNote = (TextInputEditText) findViewById(R.id.ane_tiedittext_expense_note);
        mTIEtNote.setOnEditorActionListener(onEditorActionListener);

        mCbAddAnotherExpense = (CheckBox) findViewById(R.id.ane_checkbox_add_another);

        mVEditDate = findViewById(R.id.ane_imageview_edit_date);

        mFlexboxLayout = (FlexboxLayout) findViewById(R.id.ane_fblayout_payment_mode);
        getPaymentTypes();

        if (isNew) {
            mCTvExpenseDate.setText(getString(R.string.expense_on) + " " + mExpenseDate.getFormattedDate());
            mVEditDate.setVisibility(View.GONE);

        } else {
            mTIEtAmount.append(AppUtil.getStringAmount(mExpense.getAmount()));
            mTIEtAmount.requestFocus();
            AppUtil.toggleKeyboard(true);
            //DecimalFormat df = new DecimalFormat("0.00"); df.format(mExpense.getAmount());

            mTIEtNote.setText(mExpense.getNote());
            mCTvExpenseDate.setText(getString(R.string.expense_on) + " " + mExpense.getExpenseDate().getFormattedDate());

            mVEditDate.setVisibility(View.VISIBLE);
            mVEditDate.setOnClickListener(clickListener);
        }
    }

    private void getPaymentTypes() {
        mFlexboxLayout.removeAllViews();
        ArrayList<PaymentType> paymentTypes = DBManager.getPaymentTypes(true);
        if (null != paymentTypes) {
            LayoutInflater inflater = LayoutInflater.from(NewExpenseActivity.this);
            for (int index = 0; index < paymentTypes.size(); index++) {
                RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_radio_button, null);
                radioButton.setId(index);
                radioButton.setTag(paymentTypes.get(index).getId());
                radioButton.setText(paymentTypes.get(index).getName());
                if (!isNew && paymentTypes.get(index).getId() == mExpense.getPaymentTypePriId()) {
                    radioButton.setChecked(true);

                } else {
                    radioButton.setChecked(false);
                }
                radioButton.setOnCheckedChangeListener(paymentModeSelectedListener);
                mFlexboxLayout.addView(radioButton);
            }
        }
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ane_imageview_edit_date) {

                final DatePickerDialog datePickerDialog;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    datePickerDialog = new DatePickerDialog(NewExpenseActivity.this);
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("DateN", "Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            mExpenseDate = new ExpenseDate(dayOfMonth, month, year);
                            mCTvExpenseDate.setText(getString(R.string.expense_on) + " " + mExpenseDate.getFormattedDate());
                        }
                    });

                } else {
                    Calendar calendar = Calendar.getInstance();
                    datePickerDialog = new DatePickerDialog(NewExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            mExpenseDate = new ExpenseDate(dayOfMonth, month, year);
                            mCTvExpenseDate.setText(getString(R.string.expense_on) + " " + mExpenseDate.getFormattedDate());
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.setOwnerActivity(NewExpenseActivity.this);
                //datePickerDialog.setTitle("Select the date of expense");
                LayoutInflater inflater = LayoutInflater.from(NewExpenseActivity.this);
                View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
                datePickerDialog.setCustomTitle(customTitleView);
                datePickerDialog.show();

            } else if (v.getId() == R.id.ane_ctextview_add_new_payment) {
                showPaymentTypeDialog();
            }
        }
    };

    private void showPaymentTypeDialog() {
        AppUtil.toggleKeyboard(false);
        AddPaymentTypeFragment addPaymentTypeFragment = AddPaymentTypeFragment.newInstance();
        addPaymentTypeFragment.show(getSupportFragmentManager(), "AddPaymentTFragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.menu_done) {
            doneClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isEdited()) {
            confirmDiscard();

        } else {
            closeActivity();
        }
    }

    private void closeActivity() {
        AppUtil.toggleKeyboard(false);
        if (mViaNotification) {
            /*Intent upIntent = NavUtils.getParentActivityIntent(this);
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();*/
            startActivity(new Intent(NewExpenseActivity.this, MainActivity.class));

        } else {
            setResult(mOkStatus);
        }
        finish();
    }

    private void doneClicked() {
        if (isEdited()) {
            if (validate()) {
                saveChanges();
            }

        } else {
            if (isNew) {
                AppUtil.showSnackbar(mViewComplete, "Provide the expense details!");

            } else {
                closeActivity();
            }
        }
    }

    private final CompoundButton.OnCheckedChangeListener paymentModeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            resetAllPaymentTypes();
            AppLog.d("CheckedChange", "Checked:" + isChecked + "::Title:" + buttonView.getText());
            if (isChecked) {
                buttonView.setChecked(true);
                mSelectedTypeId = (int) buttonView.getTag();
                AppLog.d("AddExpense", "TypeId:" + mSelectedTypeId + "::Title:" + buttonView.getText());
            }
        }
    };

    private final EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doneClicked();
                return true;
            }
            return false;
        }
    };

    private void resetAll() {
        mTIEtAmount.setText("");
        mSelectedTypeId = -1;
        resetAllPaymentTypes();
        mTIEtNote.setText("");
    }

    private void resetAllPaymentTypes() {
        for (int index = 0; index < mFlexboxLayout.getChildCount(); index++) {
            if (mFlexboxLayout.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayout.getChildAt(index)).setChecked(false);
            }
        }
    }

    private void saveChanges() {
        if (isNew) {
            Expense expense = new Expense();
            String amount = AppUtil.getStringAmount(mTIEtAmount.getText().toString());
            expense.setAmount(amount);
            expense.setCreatedOn(AppUtil.convertToDateDB(System.currentTimeMillis()));
            expense.setExpenseDate(mExpenseDate);
            expense.setNote(mTIEtNote.getText().toString().trim().length() == 0 ? AppConstants.BLANK_NOTE_TEMPLATE : mTIEtNote.getText().toString().trim());
            expense.setPaymentTypePriId(mSelectedTypeId);

            if (DBManager.addExpense(expense) != -1) {
                AppUtil.showToast("Expense tracked!");
                AppUtil.toggleKeyboard(false);
                mOkStatus = RESULT_OK;
                if (!mCbAddAnotherExpense.isChecked()) {
                    setResult(RESULT_OK);
                    finish();

                } else {
                    resetAll();
                }

            } else {
                AppUtil.showSnackbar(mViewComplete, "Could not add this Expense!");
            }

        } else {
            String amount = AppUtil.getStringAmount(mTIEtAmount.getText().toString());
            if (!mExpense.getAmount().equals(amount)) {
                mExpense.setAmount(amount);
            }

            if (null != mExpenseDate) {
                mExpense.setExpenseDate(mExpenseDate);
            }

            if (!mExpense.getNote().equals(mTIEtNote.getText().toString())) {
                mExpense.setNote(mTIEtNote.getText().toString().trim().length() == 0 ? AppConstants.BLANK_NOTE_TEMPLATE : mTIEtNote.getText().toString().trim());
            }

            if (mSelectedTypeId != -1 && mExpense.getPaymentTypePriId() != mSelectedTypeId) {
                mExpense.setPaymentTypePriId(mSelectedTypeId);
            }

            int updateCount = DBManager.updateExpense(mExpense);
            if (updateCount == 1) {
                AppUtil.showToast("Expense updated!");
                AppUtil.toggleKeyboard(false);
                mOkStatus = RESULT_OK;
                if (!mCbAddAnotherExpense.isChecked()) {
                    setResult(RESULT_OK);
                    finish();

                } else {
                    resetAll();
                }
            }
        }
    }

    private boolean validate() {
        String amount = mTIEtAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            AppUtil.showSnackbar(mViewComplete, "Amount is must for an expense");
            return false;
        }

        if (isNew) {
            if (mSelectedTypeId == -1) {
                AppUtil.showSnackbar(mViewComplete, "Select payment type for this expense!");
                return false;
            }
        }

        try {
            Float amt = Float.valueOf(amount);
            if (amt == 0f) {
                AppUtil.showSnackbar(mViewComplete, "Zero amount expense!");
                return false;
            }
        } catch (NumberFormatException e) {
            AppUtil.showSnackbar(mViewComplete, "DIGITS Oopssss!!!");
            return false;
        }

        String note = mTIEtNote.getText().toString();
        String[] restrictedChars = {":", "\"", "\\", "*", "%"};
        for (String restrictedChar : restrictedChars) {
            if (note.contains(restrictedChar)) {
                AppUtil.showSnackbar(mViewComplete, "Enter valid description!");
                return false;
            }
        }

        return true;
    }

    private void confirmDiscard() {
        AlertDialog.Builder confirmDiscard = new AlertDialog.Builder(NewExpenseActivity.this);
        confirmDiscard.setTitle(R.string.discard_changes);
        confirmDiscard.setMessage(R.string.confirm_discard_message);
        confirmDiscard.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                closeActivity();
            }
        });

        confirmDiscard.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmDiscard.setCancelable(true);
        confirmDiscard.create().show();
    }

    private boolean isEdited() {

        if (isNew) {

            if (!TextUtils.isEmpty(mTIEtAmount.getText())) {
                return true;
            }

            if (mSelectedTypeId != -1) {
                return true;
            }

            if (!TextUtils.isEmpty(mTIEtNote.getText())) {
                return true;
            }

        } else {
            if (null != mExpense) {

                if (null != mExpenseDate && !mExpense.getExpenseDate().toString().equals(mExpenseDate.toString())) {
                    return true;
                }

                if (!TextUtils.isEmpty(mTIEtAmount.getText())) {
                    String amount = AppUtil.getStringAmount(mTIEtAmount.getText().toString());
                    if (!mExpense.getAmount().equals(amount)) {
                        return true;
                    }
                } else {
                    return true;
                }

                if (mSelectedTypeId != -1 && mExpense.getPaymentTypePriId() != mSelectedTypeId) {
                    return true;
                }

                if (!mExpense.getNote().equals(mTIEtNote.getText().toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onPaymentTypeAdded() {
        getPaymentTypes();
    }
}
