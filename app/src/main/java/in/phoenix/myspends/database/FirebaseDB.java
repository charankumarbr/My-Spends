package in.phoenix.myspends.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.phoenix.myspends.model.Currency;

/**
 * Created by Charan.Br on 11/25/2017.
 */

public final class FirebaseDB {

    private static FirebaseDB mFirebaseDB;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;

    private FirebaseDB() {
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
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
        getDatabaseReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currency").setValue(selectedCurrency);
    }

    public String getCurrency() {
        return getDatabaseReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();
    }

    public DatabaseReference getCurrencyReference() {
        return databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currency");
    }
}
