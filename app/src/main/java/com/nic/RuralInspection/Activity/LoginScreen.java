package com.nic.RuralInspection.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyEditTextView;
import com.nic.RuralInspection.Support.ProgressHUD;
import com.nic.RuralInspection.Utils.FontCache;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ApiService;
import com.nic.RuralInspection.api.ServerResponse;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONObject;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private Button button;

    private MyEditTextView emailEditText;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private ShowHidePasswordEditText passwordEditText;
    private ProgressHUD pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.login_screen);
        intializeUI();
    }

    public void intializeUI() {
        button = (Button) findViewById(R.id.btn_sign_in);
        emailEditText = (MyEditTextView) findViewById(R.id.email_id);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        passwordEditText = (ShowHidePasswordEditText) findViewById(R.id.password);


        button.setOnClickListener(this);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputLayoutEmail.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        inputLayoutPassword.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        button.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        inputLayoutEmail.setHintTextAppearance(R.style.InActive);
        inputLayoutPassword.setHintTextAppearance(R.style.InActive);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    callSampleApi();
                }
                return false;
            }
        });
        passwordEditText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf"));
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            String emailId = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (Utils.isOnline()) {
//                if (!validate())
//                    return;
//                pDialog = ProgressHUD.show(this, "Connecting", true, false, null);
                callSampleApi();
            } else {
                Utils.showAlert(this, getResources().getString(R.string.no_internet));
            }
        }
    }

    private void callSampleApi() {

        new ApiService(this).makeRequest("petProfile", Api.Method.GET, "https://www.tnrd.gov.in/project/webservices_forms/inspection/login_services.php", "not cache", this);
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
//        pDialog.dismiss();
        JSONObject responseInnerJSONObj = null;
        String loginResponse = null;
        loginResponse = serverResponse.getResponse();

        Toast.makeText(this, loginResponse, Toast.LENGTH_LONG).show();
        showHomeScreen();
//            int status = responseInnerJSONObj.getInt("Status");
//            String message = responseInnerJSONObj.getString(AppConstant.KEY_MESSAGE);
//
//            if (status == 1) {
//                if ("SignIn".equalsIgnoreCase(apiType)) {
//                    JSONObject userJSONObject = responseInnerJSONObj.getJSONObject(AppConstant.KEY_USER);
//                    String id = userJSONObject.getString(AppConstant.KEY_USER_ID);
//                    String username = userJSONObject.getString(AppConstant.KEY_USER_NAME);
//                }
//            }

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
}
