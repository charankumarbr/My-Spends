package in.phoenix.myspends;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import in.phoenix.myspends.controller.HourTimeReceiver;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.CategoryParser;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public class MySpends extends Application {

    public static Context APP_CONTEXT;

    private static HashMap<String, PaymentType> mMapAllPaymentTypes;
    private static ArrayList<PaymentType> mAllPaymentTypes;

    private static ArrayList<Category> mAllCategories;
    private static HashMap<Integer, String> mMapAllCategories;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;

        if (AppUtil.isUserLoggedIn()) {
            fetchCategories();
            Crashlytics.setUserIdentifier(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        initNotification();
    }

    private void fetchCategories() {
        FirebaseDB.initDb().fetchCategories(mCategoryListener);
    }

    private void initNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR)
                == -1 ? 20 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR));
        calendar.set(Calendar.MINUTE, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN)
                == -1 ? 0 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN));
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent receiverIntent = new Intent(this, HourTimeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void fetchPaymentTypes() {

        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("MySpends", "PaymentType Count:" + dataSnapshot.getChildrenCount());
                    //if (dataSnapshot.getChildrenCount() > 0) {
                    new PaymentTypeParser(null).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    /*} else {
                        new PaymentTypeParser(null).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());
                    }*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (null != databaseError) {
                    AppLog.d("MySpends", "PaymentType Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("MySpends", "PaymentType Error!!");
                }
                new PaymentTypeParser(null).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });
    }

    public static void addCashPaymentType(ArrayList<PaymentType> listpaymentTypes, HashMap<String, PaymentType> mapPaymentTypes) {
        AppLog.d("MySpends", "addCashPaymentType");
        if (null == mapPaymentTypes) {
            mapPaymentTypes = new HashMap<>();
        }
        mapPaymentTypes.put("0", PaymentType.getCashPaymentType());

        //-- removed list of payment types --//
        /*if (null == listpaymentTypes) {
            listpaymentTypes = new ArrayList<>();
        }
        listpaymentTypes.add(0, PaymentType.getCashPaymentType());*/

        setAllPaymentTypes(listpaymentTypes, mapPaymentTypes);
    }

    public static void setAllPaymentTypes(ArrayList<PaymentType> listAllPaymentTypes, HashMap<String, PaymentType> mapAllPaymentTypes) {
        AppLog.d("MySpends", "setAllPaymentTypes");
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();

        } else {
            mMapAllPaymentTypes.clear();
            mMapAllPaymentTypes = null;
        }

        mMapAllPaymentTypes = new HashMap<>(mapAllPaymentTypes);

        //-- removed list of payment types --//
        /*if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();

        } else {
            mAllPaymentTypes.clear();
            mAllPaymentTypes = null;
        }

        mAllPaymentTypes = new ArrayList<>(listAllPaymentTypes);*/
    }

    //-- removed list of payment types --//
    /*public static ArrayList<PaymentType> getAllPaymentTypes() {
        AppLog.d("MySpends", "listenPaymentTypes");
        return mAllPaymentTypes;
    }*/

    public static PaymentType getPaymentTypeForKey(String key) {
        if (null != mMapAllPaymentTypes) {
            return mMapAllPaymentTypes.get(key);
        }

        return null;
    }

    public static void addNewPaymentType(PaymentType newPaymentType) {
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();
            mMapAllPaymentTypes.put("0", PaymentType.getCashPaymentType());
        }
        mMapAllPaymentTypes.put(newPaymentType.getKey(), newPaymentType);

        //-- removed list of payment types --//
        /*if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();
            mAllPaymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        mAllPaymentTypes.add(newPaymentType);*/
    }

    public static void editPaymentType(PaymentType editedPaymentType) {
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();
        }
        mMapAllPaymentTypes.put(editedPaymentType.getKey(), editedPaymentType);

        //-- removed list of payment types --//
        /*if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();
            mAllPaymentTypes.add(0, PaymentType.getCashPaymentType());
            mAllPaymentTypes.add(editedPaymentType);

        } else {
            for (int index = 0; index < mAllPaymentTypes.size(); index++) {
                AppLog.d("MySpends", "editPaymentType: Index:" + index);
                AppLog.d("MySpends", "editPaymentType: Key:" + mAllPaymentTypes.get(index).getKey());
                AppLog.d("MySpends", "editPaymentType: Name:" + mAllPaymentTypes.get(index).getName());
                if (mAllPaymentTypes.get(index).getKey().equals(editedPaymentType.getKey())) {
                    mAllPaymentTypes.set(index, editedPaymentType);
                    break;
                }
            }
        }*/
    }

    public static void updatePaymentTypes(ArrayList<PaymentType> paymentTypes) {
        if (null != paymentTypes && paymentTypes.size() > 0) {
            if (null == mMapAllPaymentTypes) {
                mMapAllPaymentTypes = new HashMap<>();
            }
            for (PaymentType paymentType : paymentTypes) {
                mMapAllPaymentTypes.put(paymentType.getKey(), paymentType);
            }
        }
    }

    private ValueEventListener mCategoryListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount() > 0) {
                new CategoryParser().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

            } else {
                AppLog.d("MySpends", "ZERO Categories");
                updateCategories(null, null);
                //FirebaseDB.initDb().addNewCategory(null);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static void updateCategories(ArrayList<Category> allCategories, HashMap<Integer, String> mapAllCategories) {
        if (null != allCategories && null != mapAllCategories) {
            AppLog.d("MySpends", "Category :: List Size:" + allCategories.size() + " :: Map Size:" + mapAllCategories.size());

            if (null != mAllCategories) {
                mAllCategories.clear();
                mAllCategories = null;
            }
            mAllCategories = new ArrayList<>(allCategories);

            if (null != mMapAllCategories) {
                mMapAllCategories.clear();
                mMapAllCategories = null;
            }
            mMapAllCategories = new HashMap<>(mapAllCategories);
        }
    }

    public static ArrayList<Category> getCategories() {
        return mAllCategories;
    }

    public static String getCategoryName(int categoryId) {
        if (null != mMapAllCategories && mMapAllCategories.containsKey(categoryId)) {
            return mMapAllCategories.get(categoryId);
        }

        return AppConstants.BLANK_NOTE_TEMPLATE;
    }
}