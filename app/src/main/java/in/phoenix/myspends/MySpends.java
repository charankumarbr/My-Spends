package in.phoenix.myspends;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import in.phoenix.myspends.controller.HourTimeReceiver;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentType;
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

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;

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
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void fetchPaymentTypes() {

        FirebaseDB.initDb().getAllPaymentTypes(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AppLog.d("MySpends", "onChildAdded:");
                AppLog.d("MySpends", "Snapshot:" + dataSnapshot);
                AppLog.d("MySpends", "String:" + s);
                AppLog.d("MySpends", "DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("MySpends", "DataSnapshot: Value:" + dataSnapshot.getValue());
                PaymentType newPaymentType = dataSnapshot.getValue(PaymentType.class);
                newPaymentType.setKey(dataSnapshot.getKey());
                addNewPaymentType(newPaymentType);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AppLog.d("MySpends", "PaymentTypes: onChildChanged: String" + s);
                AppLog.d("MySpends", "PaymentTypes: onChildChanged: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("MySpends", "PaymentTypes: onChildChanged: DataSnapshot: Value:" + dataSnapshot.getValue());
                PaymentType editedPaymentType = dataSnapshot.getValue(PaymentType.class);
                editedPaymentType.setKey(dataSnapshot.getKey());
                editPaymentType(editedPaymentType);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AppLog.d("MySpends", "PaymentTypes: onChildRemoved: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("MySpends", "PaymentTypes: onChildRemoved: DataSnapshot: Value:" + dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                AppLog.d("MySpends", "PaymentTypes: onChildMoved: String" + s);
                AppLog.d("MySpends", "PaymentTypes: onChildMoved: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("MySpends", "PaymentTypes: onChildMoved: DataSnapshot: Value:" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AppLog.d("MySpends", "PaymentTypes: onCancelled:" + databaseError.getDetails());
            }
        });

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

        if (null == listpaymentTypes) {
            listpaymentTypes = new ArrayList<>();
        }
        listpaymentTypes.add(0, PaymentType.getCashPaymentType());

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

        if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();

        } else {
            mAllPaymentTypes.clear();
            mAllPaymentTypes = null;
        }

        mAllPaymentTypes = new ArrayList<>(listAllPaymentTypes);
    }

    public static ArrayList<PaymentType> getAllPaymentTypes() {
        AppLog.d("MySpends", "getAllPaymentTypes");
        return mAllPaymentTypes;
    }

    public static PaymentType getPaymentTypeForKey(String key) {
        if (null != mMapAllPaymentTypes) {
            return mMapAllPaymentTypes.get(key);
        }

        return null;
    }

    private static void addNewPaymentType(PaymentType newPaymentType) {
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();
            mMapAllPaymentTypes.put("0", PaymentType.getCashPaymentType());
        }
        mMapAllPaymentTypes.put(newPaymentType.getKey(), newPaymentType);

        if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();
            mAllPaymentTypes.add(0, PaymentType.getCashPaymentType());
        }

        mAllPaymentTypes.add(newPaymentType);
    }

    private static void editPaymentType(PaymentType editedPaymentType) {
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();
        }
        mMapAllPaymentTypes.put(editedPaymentType.getKey(), editedPaymentType);

        if (null == mAllPaymentTypes) {
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
        }
    }
}

