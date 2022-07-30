package cn.sunnysky.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.sunnysky.IntegratedManager;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.user.security.SecurityManager;
import com.google.android.material.snackbar.Snackbar;

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
    }

    public void onClickRG(View view) {
    }

    private Runnable login = new Runnable() {
        @Override
        public void run() {
            final String userName = username.getText().toString();
            final String original = password.getText().toString();
            final String encryptedPwd = original;
            
            rsp = StudentClientApplication.internalNetworkHandler
                    .login(userName, encryptedPwd);
            processBlock = false;
        }
    };

    public void onClickLogin(View view) {
        if (StudentClientApplication.internalNetworkHandler
            != null){

            StudentClientApplication.join(login);

            long timeStamp = SystemClock.currentThreadTimeMillis() + 1000;
            while (processBlock || timeStamp > SystemClock.currentThreadTimeMillis())
                IntegratedManager.logger.log("Waiting for response");

            if (rsp.startsWith("ERR"))
                Snackbar.make(view, rsp, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            else if (rsp.length() != 32)
                Snackbar.make(view, R.string.network_failure, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            else {
                Snackbar.make(view, R.string.login_success, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                IntegratedManager.temporaryUserActivationCode = rsp;

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                this.finish();
            }

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
        if(view.getId() == R.id.cb){
            if(cb.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void onClickRem(View view) {
    }
}