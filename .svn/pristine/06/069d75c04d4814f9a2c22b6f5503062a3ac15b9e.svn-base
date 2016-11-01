package br.com.speedy.ipapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Henrique Araújo on 2015-03-06.
 */
public class SharedPreferencesUtil {

    public static boolean writePreferences(final Context mContext,
                                           final String key, final String value) {
        boolean booResult;
        try {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext.getApplicationContext());
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
            booResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            booResult = false;
        }
        return booResult;
    }

    public static String getPreferences(final Context mContext, final String key) {
        String strResult = null;
        try {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext.getApplicationContext());
            strResult = sharedPreferences.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult != null && !strResult.equals("") ? strResult : null;
    }
}
