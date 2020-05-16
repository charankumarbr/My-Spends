package in.phoenix.myspends.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import in.phoenix.myspends.R;
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

    private long mFromMillis = 0;
    private long mToMillis = 0;

    private ExpenseDate mFromExpenseDate;
    private ExpenseDate mToExpenseDate;

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
    public static DatePickerFragment newInstance(long fromDate, long toDate) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        if (0 != fromDate && 0 != toDate) {
            Bundle arguments = new Bundle();
            arguments.putLong("fromDate", fromDate);
            arguments.putLong("toDate", toDate);
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
            mFromMillis = getArguments().getLong("fromDate");
            mToMillis = getArguments().getLong("toDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View datePickerView = inflater.inflate(R.layout.fragment_date_picker, container, false);
        datePickerView.findViewById(R.id.fdp_abutton_view_spends).setOnClickListener(this);

        datePickerView.findViewById(R.id.fdp_imageview_from_date).setOnClickListener(this);

        mIvToDate = datePickerView.findViewById(R.id.fdp_imageview_to_date);
        mIvToDate.setOnClickListener(this);

        mTietFromDate = datePickerView.findViewById(R.id.fdp_tiet_from_date);
        mTietFromDate.setInputType(InputType.TYPE_NULL);
        mTietToDate = datePickerView.findViewById(R.id.fdp_tiet_to_date);
        mTietToDate.setInputType(InputType.TYPE_NULL);

        mTilFromDate = datePickerView.findViewById(R.id.fdp_til_from_date);
        mTilToDate = datePickerView.findViewById(R.id.fdp_til_to_date);

        if (mFromMillis != 0 || mToMillis != 0) {
            mIvToDate.setEnabled(false);

        } else {
            mFromExpenseDate = new ExpenseDate(mFromMillis);
            mToExpenseDate = new ExpenseDate(mToMillis);
            mTietFromDate.setText(mFromExpenseDate.getFormattedDate());
            mTietToDate.setText(mToExpenseDate.getFormattedDate());
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
                    mListener.onDatePicked(mFromMillis, mToMillis);
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
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
            ((TextView) customTitleView).setText(R.string.select_from_date);
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
            datePickerDialog.getDatePicker().setMaxDate((currentMillis < mFromExpenseDate.reportToDateTimeInMillis())
                    ? currentMillis : mFromExpenseDate.reportToDateTimeInMillis());
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View customTitleView = inflater.inflate(R.layout.layout_date_title, null);
            ((TextView) customTitleView).setText(R.string.select_to_date);
            datePickerDialog.setCustomTitle(customTitleView);
            datePickerDialog.show();
        }
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
        void onDatePicked(long fromDate, long toDate);
    }
}
