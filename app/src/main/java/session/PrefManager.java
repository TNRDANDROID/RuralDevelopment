package session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.nic.RuralInspection.Utils.Utils;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import constant.AppConstant;


/**
 * Created by AchanthiSudan on 11/01/19.
 */
public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name

    private static final String APP_KEY = "AppKey";
    private static final String app_Version = "app_Version";
    private static final String KEY_USER_AUTH_KEY = "auth_key";
    private static final String KEY_USER_PASS_KEY = "pass_key";
    private static final String KEY_ENCRYPT_PASS = "pass";
    private static final String KEY_USER_NAME = "UserName";
    private static final String KEY_DECRYPT_KEY = "Decrypt_Key";
    private static final String KEY_DISTRICT_CODE = "District_Code";
    private static final String KEY_BLOCK_CODE = "Block_Code";
    private static final String KEY_PV_CODE = "Pv_Code";
    private static final String KEY_DISTRICT_NAME = "District_Name";
    private static final String KEY_BLOCK_NAME = "Block_Name";
    private static final String KEY_PV_NAME = "Pv_Name";
    private static final String KEY_LEVELS = "Levels";
    private static final String SERVICE_AUTH_KEY = "service_auth_key";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(AppConstant.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setAppKey(String appKey) {
        editor.putString(APP_KEY, appKey);
        editor.commit();
    }

    public String getAppKey() {
        return pref.getString(APP_KEY, null);
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }


    public void setUserAuthKey(String userAuthKey) {
        editor.putString(KEY_USER_AUTH_KEY, userAuthKey);
        editor.commit();
    }

    public String getUserAuthKey() {
        return pref.getString(KEY_USER_AUTH_KEY, null);
    }

    public void setUserPassKey(String userPassKey) {
        editor.putString(KEY_USER_PASS_KEY, userPassKey);
        editor.commit();
    }

    public String getUserPassKey() {
        return pref.getString(KEY_USER_PASS_KEY, null);
    }


    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public String   getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }


    public void setEncryptPass(String pass) {
        editor.putString(KEY_ENCRYPT_PASS, pass);
        editor.commit();
    }

    public String getEncryptPass() {
        return pref.getString(KEY_ENCRYPT_PASS, null);
    }

    public void setDistrictCode(String key) {
        editor.putString(KEY_DISTRICT_CODE, key);
        editor.commit();
    }

    public String getDistrictCode() {
        return pref.getString(KEY_DISTRICT_CODE, null);
    }


    public void setBlockCode(String key) {
        editor.putString(KEY_BLOCK_CODE, key);
        editor.commit();
    }

    public String getBlockCode() {
        return pref.getString(KEY_BLOCK_CODE, null);
    }



    public void setPvCode(String key) {
        editor.putString(KEY_PV_CODE, key);
        editor.commit();
    }

    public String getPvCode() {
        return pref.getString(KEY_PV_CODE, null);
    }




    public void setDistrictName(String key) {
        editor.putString(KEY_DISTRICT_NAME, key);
        editor.commit();
    }

    public String getDistrictName() {
        return pref.getString(KEY_DISTRICT_NAME, null);
    }

    public void setBlockName(String key) {
        editor.putString(KEY_BLOCK_NAME, key);
        editor.commit();
    }

    public String getBlockName() {
        return pref.getString(KEY_BLOCK_NAME, null);
    }


    public void setPvName(String key) {
        editor.putString(KEY_PV_NAME, key);
        editor.commit();
    }

    public String getPvName() {
        return pref.getString(KEY_PV_NAME, null);
    }




    public void setLevels(String key) {
        editor.putString(KEY_LEVELS, key);
        editor.commit();
    }

    public String getLevels() {
        return pref.getString(KEY_LEVELS, null);
    }



    public void setServiceAuthKey(String key) {
        editor.putString(SERVICE_AUTH_KEY, key);
        editor.commit();
    }

    public String getServiceAuthKey() {
        return pref.getString(SERVICE_AUTH_KEY, null);
    }



    public void clearSharedPreferences(Context context) {
        pref = _context.getSharedPreferences(AppConstant.PREF_NAME, PRIVATE_MODE);
        editor.clear();
        editor.apply();
    }
}
