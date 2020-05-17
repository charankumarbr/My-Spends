package in.phoenix.myspends.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.CustomSpinnerAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentMode;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppAnalytics;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPaymentTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPaymentTypeFragment extends DialogFragment {

    private Context mContext;

    private TextInputEditText mTIETTypeName = null;

    private TextInputLayout mTILTypeName = null;

    //private FlexboxLayout mFlexboxLayoutTypes = null;

    private int mSelectedPaymentModeId = -1;

    private OnPaymentTypeListener mListener = null;

    private ProgressBar mPbLoading = null;

    private Spinner mSpnrPaymentType = null;

    public AddPaymentTypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddPaymentTypeFragment.
     */
    public static AddPaymentTypeFragment newInstance() {
        return new AddPaymentTypeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnPaymentTypeListener) context;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addPaymentTypeView = inflater.inflate(R.layout.fragment_add_payment_type, container, false);
        //mFlexboxLayoutTypes = addPaymentTypeView.findViewById(R.id.fapt_fblayout_payment_type);
        mTILTypeName = addPaymentTypeView.findViewById(R.id.fapt_til_type_name);
        mTIETTypeName = addPaymentTypeView.findViewById(R.id.fapt_tiedittext_type_name);

        AppCompatButton buttonAdd = addPaymentTypeView.findViewById(R.id.fapt_acbutton_add);
        buttonAdd.setOnClickListener(clickListener);

        mPbLoading = addPaymentTypeView.findViewById(R.id.fapt_pb_loading);

        mSpnrPaymentType = addPaymentTypeView.findViewById(R.id.fapt_spnr_payment_type);

        ArrayList<PaymentMode> paymentModes = PaymentMode.getPaymentModes();
        if ((null != paymentModes) && paymentModes.size() > 0) {
            /*for (int index = 0; index < paymentModes.size(); index++) {
                RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_radio_button, null);
                radioButton.setId(index);
                radioButton.setTag(paymentModes.get(index).getId());
                radioButton.setText(paymentModes.get(index).getName());
                radioButton.setOnCheckedChangeListener(paymentTypeSelectedListener);
                mFlexboxLayoutTypes.addView(radioButton);
            }*/
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mContext,
                    R.layout.layout_spinner_selected, paymentModes);
            adapter.setSelectionText("Select Payment type");
            mSpnrPaymentType.setAdapter(adapter);
            mSpnrPaymentType.setOnItemSelectedListener(mPaymentTypeSelectedListener);

            mTIETTypeName.requestFocus();
            AppUtil.toggleKeyboard(true);

        } else {
            AppUtil.showToast("Unable to fetch payment types!");
            dismissAllowingStateLoss();
        }

        return addPaymentTypeView;
    }

    private AdapterView.OnItemSelectedListener mPaymentTypeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            AppLog.d("Paid By Listener", "onItemSelected: Position:" + position);
            if (null == view.getTag()) {
                mSelectedPaymentModeId = -1;

            } else {
                mSelectedPaymentModeId = (int) view.getTag();
            }
            AppLog.d("AddExpense", "TypeId Key:" + mSelectedPaymentModeId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            AppLog.d("Paid By Listener", "onNothing:");
            mSelectedPaymentModeId = -1;
        }
    };

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fapt_acbutton_add) {
                if ((validate()) && !isDuplicate()) {
                    addPaymentType();
                }
            }
        }

        private boolean isDuplicate() {
            return false; //-- DBManager.checkPaymentTypeName(mTIETTypeName.getText().toString(), mSelectedPaymentModeId);
        }

        private void addPaymentType() {
            String paymentTypeName = mTIETTypeName.getText().toString();

            final PaymentType paymentType = new PaymentType();
            paymentType.setName(paymentTypeName);
            paymentType.setCreatedOn(System.currentTimeMillis());
            paymentType.setPaymentModeId(mSelectedPaymentModeId);
            paymentType.setActive(true);

            if (AppUtil.isConnected()) {
                //mPbLoading.setVisibility(View.VISIBLE);
                AppUtil.toggleKeyboard(false);
                AppDialog.showDialog(mContext, "Adding...");
                FirebaseDB.initDb().addNewPaymentType(paymentType, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        AppLog.d("AddNew", "onComplete 1");
                        if (null != getActivity() && isAdded()) {
                            AppLog.d("AddNew", "onComplete 2");
                            //mPbLoading.setVisibility(View.GONE);
                            AppDialog.dismissDialog();
                            if (null == databaseError) {
                                AppLog.d("AddNew", "onComplete 3");
                                AppLog.d("AddNew", "Key:" + databaseReference.getKey());
                                Bundle eventBundle = new Bundle();
                                eventBundle.putInt("payment_mode_id", paymentType.getPaymentModeId());
                                eventBundle.putString("payment_type", paymentType.getName());
                                AppAnalytics.init().logEvent("added_payment_type", eventBundle);
                                if (null != mListener) {
                                    AppLog.d("AddNew", "onComplete 4");
                                    mListener.onPaymentTypeAdded();
                                }

                            } else {
                                AppLog.d("AddNew", "onComplete 5");
                                AppUtil.showToast("Unable to add. Please try again.");
                            }
                        }
                    }
                });

            } else {
                AppUtil.showToast(R.string.no_internet);
            }
        }

        private String getTypeId(String paymentTypeName) {

            if (paymentTypeName.contains(" ")) {
                paymentTypeName = paymentTypeName.trim();
                paymentTypeName = paymentTypeName.split(" ")[0];
            }

            paymentTypeName = paymentTypeName.trim();
            return paymentTypeName + "|" + mSelectedPaymentModeId;
        }
    };

    /*private final CompoundButton.OnCheckedChangeListener paymentTypeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            resetAll();
            AppLog.d("CheckedChange", "Checked:" + isChecked + "::Title:" + buttonView.getText());
            if (isChecked) {
                buttonView.setChecked(true);
                mSelectedPaymentModeId = (int) buttonView.getTag();
                AppLog.d("PaymentMode", "CheckedTypeId:" + mSelectedPaymentModeId + "::Title:" + buttonView.getText());
            }
        }
    };*/

    /*private void resetAll() {
        for (int index = 0; index < mFlexboxLayoutTypes.getChildCount(); index++) {
            if (mFlexboxLayoutTypes.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayoutTypes.getChildAt(index)).setChecked(false);
            }
        }
    }*/

    private boolean validate() {

        String paymentModeName = mTIETTypeName.getText().toString();
        if (TextUtils.isEmpty(paymentModeName)) {
            //AppUtil.showToast(R.string.enter_payment_name);
            mTILTypeName.setError(getString(R.string.enter_payment_name));
            mTILTypeName.setErrorEnabled(true);
            return false;
        }

        String[] restrictedChars = {":", "$", "'", "\"", "\\", "(", ")", "*", "%", "!"};
        for (String restrictedChar : restrictedChars) {
            if (paymentModeName.contains(restrictedChar)) {
                //AppUtil.showToast(R.string.enter_valid_payment_name);
                mTILTypeName.setError(getString(R.string.enter_valid_payment_name));
                mTILTypeName.setErrorEnabled(true);
                return false;
            }
        }

        if (mSelectedPaymentModeId == -1) {
            //AppUtil.showToast(R.string.please_select_payment_type);
            mTILTypeName.setError(getString(R.string.please_select_payment_type));
            mTILTypeName.setErrorEnabled(true);
            return false;
        }

        mTILTypeName.setError(null);
        mTILTypeName.setErrorEnabled(false);
        return true;
    }

    public interface OnPaymentTypeListener {
        void onPaymentTypeAdded();
    }

}
