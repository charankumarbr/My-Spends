package in.phoenix.myspends.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/25/2017.
 */

public final class FirebaseDB {

    private static FirebaseDB mFirebaseDB;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;

    private DatabaseReference currencyRef;
    private DatabaseReference spendsRef;
    private DatabaseReference paymentTypeRef;

    private FirebaseDB() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();

        currencyRef = databaseReference.child("currency").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());

        spendsRef = databaseReference.child("spends").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());
        spendsRef.goOffline();
        spendsRef.keepSynced(true);

        paymentTypeRef = databaseReference.child("paymentType").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());
        paymentTypeRef.goOffline();
        paymentTypeRef.keepSynced(true);
    }

    public static FirebaseDB initDb() {
        if (null == mFirebaseDB) {
            mFirebaseDB = new FirebaseDB();
        }

        return mFirebaseDB;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setCurrency(Currency selectedCurrency) {
        currencyRef.setValue(selectedCurrency);
    }

    public String getCurrency() {
        return currencyRef.getKey();
    }

    public DatabaseReference getCurrencyReference() {
        return currencyRef;
    }

    public void addNewExpense(NewExpense newExpense, DatabaseReference.CompletionListener completionListener) {
        spendsRef.push().setValue(newExpense, completionListener);
    }

    public void getSpends(int skip, ValueEventListener spendsListener) {
        spendsRef.orderByChild("expenseDate").limitToFirst(50).addListenerForSingleValueEvent(spendsListener);
    }

    public void addNewPaymentType(PaymentType paymentType, DatabaseReference.CompletionListener completionListener) {
        String key = paymentTypeRef.push().getKey();
        AppLog.d("AddNewPaymentType", "Key:" + key);
        paymentTypeRef.child(key).setValue(paymentType, completionListener);
    }

    public void getPaymentTypes(ValueEventListener paymentTypeListener) {
        paymentTypeRef.addListenerForSingleValueEvent(paymentTypeListener);
    }

    public void getAllPaymentTypes(ChildEventListener allPaymentTypeListener) {
        paymentTypeRef.addChildEventListener(allPaymentTypeListener);
    }

    public void updateExpense(NewExpense editedExpense, DatabaseReference.CompletionListener completionListener) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("amount", editedExpense.getAmount());
        values.put("createdOn", editedExpense.getCreatedOn());
        values.put("expenseDate", editedExpense.getExpenseDate());
        values.put("note", editedExpense.getNote());
        values.put("paymentTypeKey", editedExpense.getPaymentTypeKey());
        values.put("updatedOn", editedExpense.getUpdatedOn());
        spendsRef.child(editedExpense.getId()).updateChildren(values, completionListener);
    }

    public void removeExpense(String key, DatabaseReference.CompletionListener completionListener) {
        spendsRef.child(key).removeValue(completionListener);
    }
}
