package in.phoenix.trackmyspends.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import in.phoenix.trackmyspends.R;
import in.phoenix.trackmyspends.database.DBManager;
import in.phoenix.trackmyspends.model.PaymentType;
import in.phoenix.trackmyspends.util.AppLog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaidByFragment.OnPaidBySelectedListener} interface
 * to handle interaction events.
 * Use the {@link PaidByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaidByFragment extends DialogFragment implements View.OnClickListener {

    private OnPaidBySelectedListener mListener;

    private FlexboxLayout mFlexboxLayout = null;

    private Context mContext;

    private SortedSet<Integer> mSelectedPaidById = null;

    public PaidByFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaidByFragment.
     */
    public static PaidByFragment newInstance(Integer[] paidById) {
        PaidByFragment fragment = new PaidByFragment();
        if (null != paidById) {
            int[] iPaidBy = new int[paidById.length];
            for (int index = 0; index < paidById.length; index++) {
                iPaidBy[index] = paidById[index];
            }
            Bundle arguments = new Bundle();
            arguments.putIntArray("paidBy", iPaidBy);
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int[] iPaidBy = getArguments().getIntArray("paidBy");
            if (null != iPaidBy) {
                mSelectedPaidById = new TreeSet<>();
                for (int index = 0; index < iPaidBy.length; index++) {
                    mSelectedPaidById.add(iPaidBy[index]);
                }
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
        ArrayList<PaymentType> paymentTypes = DBManager.getPaymentTypes(false);
        if (null != paymentTypes) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            for (int index = 0; index < paymentTypes.size(); index++) {
                CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.layout_checkbox, null);
                checkBox.setId(index);
                checkBox.setTag(paymentTypes.get(index).getId());
                checkBox.setText(paymentTypes.get(index).getName());
                if (null != mSelectedPaidById && mSelectedPaidById.contains(paymentTypes.get(index).getId())) {
                    checkBox.setChecked(true);

                } else {
                    checkBox.setChecked(false);
                }
                checkBox.setOnCheckedChangeListener(paymentModeSelectedListener);
                mFlexboxLayout.addView(checkBox);
            }
        }
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
            if ((null != mSelectedPaidById) && mSelectedPaidById.size() > 0) {
                mListener.onPaidBySelected(mSelectedPaidById.toArray(new Integer[mSelectedPaidById.size()]));
            } else {
                mListener.onPaidBySelected(null);
            }
            dismissAllowingStateLoss();
        }
    }

    private final CompoundButton.OnCheckedChangeListener paymentModeSelectedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            AppLog.d("CheckedChange", "Checked:" + isChecked + "::Title:" + buttonView.getText());
            if (null == mSelectedPaidById) {
                mSelectedPaidById = new TreeSet<>();
            }
            int selectedTypeId = (int) buttonView.getTag();
            AppLog.d("PaidByFragment", "TypeId:" + selectedTypeId + "::Title:" + buttonView.getText());
            if (isChecked) {
                mSelectedPaidById.add(selectedTypeId);

            } else {
                mSelectedPaidById.remove(selectedTypeId);
            }
        }
    };

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
        void onPaidBySelected(Integer[] paidById);
    }
}
