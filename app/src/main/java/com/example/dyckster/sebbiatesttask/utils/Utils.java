package com.example.dyckster.sebbiatesttask.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());


    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = SebbiaTestTaskApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = SebbiaTestTaskApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(int orientation) {
        Resources resources = SebbiaTestTaskApplication.getInstance().getResources();
        int id;
        /*id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id == 0 || !resources.getBoolean(id))
            return 0;//не отображается*///не работает

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                id = resources.getIdentifier("navigation_bar_height_landscape", "dimen", "android");
                break;
            default:
                id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                break;
        }
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    public static boolean isTablet() {
        Context context = SebbiaTestTaskApplication.getInstance();
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    private static Point screenSize;
    private static int miniumWidth;

    public static int getMinimumDeviceWidth() {
        if (screenSize == null) {
            screenSize = new Point();
            ((WindowManager) SebbiaTestTaskApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
            miniumWidth = screenSize.x < screenSize.y ? screenSize.x : screenSize.y;
            if (SebbiaTestTaskApplication.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                miniumWidth += getNavigationBarHeight(Configuration.ORIENTATION_LANDSCAPE);
            }
        }
        return miniumWidth;
    }

    public static boolean isTelephonyEnabled() {
        TelephonyManager tm = (TelephonyManager) SebbiaTestTaskApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    public static byte[] serialize(Serializable object) {
        try {
            byte[] result;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            result = bos.toByteArray();
            out.close();
            bos.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deserialize(byte[] bytes) {
        try {
            T result;
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            result = (T) in.readObject();
            bis.close();
            in.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDaysBetween(Date a, Date b) {
        return (int) TimeUnit.MILLISECONDS.toDays(a.getTime() - b.getTime());
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.+-]+\\.[a-zA-Z]{2,4}$");
        Matcher m = pattern.matcher(email);
        return m.find();
    }

    public static boolean isPhoneValid(String phone) {
        Pattern pattern = Pattern.compile("^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$");
        Matcher m = pattern.matcher(phone);
        return m.find();
    }

    public static String join(String a, String b, String delimeter) {
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b))
            return null;
        if (TextUtils.isEmpty(a))
            return b;
        if (TextUtils.isEmpty(b))
            return a;
        return a + delimeter + b;
    }

    public static void showKeyboard(final View view) {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        }, 100);
    }

    public static void hideKeyboard(final View view) {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 100);
    }

    public static String capitalize(String input) {
        if (TextUtils.isEmpty(input) == false) {
            input = input.toLowerCase(Locale.US);
            input = Character.toUpperCase(input.charAt(0)) + input.substring(1);
        }
        return input;
    }

    public static Drawable tintDrawable(int resource, int colorRes) {
        Resources resources = SebbiaTestTaskApplication.getInstance().getResources();
        return setColorFilter(resources.getDrawable(resource), resources.getColor(colorRes), PorterDuff.Mode.SRC_ATOP);
    }

    public static Drawable setColorFilter(Drawable src, int color, PorterDuff.Mode mode) {
        src = src.mutate();
        src.setColorFilter(color, mode);
        return src;
    }

    public static String getSuffix(int count, int res_1, int res_2_4, int res_5_0) {
        Context context = SebbiaTestTaskApplication.getInstance();
        int rem = count % 10;
        if (count >= 10 && count <= 20) {
            return context.getString(res_5_0);
        } else if (rem == 1) {
            return context.getString(res_1);
        } else if (rem >= 2 && rem <= 4) {
            return context.getString(res_2_4);
        } else {
            return context.getString(res_5_0);
        }
    }

    public static boolean isInternetAvailable(Context context) {
        if (context == null) {
            context = SebbiaTestTaskApplication.getInstance();
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    public static String getCurrentAppVersion() {
        try {
            PackageInfo packageInfo = SebbiaTestTaskApplication.getInstance().getPackageManager().getPackageInfo(SebbiaTestTaskApplication.getInstance().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Failed to get current version");
            return null;
        }
    }



    public static Date getDateFromString(String string) {
        final DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }




}
