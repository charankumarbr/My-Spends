package in.phoenix.myspends.parser;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/29/2017.
 */

public final class SpendsParser extends AsyncTask<Iterable<DataSnapshot>, Void, Void> {

    private SpendsParserListener mListener = null;

    private ArrayList<NewExpense> mSpends = null;

    private String mLastKey = null;

    public SpendsParser(SpendsParserListener listener, String lastKey) {
        this.mListener = listener;
        this.mLastKey = lastKey;
    }

    @Override
    protected Void doInBackground(Iterable<DataSnapshot>... iterables) {

        Iterable<DataSnapshot> values = iterables[0];
        if (null != values) {
            mSpends = new ArrayList<>();
            for (DataSnapshot aValue : values) {
                String key = aValue.getKey();
                String timeMillis = String.valueOf(aValue.child("expenseDate").getValue());
                if (null != mLastKey && mLastKey.equals(timeMillis)) {
                    //-- do not add this --//

                } else {
                    NewExpense newExpense = aValue.getValue(NewExpense.class);
                    newExpense.setId(key);
                    AppLog.d("SpendsParser", "Spend:" + newExpense.toString());
                    mSpends.add(newExpense);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (null != mListener) {
            AppLog.d("SpendsParser", "onPostExecute" + (null != mSpends ? mSpends.size() : 0));
            mListener.onSpendsParsed(mSpends);
        }
    }

    public interface SpendsParserListener {
        void onSpendsParsed(ArrayList<NewExpense> spends);
    }
}
