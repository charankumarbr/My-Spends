package in.phoenix.myspends.database;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.model.MessageBoard;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 11/25/2017.
 */

public final class FirebaseDB {

    private static FirebaseDB mFirebaseDB;

    private DatabaseReference currencyRef;

    private DatabaseReference paymentTypeRef;
    private ChildEventListener mPaymentTypeListener;

    private DatabaseReference categoryRef;

    private DatabaseReference messageBoardRef;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference fsSpendsRef;
    private EventListener mSpendsListener = null;
    private ListenerRegistration mSpendsListenerRegistration = null;

    private Boolean mIsLoggedOut = Boolean.FALSE;

    private FirebaseDB() {
        AppLog.d("FirebaseDB", "constructor");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        DatabaseReference.goOffline();

        initDBPaths();
        //initSpendsListener();
    }

    private void initDBPaths() {

        if (null != FirebaseAuth.getInstance() && null != FirebaseAuth.getInstance().getCurrentUser()) {
            String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            currencyRef = databaseReference.child("currency").child(firebaseUserId);
            currencyRef.goOffline();
            currencyRef.keepSynced(true);

            paymentTypeRef = databaseReference.child("paymentType").child(firebaseUserId);
            paymentTypeRef.goOffline();
            paymentTypeRef.keepSynced(true);

            categoryRef = databaseReference.child("category");
            categoryRef.goOffline();
            categoryRef.keepSynced(true);

            messageBoardRef = databaseReference.child("messageBoard").child(firebaseUserId);
            messageBoardRef.goOffline();
            messageBoardRef.keepSynced(true);

            firebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            firebaseFirestore.setFirestoreSettings(settings);

            fsSpendsRef = firebaseFirestore.collection("my-spends").document(firebaseUserId)
                    .collection("spends");
        }
    }

