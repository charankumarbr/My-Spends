package in.phoenix.myspends.parser;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import in.phoenix.myspends.model.Expense;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/29/2017.
 */

public final class SpendsParser extends AsyncTask<Iterable<DataSnapshot>, Void, Void> {

    private SpendsParserListener mListener = null;

    private ArrayList<NewExpense> mSpends = null;

    public SpendsParser(SpendsParserListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Iterable<DataSnapshot>... iterables) {

        Iterable<DataSnapshot> values = iterables[0];
        if (null != values) {
            mSpends = new ArrayList<>();
            for (DataSnapshot aValue : values) {
                NewExpense newExpense = aValue.getValue(NewExpense.class);
                newExpense.setId(aValue.getKey());
                AppLog.d("SpendsParser", "Spend:" + newExpense.toString());
                mSpends.add(newExpense);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (null != mListener) {
            mListener.onSpendsParsed(mSpends);
        }
    }

    public interface SpendsParserListener {
        void onSpendsParsed(ArrayList<NewExpense> spends);
    }
}
