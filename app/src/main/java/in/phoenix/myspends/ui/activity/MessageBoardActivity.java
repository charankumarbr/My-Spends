package in.phoenix.myspends.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import in.phoenix.myspends.R;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.MessageBoard;
import in.phoenix.myspends.ui.dialog.AppDialog;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

public class MessageBoardActivity extends BaseActivity {

    private TextView mCTvMessage;
    private View mVHeader;
    private EditText mEtMessage;

    private MenuItem mMiEdit;
    private MenuItem mMiDone;

    private MessageBoard mMessageBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        init();
    }

    private void init() {
        initLayout();
        Toolbar toolbar = findViewById(R.id.amb_in_toolbar);
        toolbar.setTitle(R.string.message_board);
        setSupportActionBar(toolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setElevation(0f);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mCTvMessage = findViewById(R.id.amb_tv_message);
        mVHeader = findViewById(R.id.amb_tv_header);
        mEtMessage = findViewById(R.id.amb_et_message);

        getMessage();
    }

    private void getMessage() {
        AppDialog.showDialog(MessageBoardActivity.this, "Fetching Message Board...");
        FirebaseDB.initDb().getMessageBoardRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (null == dataSnapshot.getValue()) {
                    AppLog.d("MessageBoard", "Value: NULL");
                    mMessageBoard = new MessageBoard();

                } else {
                    AppLog.d("MessageBoard", "Value:" + dataSnapshot.getValue());
                    AppLog.d("MessageBoard", "Key:" + dataSnapshot.getKey());
                    AppLog.d("MessageBoard", "Children count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() >= 1) {
                        Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds = iter.next();
                        AppLog.d("MessageBoard", "--------------");
                        AppLog.d("MessageBoard", "DS Value:" + ds.getValue());
                        AppLog.d("MessageBoard", "DS Key:" + ds.getKey());
                        mMessageBoard = ds.getValue(MessageBoard.class);
                        mMessageBoard.setKey(ds.getKey());
                    }
                }

                mCTvMessage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppDialog.dismissDialog();
                        showMessage();
                    }
                }, 1000);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AppDialog.dismissDialog();
                AppUtil.showToast("Unable to fetch Message Board.");
                finish();
            }
        });
    }

    private void showMessage() {
        if (null == mMessageBoard || null == mMessageBoard.getKey()) {
            //-- allow to edit the message directly, since no message stored by the user yet! --//
            canEdit(Boolean.TRUE);

            /*if (null != mMessageBoard) {
                mEtMessage.append(mMessageBoard.getMessage());
            }*/
            return;
        }

        canEdit(Boolean.FALSE);
        AppLog.d("MessageBoard", "Message:" + mMessageBoard.toString());
        mCTvMessage.setText(mMessageBoard.getMessage());
    }

    private void canEdit(Boolean editStatus) {
        mMiEdit.setVisible(!editStatus);
        mMiDone.setVisible(editStatus);

        mCTvMessage.setVisibility(editStatus ? View.GONE : View.VISIBLE);
        mVHeader.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        mEtMessage.setVisibility(editStatus ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_board, menu);
        mMiEdit = menu.findItem(R.id.menu_edit);
        mMiDone = menu.findItem(R.id.menu_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            canEdit(Boolean.TRUE);
            mEtMessage.append(mMessageBoard.getMessage());
            return true;

        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.menu_done) {
            if (!TextUtils.isEmpty(mEtMessage.getText().toString().trim())) {
                if (null == mMessageBoard.getMessage() || ((null != mMessageBoard.getMessage()) &&
                        !mEtMessage.getText().toString().equals(mMessageBoard.getMessage()))) {
                    prepareData();
                    saveMessage();

                } else {
                    AppLog.d("MessageBoard","Not reqd to save!");
                    AppUtil.toggleKeyboard(mViewComplete, false);
                    showMessage();
                    mEtMessage.setText("");
                }

            } else {
                AppUtil.showToast("Enter the message to be saved.");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveMessage() {
        if (AppUtil.isConnected()) {
            FirebaseDB.initDb().saveMessage(mMessageBoard, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (null == databaseError) {
                        AppLog.d("MessageBoard", "Local Key:" + mMessageBoard.getKey());
                        if (null == mMessageBoard.getKey()) {
                            String key = databaseReference.getKey();
                            AppLog.d("MessageBoard", "Ref Key:" + key);
                            mMessageBoard.setKey(key);
                        }
                        showMessage();
                        mEtMessage.setText("");

                    } else {
                        AppUtil.showToast("Unable to save the message.");
                    }
                }
            });
            AppUtil.toggleKeyboard(mViewComplete,false);

        } else {
            AppUtil.showToast(R.string.no_internet);
        }
    }

    private void prepareData() {
        mMessageBoard.setMessage(mEtMessage.getText().toString());
        long timeInMillis = System.currentTimeMillis();
        if (null == mMessageBoard.getKey()) {
            mMessageBoard.setCreatedOn(timeInMillis);
        }
        mMessageBoard.setUpdatedOn(timeInMillis);
    }
}