    private void initSpendsListener() {
        mSpendsListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (null != e) {
                    AppLog.d("FirebaseDB", "initSpendsListener", e);
                    mSpendsListener = null;
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            AppLog.d("FirebaseDB", "initSpendsListener: New Spend: " + dc.getDocument().getData());
                            break;

                        case MODIFIED:
                            AppLog.d("FirebaseDB", "initSpendsListener: Modified Spend: " + dc.getDocument().getData());
                            break;

                        case REMOVED:
                            AppLog.d("FirebaseDB", "initSpendsListener: Removed Spend: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        };
    }

    private void initPaymentTypeListener() {
        mPaymentTypeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AppLog.d("FirebaseDB", "PaymentTypes: onChildAdded:");
                AppLog.d("FirebaseDB", "PaymentTypes: onChildAdded: Snapshot:" + dataSnapshot);
                AppLog.d("FirebaseDB", "PaymentTypes: onChildAdded: String:" + s);
                AppLog.d("FirebaseDB", "PaymentTypes: onChildAdded: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("FirebaseDB", "PaymentTypes: onChildAdded: DataSnapshot: Value:" + dataSnapshot.getValue());
                PaymentType newPaymentType = dataSnapshot.getValue(PaymentType.class);
                newPaymentType.setKey(dataSnapshot.getKey());
                MySpends.addNewPaymentType(newPaymentType);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AppLog.d("FirebaseDB", "PaymentTypes: onChildChanged: String" + s);
                AppLog.d("FirebaseDB", "PaymentTypes: onChildChanged: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("FirebaseDB", "PaymentTypes: onChildChanged: DataSnapshot: Value:" + dataSnapshot.getValue());
                PaymentType editedPaymentType = dataSnapshot.getValue(PaymentType.class);
                editedPaymentType.setKey(dataSnapshot.getKey());
                MySpends.editPaymentType(editedPaymentType);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AppLog.d("FirebaseDB", "PaymentTypes: onChildRemoved: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("FirebaseDB", "PaymentTypes: onChildRemoved: DataSnapshot: Value:" + dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                AppLog.d("FirebaseDB", "PaymentTypes: onChildMoved: String" + s);
                AppLog.d("FirebaseDB", "PaymentTypes: onChildMoved: DataSnapshot: Key:" + dataSnapshot.getKey());
                AppLog.d("FirebaseDB", "PaymentTypes: onChildMoved: DataSnapshot: Value:" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AppLog.d("FirebaseDB", "PaymentTypes: onCancelled:" + databaseError.getDetails());
            }
        };
    }

    public static FirebaseDB initDb() {
        AppLog.d("FirebaseDB", "initDb");
        if (null == mFirebaseDB) {
            mFirebaseDB = new FirebaseDB();

        } else if (mFirebaseDB.mIsLoggedOut) {
            mFirebaseDB.initDBPaths();
            mFirebaseDB.mIsLoggedOut = Boolean.FALSE;
        }

        return mFirebaseDB;
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

    public void listenPaymentTypes() {
        if (null == mPaymentTypeListener) {
            initPaymentTypeListener();
        }
        paymentTypeRef.addChildEventListener(mPaymentTypeListener);
    }

    public void detachPaymentTypes() {
        if (null != mPaymentTypeListener) {
            paymentTypeRef.removeEventListener(mPaymentTypeListener);
        }
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

        Map<String, Object> values = new HashMap<>();
        values.put("amount", newExpense.getAmount());
        values.put("createdOn", newExpense.getCreatedOn());
        values.put("expenseDate", newExpense.getExpenseDate());
        values.put("note", newExpense.getNote());
        values.put("paymentTypeKey", newExpense.getPaymentTypeKey());
        values.put("updatedOn", newExpense.getUpdatedOn());
        values.put("categoryId", newExpense.getCategoryId());

        firebaseFirestore.collection("my-spends").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("spends").add(values)
                .addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void getFsSpends(DocumentSnapshot lastVisible, OnSuccessListener successListener, OnFailureListener failureListener) {

        com.google.firebase.firestore.Query query = fsSpendsRef.orderBy("expenseDate", com.google.firebase.firestore.Query.Direction.DESCENDING);

        if (null != lastVisible) {
            query = query.startAfter(lastVisible);
        }

        query.limit(AppConstants.PAGE_SPENDS_SIZE).get().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void getFsSpends(long lastVisible, OnSuccessListener successListener, OnFailureListener failureListener) {

        com.google.firebase.firestore.Query query = fsSpendsRef.orderBy("expenseDate", com.google.firebase.firestore.Query.Direction.DESCENDING);

        if (-1 != lastVisible) {
            //query = query.startAfter(lastVisible);
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
        values.put("categoryId", editedExpense.getCategoryId());

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

    public void togglePaymentType(String paymentTypeKey, boolean isChecked, DatabaseReference.CompletionListener completionListener) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("active", isChecked);
        paymentTypeRef.child(paymentTypeKey).updateChildren(values, completionListener);
    }

    public void listenSpends() {

        if (null != mSpendsListener && null == mSpendsListenerRegistration) {
            mSpendsListenerRegistration = fsSpendsRef.addSnapshotListener(mSpendsListener);

        } else {
            AppLog.d("FirebaseDB", "listenSpends: NULL listener");
        }
    }

    public void detachSpendsListener() {
        if (null != mSpendsListenerRegistration) {
            mSpendsListenerRegistration.remove();
        }
    }

    public void getExpenseCategories(ValueEventListener valueEventListener) {
        categoryRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void addNewCategory(DatabaseReference.CompletionListener completionListener) {
        ArrayList<Category> categories = prepareCategories();
        for (int index = 0; index < categories.size(); index++) {
            String categoryKey = categoryRef.push().getKey();
            AppLog.d("AddNewCategory", "Key:" + categoryKey + " :: Name:" + categories.get(index).getName());
            if (null != completionListener) {
                categoryRef.child(categoryKey).setValue(categories.get(index), completionListener);

            } else {
                categoryRef.child(categoryKey).setValue(categories.get(index));
            }
        }
    }

    private ArrayList<Category> prepareCategories() {
        String[] categoryNames = {"Beauty & Fitness", "Bills & Payments", "Books & Stationery",
                "Clothing", "Donation", "EMI", "Entertainment", "Food & Beverages", "Gifts", "Grocery",
                "Home", "Insurance", "Investments", "Maintenance", "Medical", "Miscellaneous", "Purchases",
                "Rent", "Service & Repairs", "Shopping", "Transport", "Travel", "Utility", "Vacation"};

        //Beauty & Fitness, EMI, Entertainment, Grocery, Investments, Shopping, Travel, Medical (instead of Healthcare)

        ArrayList<Category> categories = new ArrayList<>();
        Category category;
        for (int index = 0; index < categoryNames.length; index++) {
            category = new Category((index + 1), categoryNames[index]);
            categories.add(category);
        }

        return categories;
    }

    public DatabaseReference getMessageBoardRef() {
        return messageBoardRef;
    }

    public void saveMessage(MessageBoard messageBoard, DatabaseReference.CompletionListener completionListener) {
        String key = messageBoard.getKey();
        if (null == key) {
            key = messageBoardRef.push().getKey();
            AppLog.d("FirebaseDB", "saveMessage: Message Board:Push Key:" + key);
        }

        //messageBoardRef.child(key).setValue(messageBoard, completionListener);
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", messageBoard.getMessage());
        map.put("createdOn", messageBoard.getCreatedOn());
        map.put("updatedOn", messageBoard.getUpdatedOn());
        messageBoardRef.child(key).setValue(map, completionListener);
    }

    public void clearAll() {
        AppLog.d("FirebaseDB", "clearAll");
        currencyRef = null;
        paymentTypeRef = null;
        categoryRef = null;
        messageBoardRef = null;
        firebaseFirestore = null;
        fsSpendsRef = null;
    }

    public static void onLogout() {
        mFirebaseDB.clearAll();
        mFirebaseDB.mIsLoggedOut = Boolean.TRUE;
        AppLog.d("FirebaseDB", "onLogout");
    }
}
