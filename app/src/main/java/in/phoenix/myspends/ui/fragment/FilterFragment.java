package in.phoenix.myspends.ui.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.CustomSpinnerAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.CategoryParser;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends DialogFragment implements PaymentTypeParser.PaymentTypeParserListener, CategoryParser.CategoryParserListener {

    private Context mContext;

    private OnFilterListener mListener;

    private TextInputLayout mTilFromDate;
    private TextInputLayout mTilToDate;

    private TextInputEditText mTietFromDate;
    private TextInputEditText mTietToDate;

    private long mFromMillis = 0;
    private long mToMillis = 0;

    private ExpenseDate mFromExpenseDate;
    private ExpenseDate mToExpenseDate;

    private ImageView mIvToDate;

    //private FlexboxLayout mFlexboxLayout = null;

    private String mSelectedPaymentKey = null;
    private int mSelectedCategoryId = -1;

    private Spinner mSpnrPaidBy = null;

    private Spinner mSpnrCategory = null;

    private CheckBox mCbGroupByCategory;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilterFragment.
     */
    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterListener) {
            mListener = (OnFilterListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterListener");
        }
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().setTitle("Filter");
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View filterView = inflater.inflate(R.layout.fragment_filter, container, false);

        filterView.findViewById(R.id.ff_abutton_view_spends).setOnClickListener(clickListener);
        filterView.findViewById(R.id.ff_imageview_from_date).setOnClickListener(clickListener);
        filterView.findViewById(R.id.ff_abutton_reset).setOnClickListener(clickListener);

        mIvToDate = filterView.findViewById(R.id.ff_imageview_to_date);
        mIvToDate.setOnClickListener(clickListener);

        mTietFromDate = filterView.findViewById(R.id.ff_tiet_from_date);
        mTietFromDate.setInputType(InputType.TYPE_NULL);
        mTietToDate = filterView.findViewById(R.id.ff_tiet_to_date);
        mTietToDate.setInputType(InputType.TYPE_NULL);

        mTilFromDate = filterView.findViewById(R.id.ff_til_from_date);
        mTilToDate = filterView.findViewById(R.id.ff_til_to_date);

        if (mFromMillis == 0) {
            mIvToDate.setEnabled(false);

        } else {
            mFromExpenseDate = new ExpenseDate(mFromMillis);
            mToExpenseDate = new ExpenseDate(mToMillis);
            mTietFromDate.setText(mFromExpenseDate.getFormattedDate());
            mTietToDate.setText(mToExpenseDate.getFormattedDate());
        }

        //mFlexboxLayout = (FlexboxLayout) filterView.findViewById(R.id.ff_fblayout_payment_mode);
        mSpnrPaidBy = filterView.findViewById(R.id.ff_spnr_paid_by);
        mCbGroupByCategory = filterView.findViewById(R.id.ff_checkbox_group_category);
        mSpnrCategory = filterView.findViewById(R.id.ff_spnr_category);
        filterView.post(new Runnable() {
            @Override
            public void run() {
                getAllPaymentTypes();
            }
        });

        return filterView;
    }

    private void getCategories() {
        FirebaseDB.initDb().getExpenseCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("FilterFragment", "getCategories::Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new CategoryParser(FilterFragment.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onCategoriesParsed(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (null != databaseError) {
                    AppLog.d("FilterFragment", "getCategories::Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("FilterFragment", "getCategories::Error!");
                }
                AppUtil.showToast("Unable to fetch categories.");
                onCategoriesParsed(null);
            }
        });
    }

    private void getAllPaymentTypes() {
        //mFlexboxLayout.removeAllViews();
        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("FilterFragment", "getAllPaymentTypes::Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(FilterFragment.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onPaymentTypesParsed(null, false);
                    }

                    getCategories();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (null != databaseError) {
                    AppLog.d("FilterFragment", "Payment Types Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("FilterFragment", "Payment Types Error!");
                }
                AppUtil.showToast("Unable to fetch payment types.");
                dismissAllowingStateLoss();
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ff_abutton_view_spends) {
                if (isDateSelected()) {
                    if (AppUtil.daysDiff(mFromMillis, mToMillis) <= 60) {
                        if (null != mListener) {
                            AppLog.d("FilterFragment", "clickListener: DaysDiff:" + AppUtil.daysDiff(mFromMillis, mToMillis));
                            boolean isGroupBy = mCbGroupByCategory.isChecked();
                            if (isGroupBy && mSelectedCategoryId > 0) {
                                isGroupBy = false;
                            }
                            mListener.onFilterChanged(mFromMillis, mToMillis, mSelectedPaymentKey, mSelectedCategoryId, isGroupBy);
                            dismissAllowingStateLoss();
                        }

                    } else {
                        AppUtil.showToast("Maximum date range allowed is 60 days.");
                    }
                }
            } else if (v.getId() == R.id.ff_imageview_from_date) {
                final DatePickerDialog datePickerDialog;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    datePickerDialog = new DatePickerDialog(mContext);
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "From::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            getFromDateMillis(dayOfMonth, month, year);
                        }
                    });

                } else {
                    Calendar calendar = Calendar.getInstance();
                    datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "From::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            getFromDateMillis(dayOfMonth, month, year);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                long currentMillis = System.currentTimeMillis();
                datePickerDialog.getDatePicker().setMaxDate(currentMillis);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
                ((TextView) customTitleView).setText(R.string.select_from_date);
                datePickerDialog.setCustomTitle(customTitleView);
                datePickerDialog.show();

            } else if (v.getId() == R.id.ff_imageview_to_date) {
                final DatePickerDialog datePickerDialog;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    datePickerDialog = new DatePickerDialog(mContext);
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "To::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            getToDateMillis(dayOfMonth, month, year);
                        }
                    });

                } else {
                    Calendar calendar = Calendar.getInstance();
                    datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            AppLog.d("Date", "To::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                            getToDateMillis(dayOfMonth, month, year);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.getDatePicker().setMinDate(mFromMillis);
                long currentMillis = System.currentTimeMillis();
                if (null != mFromExpenseDate) {
                    //-- not sure how the NPE crash came for mFromExpenseDate --//
                    datePickerDialog.getDatePicker().setMaxDate((currentMillis < mFromExpenseDate.reportToDateTimeInMillis())
                            ? currentMillis : mFromExpenseDate.reportToDateTimeInMillis());
                } else {
                    datePickerDialog.getDatePicker().setMaxDate(currentMillis);
                }
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
                ((TextView) customTitleView).setText(R.string.select_to_date);
                datePickerDialog.setCustomTitle(customTitleView);
                datePickerDialog.show();

            } else if (v.getId() == R.id.ff_abutton_reset) {
                resetDate();
                resetPaymentTypes();
                resetCategory();
                mCbGroupByCategory.setChecked(false);
            }
        }
    };

    private void resetCategory() {
        if (null != mSpnrCategory) {
            mSpnrCategory.setSelection(0);
        }
        mSelectedCategoryId = -1;
    }

    private void resetDate() {
        mTietFromDate.setText("");
        mFromExpenseDate = null;
        mFromMillis = 0;
        mTietToDate.setText("");
        mToExpenseDate = null;
        mToMillis = 0;
        mIvToDate.setEnabled(false);
    }

    private void getToDateMillis(int dayOfMonth, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        mToMillis = calendar.getTimeInMillis();
        if (null == mToExpenseDate) {
            mToExpenseDate = new ExpenseDate(dayOfMonth, month, year);

        } else {
            mToExpenseDate.changeDate(mToMillis);
        }
        mTietToDate.setText(mToExpenseDate.getFormattedDate());
    }

    private void getFromDateMillis(int dayOfMonth, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        mFromMillis = calendar.getTimeInMillis();
        if (null == mFromExpenseDate) {
            mFromExpenseDate = new ExpenseDate(dayOfMonth, month, year);

        } else {
            mFromExpenseDate.changeDate(mFromMillis);
        }
        mIvToDate.setEnabled(true);
        mTietFromDate.setText(mFromExpenseDate.getFormattedDate());
        mTietToDate.setText("");
        mToMillis = 0;
        mToExpenseDate = null;
    }

    private boolean isDateSelected() {

        boolean isValid = true;

        if (mFromMillis == 0) {
            mTilFromDate.setErrorEnabled(true);
            mTilFromDate.setError("From date is required");
            isValid = false;

        } else {
            mTilFromDate.setErrorEnabled(false);
        }

        if (mToMillis == 0) {
            mTilToDate.setErrorEnabled(true);
            mTilToDate.setError("To date is required");
            isValid = false;

        } else {
            mTilToDate.setErrorEnabled(false);
        }

        return isValid;
    }

    @Override
    public void onPaymentTypesParsed(ArrayList<PaymentType> paymentTypes, boolean isCashPaymentTypeAdded) {

        if (null == paymentTypes && !isCashPaymentTypeAdded) {
            AppUtil.showToast("Unable to fetch payment types!");
            return;
        }
        
        if (null == paymentTypes) {
            paymentTypes = new ArrayList<>();
        }

        if (!isCashPaymentTypeAdded) {
            paymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mContext,
                R.layout.layout_spinner_selected, paymentTypes);
        adapter.setSelectionText("Select Paid by");
        mSpnrPaidBy.setAdapter(adapter);
        mSpnrPaidBy.setOnItemSelectedListener(mPaidBySelectedListener);

        /*LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int index = 0; index < paymentTypes.size(); index++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_radio_button, null);
            radioButton.setId(index);
            radioButton.setTag(paymentTypes.get(index).getKey());
            radioButton.setText(paymentTypes.get(index).getName());
            if (null != mSelectedPaymentKey && mSelectedPaymentKey.contains(paymentTypes.get(index).getKey())) {
                radioButton.setChecked(true);

            } else {
                radioButton.setChecked(false);
            }
            radioButton.setOnCheckedChangeListener(paymentModeSelectedListener);
            mFlexboxLayout.addView(radioButton);
        }*/
    }

    private AdapterView.OnItemSelectedListener mPaidBySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedPaymentKey = (String) view.getTag();
            AppLog.d("FilterFragment", "PaymentType::TypeId:" + mSelectedPaymentKey);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            AppLog.d("FilterFragment", "Paid By Listener::onNothing:");
            mSelectedPaymentKey = null;
        }
    };

    private AdapterView.OnItemSelectedListener mCategoryListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedCategoryId = (int) view.getTag();
            AppLog.d("FilterFragment", "Category::TypeId:" + mSelectedCategoryId);
            if (mSelectedCategoryId > 0) {
                mCbGroupByCategory.setChecked(false);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            AppLog.d("FilterFragment", "mCategoryListener::onNothing:");
            mSelectedCategoryId = -1;
        }
    };

    private final CompoundButton.OnCheckedChangeListener paymentModeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            resetPaymentTypes();

            AppLog.d("CheckedChange", "Checked:" + isChecked + "::Title:" + buttonView.getText());
            if (isChecked) {
                buttonView.setChecked(true);
                mSelectedPaymentKey = (String) buttonView.getTag();
                AppLog.d("FilterFragment", "TypeId:" + mSelectedPaymentKey + "::Title:" + buttonView.getText());
            }
        }
    };

    private void resetPaymentTypes() {
        /*for (int index = 0; index < mFlexboxLayout.getChildCount(); index++) {
            if (mFlexboxLayout.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayout.getChildAt(index)).setChecked(false);
            }
        }*/
        if (null != mSpnrPaidBy) {
            mSpnrPaidBy.setSelection(0);
        }
        mSelectedPaymentKey = null;
    }

    @Override
    public void onCategoriesParsed(ArrayList<Category> allCategories) {
        if ((null == allCategories) || allCategories.size() == 0) {
            mSpnrCategory.setEnabled(false);
            return;
        }

        mSpnrCategory.setEnabled(true);
        CustomSpinnerAdapter categoryAdapter = new CustomSpinnerAdapter(mContext,
                R.layout.layout_spinner_selected, allCategories);
        categoryAdapter.setSelectionText("Select Category");
        mSpnrCategory.setAdapter(categoryAdapter);
        mSpnrCategory.setOnItemSelectedListener(mCategoryListener);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFilterListener {
        void onFilterChanged(long fromDate, long toDate, String paidByKey, int categoryId, boolean isGroupbyCategory);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
