package in.phoenix.myspends.parser;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/30/2017.
 */

public class PaymentTypeParser extends AsyncTask<Iterable<DataSnapshot>, Void, Void> {

    private PaymentTypeParserListener mListener;

    private ArrayList<PaymentType> mPaymentTypes = null;

    private HashMap<String, PaymentType> mAllPaymentTypes = null;

    public PaymentTypeParser(PaymentTypeParserListener listener ) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Iterable<DataSnapshot>... iterables) {

        if (null != iterables && iterables.length > 0) {

            Iterable<DataSnapshot> values = iterables[0];

            AppLog.d("PaymentType", "Zero");
            if (null != values) {
                mPaymentTypes = new ArrayList<>();
                if (null != mListener) {
                    AppLog.d("PaymentType", "One");
                    AppLog.d("PaymentType", "Two");
                    for (DataSnapshot aValue : values) {
                        AppLog.d("PaymentType", "Key:" + aValue.getKey());
                        AppLog.d("PaymentType", "Value:" + aValue.getValue());
                        PaymentType paymentType = aValue.getValue(PaymentType.class);
                        paymentType.setKey(aValue.getKey());
                        mPaymentTypes.add(paymentType);
                    }
                } else {
                    //-- for the application --//
                    mAllPaymentTypes = new HashMap<>();
                    for (DataSnapshot aValue : values) {
                        AppLog.d("PaymentType", "Key:" + aValue.getKey());
                        AppLog.d("PaymentType", "Value:" + aValue.getValue());
                        PaymentType paymentType = aValue.getValue(PaymentType.class);
                        paymentType.setKey(aValue.getKey());
                        mPaymentTypes.add(paymentType);
                        mAllPaymentTypes.put(aValue.getKey(), paymentType);
                    }
                }
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (null != mListener) {
            mListener.onPaymentTypesParsed(mPaymentTypes, false);
            MySpends.updatePaymentTypes(mPaymentTypes);

        } else {
            MySpends.addCashPaymentType(mPaymentTypes, mAllPaymentTypes);
        }
    }

    public interface PaymentTypeParserListener {
        void onPaymentTypesParsed(ArrayList<PaymentType> paymentTypes, boolean isCashPaymentTypeAdded);
    }
}
