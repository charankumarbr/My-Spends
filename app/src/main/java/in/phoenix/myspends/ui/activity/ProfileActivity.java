package in.phoenix.myspends.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 12/22/2017.
 */

public class ProfileActivity extends BaseActivity {

    private TextView cTvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.ap_toolbar);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }*/
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        cTvData = findViewById(R.id.ap_tv_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cTvData.setText(user.getDisplayName());
        cTvData = null;
        cTvData = findViewById(R.id.ap_tv_email);
        cTvData.setText(user.getEmail());
        /*cTvData = findViewById(R.id.ap_tv_version);
        cTvData.setText("v " + BuildConfig.VERSION_NAME);*/

        findViewById(R.id.ap_tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });

        cTvData = findViewById(R.id.ap_tv_currency);

        getCurrency();
    }

    private void getCurrency() {

        FirebaseDB.initDb().getCurrencyReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    if (null != dataSnapshot.getValue() && dataSnapshot.getChildrenCount() == 3) {
                        Currency currencyData = dataSnapshot.getValue(Currency.class);
                        cTvData.setText(currencyData.getCurrencySymbol() + " - " + currencyData.getCurrencyName() + " (" + currencyData.getCurrencyCode() + ")");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (AppUtil.isConnected()) {
                            AuthUI.getInstance()
                                    .signOut(ProfileActivity.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AppPref.getInstance().clearAll();
                                                MySpends.clearAll();
                                                FirebaseDB.onLogout();
                                                AppUtil.removeDynamicShortcut();
                                                Intent newIntent = new Intent(ProfileActivity.this, LaunchDeciderActivity.class);
                                                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(newIntent);
                                                finish();

                                            } else {
                                                AppUtil.showToast("Unable to logout.");
                                            }
                                        }
                                    });
                        } else {
                            AppUtil.showToast(R.string.no_internet);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        logoutBuilder.create().show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
