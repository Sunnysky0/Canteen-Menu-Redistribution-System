package cn.sunnysky.activities;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.SystemClock;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.dialogs.LoginMessageNotification;
import com.google.android.material.snackbar.Snackbar;

import static cn.sunnysky.IntegratedManager.logger;
import static cn.sunnysky.StudentClientApplication.internalNetworkHandler;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    private CheckBox cb;

    private boolean visible;

    private String rsp = "";
    private boolean processBlock = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.username = findViewById(R.id.IUN);
        this.password = findViewById(R.id.IPW);

        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        cb = (CheckBox)findViewById(R.id.cb);

        if (internalNetworkHandler == null) {
            try {
                StudentClientApplication.initializeNetwork();
            } catch (NetworkErrorException e) {
                Snackbar.make(this.getCurrentFocus(), R.string.network_failure, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    public void onClickRG(View view) {
    }

    private Runnable login = new Runnable() {
        @Override
        public void run() {
            final String userName = username.getText().toString();
            final String original = password.getText().toString();
            final String encryptedPwd = original;
            
            rsp = internalNetworkHandler
                    .login(userName, encryptedPwd);

            performLogin();
        }
    };

    private void performLogin(){
        if (rsp.startsWith("ERR") || rsp.length() != 32)
            new LoginMessageNotification(false).show(getSupportFragmentManager(),"");
        else{
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClickLogin(View view) {
        if (internalNetworkHandler
            != null){

            Snackbar.make(view, R.string.logining_in, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            StudentClientApplication.join(login);
        }
    }

    public void onClickExit(View view) {
        if(view.getId() == R.id.EXIT){
            finishAndRemoveTask();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
    }

    public void onClickShow(View view) {

        if(view.getId() == R.id.cb)
            if(cb.isChecked())
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    public void onClickRem(View view) {
    }
}