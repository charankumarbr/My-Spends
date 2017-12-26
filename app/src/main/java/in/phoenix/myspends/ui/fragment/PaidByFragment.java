package in.phoenix.myspends.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaidByFragment.OnPaidBySelectedListener} interface
 * to handle interaction events.
 * Use the {@link PaidByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaidByFragment extends DialogFragment implements View.OnClickListener, PaymentTypeParser.PaymentTypeParserListener {

    private OnPaidBySelectedListener mListener;

    private FlexboxLayout mFlexboxLayout = null;

    private Context mContext;

    private String mSelectedPaymentKey = null;

    public PaidByFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaidByFragment.
     */
    public static PaidByFragment newInstance(String paidById) {
        PaidByFragment fragment = new PaidByFragment();
        if (null != paidById) {
            Bundle arguments = new Bundle();
            arguments.putString("paidBy", paidById);
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String iPaidBy = getArguments().getString("paidBy");
            if (null != iPaidBy) {
                mSelectedPaymentKey = iPaidBy;
            }
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
        View paidByView = inflater.inflate(R.layout.fragment_paid_by, container, false);
        paidByView.findViewById(R.id.fpb_abutton_done).setOnClickListener(this);
        paidByView.findViewById(R.id.fpb_abutton_reset).setOnClickListener(this);

        mFlexboxLayout = (FlexboxLayout) paidByView.findViewById(R.id.fpb_fblayout_payment_mode);
        paidByView.post(new Runnable() {
            @Override
            public void run() {
                getAllPaymentTypes();
            }
        });
        return paidByView;
    }

    private void getAllPaymentTypes() {
        mFlexboxLayout.removeAllViews();
        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("PaidByFragment", "Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(PaidByFragment.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onPaymentTypesParsed(null, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (null != databaseError) {
                    AppLog.d("PaidByFragment", "Payment Types Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("PaidByFragment", "Payment Types Error!");
                }
                AppUtil.showToast("Unable to fetch payment types.");
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaidBySelectedListener) {
            mListener = (OnPaidBySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPaidBySelectedListener");
        }
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fpb_abutton_done) {
            mListener.onPaidBySelected(mSelectedPaymentKey);
            dismissAllowingStateLoss();

        } else if (v.getId() == R.id.fpb_abutton_reset) {
            resetAllPaymentTypes();
            mSelectedPaymentKey = null;
        }
    }

    private final CompoundButton.OnCheckedChangeListener paymentModeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            resetAllPaymentTypes();

            AppLog.d("CheckedChange", "Checked:" + isChecked + "::Title:" + buttonView.getText());
            if (isChecked) {
                buttonView.setChecked(true);
                mSelectedPaymentKey = (String) buttonView.getTag();
                AppLog.d("PaidByFragment", "TypeId:" + mSelectedPaymentKey + "::Title:" + buttonView.getText());
            }
        }
    };

    private void resetAllPaymentTypes() {
        for (int index = 0; index < mFlexboxLayout.getChildCount(); index++) {
            if (mFlexboxLayout.getChildAt(index) instanceof RadioButton) {
                ((RadioButton) mFlexboxLayout.getChildAt(index)).setChecked(false);
            }
        }
    }

    @Override
    public void onPaymentTypesParsed(ArrayList<PaymentType> paymentTypes, boolean isCashPaymentTypeAdded) {
        if (null == paymentTypes) {
            paymentTypes = new ArrayList<>();
        }

        if (!isCashPaymentTypeAdded) {
            paymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        if (null != paymentTypes && paymentTypes.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
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
            }
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
    public interface OnPaidBySelectedListener {
        void onPaidBySelected(String paidByKey);
    }
}
