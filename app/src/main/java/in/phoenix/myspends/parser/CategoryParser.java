package in.phoenix.myspends.parser;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 3/31/2018.
 */

public final class CategoryParser extends AsyncTask<Iterable<DataSnapshot>, Void, Void> {

    private ArrayList<Category> mAllCategories = null;

    private HashMap<Integer, String> mMapAllCategories = null;

    private CategoryParserListener mListener = null;

    public CategoryParser(CategoryParserListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Iterable<DataSnapshot>[] iterables) {

        if (null != iterables && iterables.length > 0) {

            Iterable<DataSnapshot> values = iterables[0];

            AppLog.d("CategoryParser", "Zero");
            if (null != values) {
                mAllCategories = new ArrayList<>();
                mMapAllCategories = new HashMap<>();

                AppLog.d("CategoryParser", "One");
                for (DataSnapshot aValue : values) {
                    AppLog.d("CategoryParser", "Key:" + aValue.getKey());
                    AppLog.d("CategoryParser", "Value:" + aValue.getValue());
                    Category category = aValue.getValue(Category.class);
                    mAllCategories.add(category);
                    mMapAllCategories.put(category.getId(), category.getName());
                }

                Collections.sort(mAllCategories, new Comparator<Category>() {
                    @Override
                    public int compare(Category o1, Category o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (null != mAllCategories) {
            MySpends.updateCategories(mAllCategories, mMapAllCategories);
            if (null != mListener) {
                mListener.onCategoriesParsed(mAllCategories);
            }
        }
    }

    public interface CategoryParserListener {
        void onCategoriesParsed(ArrayList<Category> allCategories);
    }
}
