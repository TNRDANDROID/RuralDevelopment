package com.nic.RuralInspection.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    JSONObject jsonObject;

    String sb;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.login_screen);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkLoginScreen();

                    }
                },500);
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        String username = userName.getText().toString().trim();
        prefManager.setUserName(username);
        String password = passwordEditText.getText().toString();

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
        String username = userName.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (Utils.isOnline()) {
            if (!validate())
                return;
            else if (prefManager.getUserName().length() > 0 && password.length() > 0) {
                new ApiService(this).makeRequest("LoginScreen", Api.Method.POST, UrlGenerator.getLoginUrl(), loginParams(), "not cache", this);
            } else {
                Utils.showAlert(this, "Please enter your username and password!");
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
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
                        jsonObject = new JSONObject(userDataDecrypt);
                        prefManager.setDistrictCode(String.valueOf(jsonObject.get(AppConstant.DISTRICT_CODE)));
                        prefManager.setBlockCode(String.valueOf(jsonObject.get(AppConstant.BLOCK_CODE)));
                        prefManager.setPvCode(String.valueOf(jsonObject.get(AppConstant.PV_CODE)));
                        prefManager.setDistrictName(String.valueOf(jsonObject.get(AppConstant.DISTRICT_NAME)));
                        prefManager.setBlockName(String.valueOf(jsonObject.get(AppConstant.BLOCK_NAME)));
                        prefManager.setPvName(String.valueOf(jsonObject.get(AppConstant.PV_NAME)));
                        prefManager.setLevels(String.valueOf( jsonObject.get(AppConstant.LEVELS)));
                        Log.d("userdata", "" + prefManager.getDistrictCode()+prefManager.getBlockCode()+prefManager.getPvCode()+prefManager.getDistrictName()+prefManager.getBlockName()+prefManager.getPvName()+prefManager.getLevels());
                        prefManager.setUserPassKey(decryptedKey);
                        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), jsonParams().toString().replaceAll(" ", "").replaceAll(",", ""));
                        Log.d("auth",""+authKey);
                        prefManager.setUserAuthKey(authKey);
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

    public ArrayList<String> blockListParams() {
        ArrayList<String> arrli = new ArrayList<String>();
        arrli.add("\"");
        arrli.add(AppConstant.KEY_SERVICE_ID);
        arrli.add(":");
        arrli.add(AppConstant.BLOCK_LIST_ALL);
        arrli.add("\"");
        //traversing elements of ArrayList object

        Log.d("paarams", "" + arrli);
        return arrli;

    }

    public JSONObject jsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.BLOCK_LIST_ALL);
        Log.d("object", "" + dataSet);
        return dataSet;
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

    public void loginMethod(String username, String password) {
        name = Base64.encodeToString((username).getBytes(), Base64.DEFAULT);
        pass = Base64.encodeToString((password).getBytes(), Base64.DEFAULT);


        if ((name.equals("") && pass.equals(""))) {
            userName.setFocusableInTouchMode(true);
            Utils.showAlert(LoginScreen.this, "Please Enter UserName");

        } else if (name.equals("")) {
            userName.setFocusableInTouchMode(true);
            Utils.showAlert(LoginScreen.this, "Please Enter UserName");

        } else if (pass.equals("")) {
            passwordEditText.setFocusableInTouchMode(true);
            Utils.showAlert(LoginScreen.this, "Please Enter Password");

        } else {
            if (Utils.isOnline()) {
//                if (!validate())
//                    return;
//                pDialog = ProgressHUD.show(this, "Connecting", true, false, null);


            } else {
                //Utils.showAlert(this, getResources().getString(R.string.no_internet));
                AlertDialog.Builder ab = new AlertDialog.Builder(
                        LoginScreen.this);
                ab.setMessage(Html
                        .fromHtml("<font color=#A52A2A>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..</font>"));
                ab.setPositiveButton("Setting Internet Connection",
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
                                offline_mode();
                            }
                        });
                ab.show();

            }
        }

    }


    public void offline_mode() {

    }

}
