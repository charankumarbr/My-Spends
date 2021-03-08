package in.phoenix.myspends.ui.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.CustomSpinnerAdapter;
import in.phoenix.myspends.customview.MoneyValueFilter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.CategoryParser;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.myspends.util.AppAnalytics;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppCrashLogger;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/11/2017.
 */

public final class NewExpenseActivity extends BaseActivity implements AddPaymentTypeFragment.
        OnPaymentTypeListener, PaymentTypeParser.PaymentTypeParserListener, CategoryParser.CategoryParserListener {

    private NewExpense mExpense = null;

    private ExpenseDate mExpenseDate;

    //private FlexboxLayout mFlexboxLayout = null;

    private TextView mCTvExpenseDate = null;

    private TextInputEditText mTIEtAmount = null;
    private TextInputEditText mTIEtNote = null;

    private CheckBox mCbAddAnotherExpense;

    private String mSelectedTypeKey = null;
    private int mOkStatus = RESULT_CANCELED;

    private boolean isNew = false;

    //private View mVEditDate = null;

    private boolean mViaNotification = false;

    private ProgressBar mPbLoading = null;

    private int mPaymentTypeCount = -1;

    private Spinner mSpnrPaidBy = null;
    private Spinner mSpnrCategory = null;

    private int mSelectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViaNotification = getIntent().getBooleanExtra(AppConstants.Bundle.VIA_NOTIFICATION, false);
        AppLog.d(getClass().getSimpleName(), "Logged in Status:" + AppUtil.isUserLoggedIn() + ":: viaNotif" + mViaNotification);
        if (!AppUtil.isUserLoggedIn()) {
            finish();

        } else {
            if (getIntent().hasExtra(AppConstants.Bundle.EXPENSE)) {
                mExpense = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE);
                isNew = false;

            } else if (getIntent().hasExtra(AppConstants.Bundle.EXPENSE_DATE)) {
                mExpenseDate = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE_DATE);
                isNew = true;

            } else {
                if (AppUtil.isUserLoggedIn()) {
                    mExpenseDate = AppUtil.convertToDate(System.currentTimeMillis());
                    isNew = true;

                } else {
                    finish();
                }
            }

            setContentView(R.layout.activity_new_expense);
            init();
        }
    }

    private void init() {
        initLayout();
        Toolbar toolbar = findViewById(R.id.ane_toolbar);
        toolbar.setTitle(isNew ? "Add Expense" : "Edit Expense");
        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mCTvExpenseDate = findViewById(R.id.ane_tv_expense_date);
        TextView cTvCurrencySymbol = findViewById(R.id.ane_ctextview_currency);
        cTvCurrencySymbol.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY));

        findViewById(R.id.ane_ctextview_add_new_payment).setOnClickListener(clickListener);

        mTIEtAmount = findViewById(R.id.ane_tiedittext_expense_amount);
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(10);
        mTIEtAmount.setFilters(new InputFilter[]{new MoneyValueFilter(), lengthFilter});

        mTIEtNote = findViewById(R.id.ane_tiedittext_expense_note);
        mTIEtNote.setOnEditorActionListener(onEditorActionListener);

        mCbAddAnotherExpense = findViewById(R.id.ane_checkbox_add_another);

        //mVEditDate = findViewById(R.id.ane_imageview_edit_date);

        //mFlexboxLayout = (FlexboxLayout) findViewById(R.id.ane_fblayout_payment_mode);

        mPbLoading = findViewById(R.id.ane_pb_loading);

        mSpnrPaidBy = findViewById(R.id.ane_spnr_paid_by);
        mSpnrCategory = findViewById(R.id.ane_spnr_category);

        getPaymentTypes();
        getCategories();

        if (isNew) {
            mCTvExpenseDate.setText(/*getString(R.string.expense_on) + " " + */mExpenseDate.getFormattedDate());
            //mVEditDate.setVisibility(View.VISIBLE);
            //mVEditDate.setOnClickListener(clickListener);
            mCbAddAnotherExpense.setVisibility(View.VISIBLE);

        } else {
            mTIEtAmount.append(AppUtil.getStringAmount(String.valueOf(mExpense.getAmount())));
            mTIEtAmount.requestFocus();
            AppUtil.toggleKeyboard(mViewComplete, true);
            mCbAddAnotherExpense.setVisibility(View.GONE);
            //DecimalFormat df = new DecimalFormat("0.00"); df.format(mExpense.getAmount());

            mTIEtNote.setText(mExpense.getNote());
            mExpenseDate = new ExpenseDate(mExpense.getExpenseDate());
            mCTvExpenseDate.setText(/*getString(R.string.expense_on) + " " + */mExpenseDate.getFormattedDate());

            /*mVEditDate.setVisibility(View.VISIBLE);
            mVEditDate.setOnClickListener(clickListener);*/
        }

        mCTvExpenseDate.setOnClickListener(clickListener);
    }

    private void getCategories() {
        FirebaseDB.initDb().getExpenseCategories(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    new CategoryParser(NewExpenseActivity.this).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                } else {
                    AppLog.d("NewExpense", "ZERO Categories");
                    onCategoriesParsed(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onCategoriesParsed(null);
            }
        });
    }

    private void getPaymentTypes() {
        //mFlexboxLayout.removeAllViews();
        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("NewExpenseActivity", "Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(NewExpenseActivity.this, true).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onPaymentTypesParsed(null, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mPaymentTypeCount = -1;
                if (null != databaseError) {
                    AppLog.d("NewExpenseActivity", "Payment Types Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("NewExpenseActivity", "Payment Types Error!");
                }
                AppUtil.showToast("Unable to fetch payment types.");
                finish();
            }
        });
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ane_tv_expense_date) {

                final DatePickerDialog datePickerDialog;
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    datePickerDialog = new DatePickerDialog(NewExpenseActivity.this);
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("DateN", "Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            mExpenseDate = new ExpenseDate(dayOfMonth, month, year);
                            mCTvExpenseDate.setText(/*getString(R.string.expense_on) + " " + */mExpenseDate.getFormattedDate());
                        }
                    });

                } else {
                    datePickerDialog = new DatePickerDialog(NewExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            mExpenseDate = new ExpenseDate(dayOfMonth, month, year);
                            mCTvExpenseDate.setText(/*getString(R.string.expense_on) + " " + */mExpenseDate.getFormattedDate());
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.setOwnerActivity(NewExpenseActivity.this);
                //datePickerDialog.setTitle("Select the date of expense");
                LayoutInflater inflater = LayoutInflater.from(NewExpenseActivity.this);
                View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
                datePickerDialog.setCustomTitle(customTitleView);
                datePickerDialog.show();
                AppUtil.toggleKeyboard(mViewComplete, false);

            } else if (v.getId() == R.id.ane_ctextview_add_new_payment) {

                if (mPaymentTypeCount <= 0) {
                    AppUtil.showToast("Unable to add new Payment Types. Please try later.");

                } else if (mPaymentTypeCount == AppConstants.MAX_PAYMENT_TYPE_COUNT) {
                    AppUtil.showToast("Reached maximum count of Payment Types.");

                } else {
                    showPaymentTypeDialog();
                }
            }
        }
    };

    private void showPaymentTypeDialog() {
        AppUtil.toggleKeyboard(mViewComplete, false);
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
            closeActivity();
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
        AppUtil.toggleKeyboard(mViewComplete, false);
        if (mViaNotification) {
            /*Intent upIntent = NavUtils.getParentActivityIntent(this);
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();*/
            //startActivity(new Intent(NewExpenseActivity.this, MainActivity.class));

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
                mSelectedTypeKey = (String) buttonView.getTag();
                AppLog.d("AddExpense", "TypeIdKey:" + mSelectedTypeKey + "::Title:" + buttonView.getText());
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
        mTIEtNote.setText("");
        mTIEtAmount.requestFocus();
        resetAllPaymentTypes();
    }

    private void resetAllPaymentTypes() {
        mSpnrPaidBy.setSelection(0);
        mSelectedTypeKey = null;
        mSpnrCategory.setSelection(0);
        mSelectedCategoryId = -1;
    }

    private void saveChanges() {

        if (mPbLoading.getVisibility() == View.VISIBLE) {
            return;
        }

        if (isNew) {
            if (AppUtil.isConnected()) {
                if (AppUtil.isUserLoggedIn()) {
                    final NewExpense newExpense = new NewExpense();
                    final long timeMillis = System.currentTimeMillis();
                    newExpense.setAmount(AppUtil.getFloatAmount(mTIEtAmount.getText().toString()));
                    newExpense.setCreatedOn(timeMillis);
                    newExpense.setExpenseDate(mExpenseDate.getTimeInMillis());
                    newExpense.setNote(mTIEtNote.getText().toString().trim().length() == 0 ? "" : mTIEtNote.getText().toString().trim());
                    newExpense.setUpdatedOn(timeMillis);
                    newExpense.setPaymentTypeKey(mSelectedTypeKey);
                    newExpense.setCategoryId(mSelectedCategoryId);

                    //mPbLoading.setVisibility(View.VISIBLE);
                    AppDialog.showDialog(NewExpenseActivity.this, "Adding new spend...");
                    FirebaseDB.initDb().addFsNewSpend(newExpense, new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //mPbLoading.setVisibility(View.GONE);
                            AppDialog.dismissDialog();
                            AppLog.d("NewExpense", "OnSuccess: Documentreference Id:" + documentReference.getId());
                            AppLog.d("NewExpense", "OnSuccess: Documentreference Path:" + documentReference.getPath());
                            AppUtil.showToast("Expense tracked!");
                            mOkStatus = RESULT_OK;

                            Bundle eventBundle = new Bundle();
                            eventBundle.putBoolean("is_another", mCbAddAnotherExpense.isChecked());
                            eventBundle.putInt("category_id", newExpense.getCategoryId());
                            AppAnalytics.init().logEvent("added_expense", eventBundle);

                            AppPref.getInstance().putInt(AppConstants.PrefConstants.SPENDS_ADDED, 1);
                            if (!mCbAddAnotherExpense.isChecked()) {
                                AppUtil.toggleKeyboard(mViewComplete, false);
                                /*setResult(RESULT_OK);
                                finish();*/
                                closeActivity();

                            } else {
                                resetAll();
                            }
                        }
                    }, e -> {
                        //mPbLoading.setVisibility(View.GONE);
                        AppDialog.dismissDialog();
                        AppUtil.showSnackbar(mViewComplete, "Could not add this Expense!");
                        AppLog.d("NewExpense", "OnFailure: Exception", e);
                        AppCrashLogger.INSTANCE.reportException(e);
                    });

                } else {
                    //AppUtil.showToast("Not logged in");
                    AppUtil.showSnackbar(mViewComplete, "Not logged in.");
                }

            } else {
                //AppUtil.showToast("No internet!");
                AppUtil.showSnackbar(mViewComplete, R.string.no_internet);
            }

        } else {
            boolean status = false;
            Float amount = AppUtil.getFloatAmount(mTIEtAmount.getText().toString());
            if (Float.compare(mExpense.getAmount(), amount) != 0) {
                mExpense.setAmount(amount);
                status = true;
            }

            if (null != mExpenseDate && !mExpenseDate.isSameExpenseDate(mExpense.getExpenseDate())) {
                mExpense.setExpenseDate(mExpenseDate.getTimeInMillis());
                status = true;
            }

            if (!mExpense.getNote().equals(mTIEtNote.getText().toString())) {
                mExpense.setNote(mTIEtNote.getText().toString().trim().length() == 0 ? "" : mTIEtNote.getText().toString().trim());
                status = true;
            }

            if (null != mSelectedTypeKey && !mExpense.getPaymentTypeKey().equals(mSelectedTypeKey)) {
                mExpense.setPaymentTypeKey(mSelectedTypeKey);
                status = true;
            }

            if (mSelectedCategoryId != -1 && mExpense.getCategoryId() != mSelectedCategoryId) {
                mExpense.setCategoryId(mSelectedCategoryId);
            }

            if (status) {
                mExpense.setUpdatedOn(System.currentTimeMillis());
            }
            AppLog.d("NewExpense", "Edited expense:" + mExpense.getId() + ":" + mExpense.getPaymentTypeKey()
                    + ":" + mExpense.getExpenseDate() + ":" + mExpense.getCreatedOn() + ":" + mExpense.getNote() + ":"
                    + mExpense.getAmount() + ":" + mExpense.getUpdatedOn() + ":" + mExpense.getCategoryId());

            if (AppUtil.isConnected()) {
                if (AppUtil.isUserLoggedIn()) {
                    //mPbLoading.setVisibility(View.VISIBLE);
                    AppUtil.toggleKeyboard(mViewComplete, false);
                    AppDialog.showDialog(NewExpenseActivity.this, "Updating your spend...");
                    FirebaseDB.initDb().updateFsExpense(mExpense, new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            AppLog.d("NewExpense", "Edit: onSuccess");
                            //mPbLoading.setVisibility(View.GONE);
                            AppDialog.dismissDialog();
                            AppUtil.showToast("Updated.");
                            AppUtil.toggleKeyboard(mViewComplete, false);
                            mOkStatus = RESULT_OK;

                            AppPref.getInstance().putInt(AppConstants.PrefConstants.SPENDS_ADDED, 1);

                            Intent backIntent = new Intent();
                            backIntent.putExtra(AppConstants.Bundle.EXPENSE, mExpense);
                            setResult(RESULT_OK, backIntent);
                            finish();
                        }
                    }, e -> {
                        //mPbLoading.setVisibility(View.GONE);
                        AppDialog.dismissDialog();
                        AppLog.d("NewExpense", "Edit: onFailure");
                        AppUtil.showToast("Unable to update.");
                        AppUtil.toggleKeyboard(mViewComplete, false);
                    });
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
            if (mSelectedTypeKey == null) {
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
            AppUtil.showSnackbar(mViewComplete, "Amount can be only number!!");
            AppCrashLogger.INSTANCE.reportException(e);
            return false;
        }

        if (isNew) {
            if (mSelectedCategoryId == -1) {
                AppUtil.showSnackbar(mViewComplete, "Select category for this expense!");
                return false;
            }
        }

        String note = mTIEtNote.getText().toString();
        if (AppUtil.doesContainRestrictedChar(note)) {
            AppUtil.showSnackbar(mViewComplete, "Enter valid description!");
            return false;
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

        AppLog.d("NewExpense", "Backpress: isNew:" + isNew);
        if (isNew) {

            if (!TextUtils.isEmpty(mTIEtAmount.getText())) {
                return true;
            }

            if (mSelectedTypeKey != null) {
                return true;
            }

            if (mSelectedCategoryId != -1) {
                return true;
            }

            return !TextUtils.isEmpty(mTIEtNote.getText());

        } else {
            if (null != mExpense) {

                if (null != mExpenseDate && !mExpenseDate.isSameExpenseDate(mExpense.getExpenseDate())) {
                    AppLog.d("NewExpense", "Backpress: Date changed!");
                    AppLog.d("NewExpense", "Expense:" + mExpense.getExpenseDate() + ":: ExpenseDate:" + mExpenseDate.getTimeInMillis());
                    return true;
                }

                if (!TextUtils.isEmpty(mTIEtAmount.getText())) {
                    Float amount = AppUtil.getFloatAmount(mTIEtAmount.getText().toString());
                    AppLog.d("NewExpense", "Backpress: Amount Compare:" + Float.compare(mExpense.getAmount(), amount));
                    if (Float.compare(mExpense.getAmount(), amount) != 0) {
                        AppLog.d("NewExpense", "Backpress: amount changed!");
                        AppLog.d("NewExpense", "Expense:" + mExpense.getAmount() + ":: Entered:" + amount);
                        return true;
                    }
                } else {
                    AppLog.d("NewExpense", "Backpress: No Amount!");
                    return true;
                }

                if (null != mSelectedTypeKey && !mExpense.getPaymentTypeKey().equals(mSelectedTypeKey)) {
                    return true;
                }

                if (mSelectedCategoryId != -1 && mExpense.getCategoryId() != mSelectedCategoryId) {
                    return true;
                }

                if (!mExpense.getNote().equals(mTIEtNote.getText().toString())) {
                    AppLog.d("NewExpense", "Backpress: Note changed!");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onPaymentTypeAdded() {
        AppUtil.showSnackbar(mViewComplete, "Payment type added!");
        //getPaymentTypes();
        mViewComplete.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag("AddPaymentTFragment");
                if (dialogFragment instanceof AddPaymentTypeFragment) {
                    ((AddPaymentTypeFragment) dialogFragment).dismissAllowingStateLoss();
                }

                getPaymentTypes();
            }
        }, 600);
    }

    @Override
    public void onPaymentTypesParsed(ArrayList<PaymentType> paymentTypes, boolean isCashPaymentTypeAdded) {

        /*if (null == paymentTypes && !isCashPaymentTypeAdded) {
            AppUtil.showToast("Unable to fetch payment types!");
            finish();
            return;
        }*/

        if (null == paymentTypes) {
            paymentTypes = new ArrayList<>();
        }

        if (!isCashPaymentTypeAdded) {
            paymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        if (paymentTypes.size() == 0) {
            AppUtil.showToast("Unable to fetch payment types!");
            finish();
            return;
        }

        if (null != paymentTypes && paymentTypes.size() > 0) {
            mPaymentTypeCount = paymentTypes.size();
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(NewExpenseActivity.this,
                    R.layout.layout_spinner_selected, paymentTypes);
            adapter.setSelectionText("Select Paid by");
            mSpnrPaidBy.setAdapter(adapter);

            if (!isNew) {
                for (int index = 0; index < paymentTypes.size(); index++) {
                    PaymentType paymentType = paymentTypes.get(index);
                    if (paymentType.getKey().equals(mExpense.getPaymentTypeKey())) {
                        mSpnrPaidBy.setSelection(index + 1);
                        break;
                    }
                }
            }

            mSpnrPaidBy.setOnItemSelectedListener(mPaidBySelectedListener);

        } else {
            mPaymentTypeCount = -1;
        }
    }

    private AdapterView.OnItemSelectedListener mPaidBySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            AppLog.d("Paid By Listener", "onItemSelected:");
            mSelectedTypeKey = (String) view.getTag();
            AppLog.d("AddExpense", "TypeId Key:" + mSelectedTypeKey);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            AppLog.d("Paid By Listener", "onNothing:");
            mSelectedTypeKey = null;
        }
    };

    private AdapterView.OnItemSelectedListener mCategoryListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedCategoryId = (int) view.getTag();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            mSelectedCategoryId = -1;
        }
    };

    @Override
    public void onCategoriesParsed(ArrayList<Category> allCategories) {
        if (null != allCategories && allCategories.size() > 0) {
            CustomSpinnerAdapter categoryAdapter = new CustomSpinnerAdapter(NewExpenseActivity.this,
                    R.layout.layout_spinner_selected, allCategories);
            categoryAdapter.setSelectionText("Select Category");
            mSpnrCategory.setAdapter(categoryAdapter);

            if (!isNew) {
                int selectionIndex = AppUtil.getPosOf(mExpense.getCategoryId(), allCategories);
                mSpnrCategory.setSelection(selectionIndex > 0 ? selectionIndex + 1 : 0);
            }
            mSpnrCategory.setOnItemSelectedListener(mCategoryListener);

        } else {
            AppUtil.showToast("Unable to fetch payment types!");
            finish();
        }
    }
}
