package in.phoenix.myspends.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;

import java.util.Calendar;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.util.AppLog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDatePickedListener} interface
 * to handle interaction events.
 * Use the {@link DatePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;

    private OnDatePickedListener mListener;

    private TextInputLayout mTilFromDate;
    private TextInputLayout mTilToDate;

    private TextInputEditText mTietFromDate;
    private TextInputEditText mTietToDate;

    private ExpenseDate mFromDate;
    private ExpenseDate mToDate;

    private ImageView mIvToDate;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DatePickerFragment.
     */
    public static DatePickerFragment newInstance(ExpenseDate fromDate, ExpenseDate toDate) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        if (null != fromDate && null != toDate) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("fromDate", fromDate);
            arguments.putParcelable("toDate", toDate);
            datePickerFragment.setArguments(arguments);
        }
        return datePickerFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mFromDate = getArguments().getParcelable("fromDate");
            mToDate = getArguments().getParcelable("toDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View datePickerView = inflater.inflate(R.layout.fragment_date_picker, container, false);
        datePickerView.findViewById(R.id.fdp_abutton_view_spends).setOnClickListener(this);

        datePickerView.findViewById(R.id.fdp_imageview_from_date).setOnClickListener(this);

        mIvToDate = (ImageView) datePickerView.findViewById(R.id.fdp_imageview_to_date);
        mIvToDate.setOnClickListener(this);

        mTietFromDate = (TextInputEditText) datePickerView.findViewById(R.id.fdp_tiet_from_date);
        mTietFromDate.setInputType(InputType.TYPE_NULL);
        mTietToDate = (TextInputEditText) datePickerView.findViewById(R.id.fdp_tiet_to_date);
        mTietToDate.setInputType(InputType.TYPE_NULL);

        mTilFromDate = (TextInputLayout) datePickerView.findViewById(R.id.fdp_til_from_date);
        mTilToDate = (TextInputLayout) datePickerView.findViewById(R.id.fdp_til_to_date);

        if (mFromDate == null || mToDate == null) {
            mIvToDate.setEnabled(false);

        } else {
            mTietFromDate.setText(mFromDate.getFormattedDate());
            mTietToDate.setText(mToDate.getFormattedDate());
        }

        return datePickerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickedListener) {
            mListener = (OnDatePickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDatePickedListener");
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
        if (v.getId() == R.id.fdp_abutton_view_spends) {
            if (isDateSelected()) {
                if (null != mListener) {
                    mListener.onDatePicked(mFromDate, mToDate);
                    dismissAllowingStateLoss();
                }
            }
        } else if (v.getId() == R.id.fdp_imageview_from_date) {
            final DatePickerDialog datePickerDialog;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                datePickerDialog = new DatePickerDialog(mContext);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        AppLog.d("Date", "From::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                        mFromDate = new ExpenseDate(dayOfMonth, month, year);
                        mIvToDate.setEnabled(true);
                        mTietFromDate.setText(mFromDate.getFormattedDate());
                        mTietToDate.setText("");
                        mToDate = null;
                    }
                });

            } else {
                Calendar calendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        AppLog.d("Date", "From::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                        mFromDate = new ExpenseDate(dayOfMonth, month, year);
                        mTietFromDate.setText(mFromDate.getFormattedDate());
                        mIvToDate.setEnabled(true);
                        mTietToDate.setText("");
                        mToDate = null;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
            ((CustomTextView) customTitleView).setText(R.string.select_from_date);
            datePickerDialog.setCustomTitle(customTitleView);
            datePickerDialog.show();

        } else if (v.getId() == R.id.fdp_imageview_to_date) {
            final DatePickerDialog datePickerDialog;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                datePickerDialog = new DatePickerDialog(mContext);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        AppLog.d("Date", "To::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                        mToDate = new ExpenseDate(dayOfMonth, month, year);
                        mTietToDate.setText(mToDate.getFormattedDate());
                    }
                });

            } else {
                Calendar calendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        AppLog.d("Date", "To::Y:" + year + "::M:" + month + "::D:" + dayOfMonth);
                        mToDate = new ExpenseDate(dayOfMonth, month, year);
                        mTietToDate.setText(mToDate.getFormattedDate());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
            datePickerDialog.getDatePicker().setMinDate(mFromDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(mFromDate.reportToDateTimeInMillis());
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
            ((CustomTextView) customTitleView).setText(R.string.select_to_date);
            datePickerDialog.setCustomTitle(customTitleView);
            datePickerDialog.show();
        }
    }

    private boolean isDateSelected() {

        boolean isValid = true;

        if (null == mFromDate) {
            mTilFromDate.setErrorEnabled(true);
            mTilFromDate.setError("From date is required");
            isValid = false;

        } else {
            mTilFromDate.setErrorEnabled(false);
        }

        if (null == mToDate) {
            mTilToDate.setErrorEnabled(true);
            mTilToDate.setError("To date is required");
            isValid = false;

        } else {
            mTilToDate.setErrorEnabled(false);
        }

        return isValid;
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
    public interface OnDatePickedListener {
        void onDatePicked(ExpenseDate fromDate, ExpenseDate toDate);
    }
}
