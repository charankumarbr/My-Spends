package in.phoenix.myspends.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentMode;
import in.phoenix.myspends.model.PaymentType;
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

    private FlexboxLayout mFlexboxLayoutTypes = null;

    private int mSelectedPaymentModeId = -1;

    private OnPaymentTypeListener mListener = null;

    private ProgressBar mPbLoading = null;

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
        mFlexboxLayoutTypes = (FlexboxLayout) addPaymentTypeView.findViewById(R.id.fapt_fblayout_payment_type);
        mTIETTypeName = (TextInputEditText) addPaymentTypeView.findViewById(R.id.fapt_tiedittext_type_name);

        AppCompatButton buttonAdd = (AppCompatButton) addPaymentTypeView.findViewById(R.id.fapt_acbutton_add);
        buttonAdd.setOnClickListener(clickListener);

        mPbLoading = addPaymentTypeView.findViewById(R.id.fapt_pb_loading);

        ArrayList<PaymentMode> paymentModes = PaymentMode.getPaymentModes();
        if (null != paymentModes) {
            for (int index = 0; index < paymentModes.size(); index++) {
                RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.layout_radio_button, null);
                radioButton.setId(index);
                radioButton.setTag(paymentModes.get(index).getId());
                radioButton.setText(paymentModes.get(index).getName());
                radioButton.setOnCheckedChangeListener(paymentTypeSelectedListener);
                mFlexboxLayoutTypes.addView(radioButton);
            }
        }

        mTIETTypeName.requestFocus();
        AppUtil.toggleKeyboard(true);

        return addPaymentTypeView;
    }

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

            PaymentType paymentType = new PaymentType();
            paymentType.setName(paymentTypeName);
            paymentType.setCreatedOn(System.currentTimeMillis());
            paymentType.setPaymentModeId(mSelectedPaymentModeId);
            paymentType.setActive(true);

            if (AppUtil.isConnected()) {
                mPbLoading.setVisibility(View.VISIBLE);
                AppUtil.toggleKeyboard(false);
                FirebaseDB.initDb().addNewPaymentType(paymentType, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        AppLog.d("AddNew", "onComplete 1");
                        if (null != getActivity() && isAdded()) {
                            AppLog.d("AddNew", "onComplete 2");
                            mPbLoading.setVisibility(View.GONE);
                            if (null == databaseError) {
                                AppLog.d("AddNew", "onComplete 3");
                                AppLog.d("AddNew", "Key:" + databaseReference.getKey());
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

    private final CompoundButton.OnCheckedChangeListener paymentTypeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
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
    };

    private void resetAll() {
        for (int index = 0; index < mFlexboxLayoutTypes.getChildCount(); index++) {
            if (mFlexboxLayoutTypes.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayoutTypes.getChildAt(index)).setChecked(false);
            }
        }
    }

    private boolean validate() {

        String paymentModeName = mTIETTypeName.getText().toString();
        if (TextUtils.isEmpty(paymentModeName)) {
            AppUtil.showToast(R.string.enter_payment_name);
            return false;
        }

        String[] restrictedChars = {":", "$", "'", "\"", "\\", "(", ")", "*", "%", "!"};
        for (String restrictedChar : restrictedChars) {
            if (paymentModeName.contains(restrictedChar)) {
                AppUtil.showToast(R.string.enter_valid_payment_name);
                return false;
            }
        }

        if (mSelectedPaymentModeId == -1) {
            AppUtil.showToast(R.string.please_select_payment_type);
            return false;
        }

        return true;
    }

    public interface OnPaymentTypeListener {
        void onPaymentTypeAdded();
    }

}
