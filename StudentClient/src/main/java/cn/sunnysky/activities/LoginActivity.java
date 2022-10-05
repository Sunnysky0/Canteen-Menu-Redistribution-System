package cn.sunnysky.activities;

import android.accounts.NetworkErrorException;
import android.content.Intent;
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
import cn.sunnysky.dialogs.OperationProgressAnimator;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import static cn.sunnysky.StudentClientApplication.DATABASE_INSTANCE;
import static cn.sunnysky.StudentClientApplication.internalNetworkHandler;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    private CheckBox cb;
    private CheckBox rm;

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
        rm = findViewById(R.id.rm);

        Map<String,String> userMap = DATABASE_INSTANCE.readSerializedDataFromFile("lastLoginData");

        if (userMap != null && !userMap.keySet().isEmpty()){
            this.username.setText((CharSequence) userMap.keySet().toArray()[0]);
            this.password.setText((CharSequence) userMap.values().toArray()[0]);
        }
    }

    public void onClickRG(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private Runnable login = new Runnable() {
        @Override
        public void run() {
            final String userName = username.getText().toString();
            final String original = password.getText().toString();

            rsp = internalNetworkHandler
                    .login(userName, original);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            performLogin();
        }
    };

    private OperationProgressAnimator notification;
    private void performLogin(){

        notification.dismiss();

        if (rsp.startsWith("ERR") || rsp.length() != 32)
            Snackbar.make(onClickView, rsp, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        else{

            IntegratedManager.setTemporaryUserActivationCode(rsp);

            if (rm.isChecked()){
                final String userName = username.getText().toString();
                final String original = password.getText().toString();

                Map<String,String> userMap = new HashMap<>();

                userMap.put(userName,original);

                DATABASE_INSTANCE.createNewFileInstance("lastLoginData");
                DATABASE_INSTANCE.writeSerializedData(userMap,"lastLoginData");
            }

            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


    private View onClickView;
    public void onClickLogin(View view) {
        if (!StudentClientApplication.isNetworkPrepared()) {
            StudentClientApplication.join(() -> {
                try {
                    StudentClientApplication.initializeNetwork();
                } catch (NetworkErrorException e) {
                    Snackbar.make(view, R.string.server_failure, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        if (internalNetworkHandler
            != null){

            onClickView = view;

            notification = new OperationProgressAnimator(this,R.string.logining_in);

            notification.show();
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