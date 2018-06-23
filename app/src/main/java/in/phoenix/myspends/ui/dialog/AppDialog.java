package in.phoenix.myspends.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import in.phoenix.myspends.R;
import in.phoenix.myspends.util.AppConstants;

/**
 * Created by Charan.Br on 10/4/2017.
 */

public final class AppDialog {

    private static ProgressDialog progressDialog = null;

    public static void display2BtnDialog(Context context, String message,
                                         @AppConstants.DialogConstants final int action) {
        AppDialogListener appDialogListener = null;
        if (context instanceof AppDialogListener) {
            appDialogListener = (AppDialogListener) context;
        }
        AlertDialog.Builder noNetDialog = new AlertDialog.Builder(context);
        noNetDialog.setTitle(R.string.app_name);
        noNetDialog.setMessage(message);

        final AppDialogListener finalAppDialogListener = appDialogListener;
        noNetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (null != finalAppDialogListener) {
                    finalAppDialogListener.onDialogButtonClick(action);
                }
            }
        });
        noNetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noNetDialog.create().show();
    }

    public interface AppDialogListener {
        void onDialogButtonClick(@AppConstants.DialogConstants int action);
    }

    public static void showDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissDialog() {
        if ((null != progressDialog) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

}
