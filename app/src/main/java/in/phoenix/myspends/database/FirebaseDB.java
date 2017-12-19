package in.phoenix.myspends.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/25/2017.
 */

public final class FirebaseDB {

    private static FirebaseDB mFirebaseDB;

    //private FirebaseDatabase firebaseDatabase;

    //private DatabaseReference databaseReference;

    private DatabaseReference currencyRef;
    //private DatabaseReference spendsRef;
    private DatabaseReference paymentTypeRef;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference fsSpendsRef;

    private FirebaseDB() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference();

        currencyRef = databaseReference.child("currency").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());

        /*spendsRef = databaseReference.child("spends").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());
        spendsRef.goOffline();
        spendsRef.keepSynced(true);*/

        paymentTypeRef = databaseReference.child("paymentType").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid());
        paymentTypeRef.goOffline();
        paymentTypeRef.keepSynced(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        fsSpendsRef = firebaseFirestore.collection("my-spends").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("spends");
    }

    public static FirebaseDB initDb() {
        if (null == mFirebaseDB) {
            mFirebaseDB = new FirebaseDB();
        }

        return mFirebaseDB;
    }

    /*public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }*/

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
        //spendsRef.push().setValue(newExpense, completionListener);
    }

    public void getSpends(String lastKey, ValueEventListener spendsListener) {
        /*Query query = spendsRef.orderByChild("expenseDate");
        if (null != lastKey) {
            query.startAt(lastKey);
        }
        query.addListenerForSingleValueEvent(spendsListener);*/
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
        /*HashMap<String, Object> values = new HashMap<>();
        values.put("amount", editedExpense.getAmount());
        values.put("createdOn", editedExpense.getCreatedOn());
        values.put("expenseDate", editedExpense.getExpenseDate());
        values.put("note", editedExpense.getNote());
        values.put("paymentTypeKey", editedExpense.getPaymentTypeKey());
        values.put("updatedOn", editedExpense.getUpdatedOn());
        spendsRef.child(editedExpense.getId()).updateChildren(values, completionListener);*/
    }

    public void removeExpense(String key, DatabaseReference.CompletionListener completionListener) {
        //spendsRef.child(key).removeValue(completionListener);
    }

    public void getSpends(long fromMillis, long toMillis, String keyToSkip, ValueEventListener valueEventListener) {
        //spendsRef.orderByChild("expenseDate").startAt(fromMillis).endAt(toMillis).limitToFirst(25).addListenerForSingleValueEvent(valueEventListener);
    }

    //-- firestore method --//
    public void addFsNewSpend(NewExpense newExpense, OnSuccessListener successListener, OnFailureListener failureListener) {
        firebaseFirestore.collection("my-spends").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("spends").add(newExpense)
                .addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void getFsSpends(DocumentSnapshot lastVisible, OnSuccessListener successListener, OnFailureListener failureListener) {

        com.google.firebase.firestore.Query query = fsSpendsRef.orderBy("expenseDate", com.google.firebase.firestore.Query.Direction.DESCENDING);

        if (null != lastVisible) {
            query = query.startAfter(lastVisible);
        }

        query.limit(AppConstants.PAGE_SPENDS_SIZE).get().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void updateFsExpense(NewExpense editedExpense, OnSuccessListener successListener, OnFailureListener failureListener) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("amount", editedExpense.getAmount());
        values.put("createdOn", editedExpense.getCreatedOn());
        values.put("expenseDate", editedExpense.getExpenseDate());
        values.put("note", editedExpense.getNote());
        values.put("paymentTypeKey", editedExpense.getPaymentTypeKey());
        values.put("updatedOn", editedExpense.getUpdatedOn());

        DocumentReference aSpendRef = fsSpendsRef.document(editedExpense.getId());
        aSpendRef.update(values).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void deleteFsExpense(String spendId, OnSuccessListener successListener, OnFailureListener failureListener) {
        fsSpendsRef.document(spendId).delete().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void getFsSpends(long fromMillis, long toMillis, String paidBy, DocumentSnapshot lastVisible,
                            OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {

        com.google.firebase.firestore.Query query = fsSpendsRef.orderBy("expenseDate", com.google.firebase.firestore.Query.Direction.DESCENDING);

        if (null != paidBy) {
            AppLog.d("FirebaseDB", "getFsSpends: PaidBy:" + paidBy);
            query = query.whereEqualTo("paymentTypeKey", paidBy);
        }

        query = query.whereGreaterThanOrEqualTo("expenseDate", fromMillis).whereLessThanOrEqualTo("expenseDate", toMillis);

        if (null != lastVisible) {
            query = query.startAfter(lastVisible);
        }

        query.limit(AppConstants.PAGE_SPENDS_SIZE).get().addOnSuccessListener(successListener).addOnFailureListener(failureListener);

    }
}
