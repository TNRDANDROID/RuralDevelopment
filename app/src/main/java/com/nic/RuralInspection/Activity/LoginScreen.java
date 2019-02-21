package com.nic.RuralInspection.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyEditTextView;
import com.nic.RuralInspection.Utils.FontCache;
import com.nic.RuralInspection.Utils.UrlGenerator;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ApiService;
import com.nic.RuralInspection.api.ServerResponse;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private Button login_btn;
    private String name, pass, randString;

    private MyEditTextView userName, passWord;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private ShowHidePasswordEditText passwordEditText;
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    JSONObject jsonObject;

    String sb;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.login_screen);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        login_btn = (Button) findViewById(R.id.btn_sign_in);
        userName = (MyEditTextView) findViewById(R.id.user_name);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        passwordEditText = (ShowHidePasswordEditText) findViewById(R.id.password);


        login_btn.setOnClickListener(this);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputLayoutEmail.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        inputLayoutPassword.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        login_btn.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        inputLayoutEmail.setHintTextAppearance(R.style.InActive);
        inputLayoutPassword.setHintTextAppearance(R.style.InActive);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkLoginScreen();
                }
                return false;
            }
        });
        passwordEditText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf"));
        randString = Utils.randomChar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                checkLoginScreen();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        checkLoginScreen();
//
//                    }
//                }, 500);
//                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        String username = userName.getText().toString().trim();
        prefManager.setUserName(username);
        String password = passwordEditText.getText().toString().trim();


        if (username.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the username");
        } else if (password.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the password");
        }
        return valid;
    }

    private void checkLoginScreen() {
        if ((Utils.isOnline())) {

            try{
                db.delete(DBHelper.BLOCK_TABLE_NAME,null,null);
                db.delete(DBHelper.VILLAGE_TABLE_NAME,null,null);
                db.delete(DBHelper.SCHEME_TABLE_NAME,null,null);
                db.delete(DBHelper.FINANCIAL_YEAR_TABLE_NAME,null ,null);
                db.delete(DBHelper.WORK_STAGE_TABLE,null ,null);
                db.delete(DBHelper.WORK_LIST_OPTIONAL,null,null);
                db.delete(DBHelper.INSPECTION,null,null);
                db.delete(DBHelper.CAPTURED_PHOTO,null,null);
                db.delete(DBHelper.OBSERVATION_TABLE,null,null);


            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String username = userName.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        prefManager.setUserPassword(password);

        if (Utils.isOnline()) {
            if (!validate())
                return;
            else if (prefManager.getUserName().length() > 0 && password.length() > 0) {
                new ApiService(this).makeRequest("LoginScreen", Api.Method.POST, UrlGenerator.getLoginUrl(), loginParams(), "not cache", this);
            } else {
                Utils.showAlert(this, "Please enter your username and password!");
            }
        } else {
            //Utils.showAlert(this, getResources().getString(R.string.no_internet));
            AlertDialog.Builder ab = new AlertDialog.Builder(
                    LoginScreen.this);
            ab.setMessage("Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..");
            ab.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            Intent I = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(I);
                        }
                    });
            ab.setNegativeButton("Continue With Off-Line",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            offline_mode(username,password);
                        }
                    });
            ab.show();
        }
    }


    public Map<String, String> loginParams() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstant.KEY_SERVICE_ID, "login");


        String random = Utils.randomChar();

        params.put(AppConstant.USER_LOGIN_KEY, random);
        Log.d("randchar", "" + random);

        params.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        Log.d("user", "" + userName.getText().toString().trim());

        String encryptUserPass = Utils.md5(passwordEditText.getText().toString().trim());
        prefManager.setEncryptPass(encryptUserPass);
        Log.d("md5", "" + encryptUserPass);

        String userPass = encryptUserPass.concat(random);
        Log.d("userpass", "" + userPass);
        String sha256 = Utils.getSHA(userPass);
        Log.d("sha", "" + sha256);

        params.put(AppConstant.KEY_USER_PASSWORD, sha256);


        Log.d("user", "" + userName.getText().toString().trim());


        return params;
    }

//    private void callSampleApi() {
//
//        new ApiService(this).makeRequest("sample", Api.Method.POST, "https://www.tnrd.gov.in/project/webservices_forms/login_service/login_services.php", loginParams(), "not cache", this);
//
//    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status = loginResponse.getString(AppConstant.KEY_STATUS);
            String response = loginResponse.getString(AppConstant.KEY_RESPONSE);
            String message = loginResponse.getString(AppConstant.KEY_MESSAGE);
            if ("LoginScreen".equals(urlType)) {
                if (status.equalsIgnoreCase("OK")) {
                    if (response.equals("LOGIN_SUCCESS")) {
                        String key = loginResponse.getString(AppConstant.KEY_USER);
                        String user_data = loginResponse.getString(AppConstant.USER_DATA);
                        String decryptedKey = Utils.decrypt(prefManager.getEncryptPass(), key);
                        String userDataDecrypt = Utils.decrypt(prefManager.getEncryptPass(), user_data);
                        Log.d("userdatadecry", "" + userDataDecrypt);
                        jsonObject = new JSONObject(userDataDecrypt);
                        prefManager.setDistrictCode(jsonObject.get(AppConstant.DISTRICT_CODE));
                        prefManager.setBlockCode(jsonObject.get(AppConstant.BLOCK_CODE));
                        prefManager.setPvCode(jsonObject.get(AppConstant.PV_CODE));
                        prefManager.setDistrictName(jsonObject.get(AppConstant.DISTRICT_NAME));
                        prefManager.setBlockName(jsonObject.get(AppConstant.BLOCK_NAME));
                        prefManager.setPvName(jsonObject.get(AppConstant.PV_NAME));
                        prefManager.setLevels(jsonObject.get(AppConstant.LEVELS));
                        Log.d("userdata", "" + prefManager.getDistrictCode() + prefManager.getBlockCode() + prefManager.getPvCode() + prefManager.getDistrictName() + prefManager.getBlockName() + prefManager.getPvName() + prefManager.getLevels());
                        prefManager.setUserPassKey(decryptedKey);
                        showHomeScreen();
                    } else {
                        if (response.equals("LOGIN_FAILED")) {
                            Utils.showAlert(this, "Invalid UserName Or Password");
                        }
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        showHomeScreen();
//    }

    private void showHomeScreen() {
        Intent intent = new Intent(LoginScreen.this, Dashboard.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void offline_mode(String name,String pass) {
        String userName = prefManager.getUserName();
        String password = prefManager.getUserPassword();
        if(name.equals(userName) && pass.equals(password)) {
            showHomeScreen();
        }
        else {
            Utils.showAlert(this,"No data available for offline. Please Turn On Your Network");
        }
    }

}
