package in.phoenix.myspends.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.customview.MoneyValueFilter;
import in.phoenix.myspends.database.DBManager;
import in.phoenix.myspends.model.Expense;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddExpenseFragment.OnAddExpenseListener} interface
 * to handle interaction events.
 * Use the {@link AddExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpenseFragment extends DialogFragment {

    private static final String ARG_EXPENSE_DATE = "expenseDate";

    private ExpenseDate mExpenseDate;

    private OnAddExpenseListener mListener;

    private FlexboxLayout mFlexboxLayout = null;

    private TextInputEditText mTIEtAmount = null;
    private TextInputEditText mTIEtNote = null;

    private int mSelectedTypeId = -1;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param expenseDate Parameter 1.
     * @return A new instance of fragment AddExpenseFragment.
     */
    public static AddExpenseFragment newInstance(ExpenseDate expenseDate) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXPENSE_DATE, expenseDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExpenseDate = getArguments().getParcelable(ARG_EXPENSE_DATE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addView = inflater.inflate(R.layout.fragment_add_expense, container, false);
        CustomTextView tvExpenseDate = (CustomTextView) addView.findViewById(R.id.fae_textview_expense_date);
        if (null != mExpenseDate) {
            tvExpenseDate.setText(mExpenseDate.getFormattedDate());
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvExpenseDate.setText(Html.fromHtml(mExpenseDate.getFormattedDate(), Html.FROM_HTML_MODE_LEGACY));

            } else {
                tvExpenseDate.setText(Html.fromHtml(mExpenseDate.getFormattedDate()));
            }*/

        } else {
            tvExpenseDate.setText("");
        }
        AppCompatButton btnSave = (AppCompatButton) addView.findViewById(R.id.fae_button_save);
        mTIEtAmount = (TextInputEditText) addView.findViewById(R.id.fae_tiedittext_expense_amount);
        mTIEtAmount.setFilters(new InputFilter[]{new MoneyValueFilter()});
        mTIEtNote = (TextInputEditText) addView.findViewById(R.id.fae_tiedittext_expense_note);

        ArrayList<PaymentType> paymentTypes = DBManager.getPaymentTypes(true);
        if (null != paymentTypes) {
            mFlexboxLayout = (FlexboxLayout) addView.findViewById(R.id.fae_fblayout_payment_mode);
            for (int index = 0; index < paymentTypes.size(); index++) {
                RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_radio_button, null);
                radioButton.setId(index);
                radioButton.setTag(paymentTypes.get(index).getId());
                radioButton.setText(paymentTypes.get(index).getName());
                radioButton.setOnCheckedChangeListener(paymentModeSelectedListener);
                mFlexboxLayout.addView(radioButton);
            }

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveBtnClicked();
                }
            });

        } else {
            btnSave.setEnabled(false);
        }

        mTIEtAmount.setFocusable(true);
        AppUtil.toggleKeyboard(true);

        mTIEtNote.setOnEditorActionListener(onEditorActionListener);

        return addView;
    }

    private void saveBtnClicked() {
        if (validate()) {
            addExpense();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddExpenseListener) {
            mListener = (OnAddExpenseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddExpenseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean validate() {

        String amount = mTIEtAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            AppUtil.showToast("Amount is must for an expense");
            return false;
        }

        if (mSelectedTypeId == -1) {
            AppUtil.showToast("Select payment type for this expense!");
            return false;
        }

        try {
            Float amt = Float.parseFloat(amount);
            if (amt == 0f) {
                AppUtil.showToast("Zero amount expense!");
                return false;
            }
        } catch (NumberFormatException e) {
            AppUtil.showToast("DIGITS Oops!!!");
            return false;
        }

        String note = mTIEtNote.getText().toString();
        String[] restrictedChars = {":", "\"", "\\", "*", "%"};
        for (String restrictedChar : restrictedChars) {
            if (note.contains(restrictedChar)) {
                AppUtil.showToast("Description is not valid!");
                return false;
            }
        }

        return true;
    }

    private void addExpense() {
        Expense expense = new Expense();
        try {
            //Float amount = Float.parseFloat(mTIEtAmount.getText().toString());
            /*NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);*/
            //amount = new Float(numberFormat.format(amount));
            //Float amount = numberFormat.parse(mTIEtAmount.getText().toString()).floatValue();
            expense.setAmount(AppUtil.getStringAmount(mTIEtAmount.getText().toString()));
            expense.setCreatedOn(AppUtil.convertToDateDB(System.currentTimeMillis()));
            expense.setExpenseDate(mExpenseDate);
            expense.setNote(mTIEtNote.getText().toString());
            expense.setPaymentTypePriId(mSelectedTypeId);

            if (DBManager.addExpense(expense) != -1) {
                AppUtil.showToast("Expense tracked!");
                if (null != mListener) {
                    mListener.onExpenseAdded();
                }
                AppUtil.toggleKeyboard(false);
                dismissAllowingStateLoss();

            } else {
                AppUtil.showToast("Could not add this Expense!");
            }
        } catch (NumberFormatException e) {
            AppUtil.showToast("DIGITS Oops!!!");
        }
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
    public interface OnAddExpenseListener {
        void onExpenseAdded();
    }

    private final CompoundButton.OnCheckedChangeListener paymentModeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            resetAll();
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
                saveBtnClicked();
                return true;
            }
            return false;
        }
    };

    private void resetAll() {
        for (int index = 0; index < mFlexboxLayout.getChildCount(); index++) {
            if (mFlexboxLayout.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayout.getChildAt(index)).setChecked(false);
            }
        }
    }

}
