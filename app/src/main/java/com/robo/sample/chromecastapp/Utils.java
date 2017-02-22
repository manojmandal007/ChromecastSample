
package com.robo.sample.chromecastapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;



public class Utils {
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";


    public static void hideKeyboard(Activity context) {
        if (context != null && context.getCurrentFocus() != null) {
            InputMethodManager keyBoardHandle = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyBoardHandle.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    public static void displayAlertDialogWithTwoBtn(Context ctx, String msg, String positiveBtnTxt, DialogInterface.OnClickListener positiveBtnListener,
                                                    String negativeBtnTxt, DialogInterface.OnClickListener negativeListener) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle(R.string.app_name).setMessage(msg).setCancelable(false)
                    .setPositiveButton(positiveBtnTxt, positiveBtnListener).setNegativeButton(negativeBtnTxt, negativeListener);
            alertDialog.create();
            alertDialog.show();

        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public static void displayAlertDialogWithOneBtn(Context ctx, String msg, String positiveBtnTxt, DialogInterface.OnClickListener positiveBtnListener) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle(R.string.app_name).setMessage(msg).setCancelable(false)
                    .setPositiveButton(positiveBtnTxt, positiveBtnListener);
            alertDialog.create();
            alertDialog.show();

        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }
        /*
     * Function to convert milliseconds time to Timer Format
	 * Hours:Minutes:Seconds
	 */

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes > 10)
            finalTimerString = finalTimerString + minutes + ":" + secondsString;
        else
            finalTimerString = finalTimerString + "0" + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }



    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
        }
    }



    public static boolean isICSMR1andAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    public static void setOrientation(Activity activity, int orientation) {
        if (activity != null)
            activity.setRequestedOrientation(orientation);
    }


    public static String getEncodedUrl(String url) {
        return Uri.encode(url, ALLOWED_URI_CHARS);
    }


}
