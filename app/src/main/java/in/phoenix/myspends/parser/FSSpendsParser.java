package in.phoenix.myspends.parser;

import android.os.AsyncTask;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Iterator;

import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 12/19/2017.
 */

public final class FSSpendsParser extends AsyncTask<Iterator<DocumentSnapshot>, Void, Void> {

    private SpendsParser.SpendsParserListener mListener = null;

    private ArrayList<NewExpense> mSpends = new ArrayList<>();

    public FSSpendsParser(SpendsParser.SpendsParserListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Iterator<DocumentSnapshot>... iterators) {

        if (null != iterators && iterators.length > 0) {

            Iterator<DocumentSnapshot> documentSnapshots = iterators[0];
            if (null != documentSnapshots) {
                while (documentSnapshots.hasNext()) {
                    DocumentSnapshot documentSnapshot = documentSnapshots.next();
                    AppLog.d("FSSpendsParser", "doInBg: Id:" + documentSnapshot.getId());
                    NewExpense newExpense = documentSnapshot.toObject(NewExpense.class);
                    newExpense.setId(documentSnapshot.getId());
                    AppLog.d("FSSpendsParser", "Spend:" + newExpense.toString());
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
            AppLog.d("FSSpendsParser", "onPostExecute" + (null != mSpends ? mSpends.size() : 0));
            mListener.onSpendsParsed(mSpends);
        }
    }
}
