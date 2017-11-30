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
                AppLog.d("MySpends", "s:" + s);
                AppLog.d("MySpends", "Key:" + dataSnapshot.getKey());
                AppLog.d("MySpends", "Value:" + dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AppLog.d("MySpends", "onCancelled:" + databaseError.getDetails());
            }
        });

        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("MySpends", "PaymentType Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(null).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (null != databaseError) {
                    AppLog.d("MySpends", "PaymentType Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("MySpends", "PaymentType Error!!");
                }
            }
        });
    }

    public static void addCashPaymentType(ArrayList<PaymentType> listpaymentTypes, HashMap<String, PaymentType> mapPaymentTypes) {
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
        if (null == mMapAllPaymentTypes) {
            mMapAllPaymentTypes = new HashMap<>();
        }

        mMapAllPaymentTypes.clear();
        mMapAllPaymentTypes = null;
        mMapAllPaymentTypes = new HashMap<>(mapAllPaymentTypes);

        if (null == mAllPaymentTypes) {
            mAllPaymentTypes = new ArrayList<>();
        }

        mAllPaymentTypes.clear();
        mAllPaymentTypes = null;
        mAllPaymentTypes = new ArrayList<>(listAllPaymentTypes);
    }

    public static ArrayList<PaymentType> getAllPaymentTypes() {
        return mAllPaymentTypes;
    }

    public static PaymentType getPaymentTypeForKey(String key) {
        if (null != mMapAllPaymentTypes) {
            return mMapAllPaymentTypes.get(key);
        }

        return null;
    }
}
