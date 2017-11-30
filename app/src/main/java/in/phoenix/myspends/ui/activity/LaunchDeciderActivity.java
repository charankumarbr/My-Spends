package in.phoenix.myspends.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import in.phoenix.myspends.BuildConfig;
import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public class LaunchDeciderActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_signup);
        mPbLoading = findViewById(R.id.asl_pb_loading);

        if (AppUtil.isUserLoggedIn()) {
            getCurrency();
            MySpends.fetchPaymentTypes();

        } else {
            AppCompatButton btnLogin = findViewById(R.id.als_abtn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppUtil.isConnected()) {
                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    } else {
                        AppUtil.showSnackbar(findViewById(R.id.als_complete), "No Internet Connection!");
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    AppLog.d("Login", "Email not verified.");
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();

                } else {
                    AppLog.d("Login", "Email is verified!");
                }

                getCurrency();

            } else {
                // Sign in failed, check response for error code
                AppLog.d("Login", "Failed:" + response.getErrorCode());
            }
        }
    }

    private void getCurrency() {

        AppLog.d("User", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        final String currency = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY);
        if (null == currency) {
            mPbLoading.setVisibility(View.VISIBLE);
            DatabaseReference reference = FirebaseDB.initDb().getCurrencyReference();
            AppLog.d("Currency", "Key:" + reference.getKey());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPbLoading.setVisibility(View.GONE);
                    AppLog.d("Currency", "onDc: Key:" + dataSnapshot.getKey());
                    AppLog.d("Currency", "onDc: Count:" + dataSnapshot.getChildrenCount());
                    AppLog.d("Currency", "onDc: Value:" + dataSnapshot.getValue());

                    Intent nextIntent;
                    if ((null == dataSnapshot.getValue()) || dataSnapshot.getChildrenCount() != 3) {
                        nextIntent = new Intent(LaunchDeciderActivity.this, AppSetupActivity.class);

                    } else {
                        Currency currencyData = dataSnapshot.getValue(Currency.class);
                        if (null != currencyData && dataSnapshot.getChildrenCount() == 3) {
                            AppPref.getInstance().putString(AppConstants.PrefConstants.CURRENCY, currencyData.getCurrencySymbol());
                            AppPref.getInstance().putInt(AppConstants.PrefConstants.APP_SETUP, BuildConfig.VERSION_CODE);
                            nextIntent = new Intent(LaunchDeciderActivity.this, MainActivity.class);

                        } else {
                            nextIntent = new Intent(LaunchDeciderActivity.this, AppSetupActivity.class);
                        }
                    }

                    startActivity(nextIntent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mPbLoading.setVisibility(View.GONE);
                    startActivity(new Intent(LaunchDeciderActivity.this, AppSetupActivity.class));
                    finish();
                }
            });

        } else {
            startActivity(new Intent(LaunchDeciderActivity.this, MainActivity.class));
            finish();
        }
    }
}