package cn.sunnysky.activities;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.OperationCanceledException;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.dialogs.OperationProgressAnimator;
import com.google.android.material.snackbar.Snackbar;

import static cn.sunnysky.StudentClientApplication.internalNetworkHandler;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText passwordCheck;

    private CheckBox cb;

    private OperationProgressAnimator animator;
    private View onClickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.username = findViewById(R.id.RUN);
        this.password = findViewById(R.id.RPW);
        this.passwordCheck = findViewById(R.id.RPWC);

        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        cb = (CheckBox)findViewById(R.id.rcb);

        this.animator = new OperationProgressAnimator(this,R.string.registering);

        if (!StudentClientApplication.isNetworkPrepared()) {
            try {
                StudentClientApplication.initializeNetwork();
            } catch (NetworkErrorException e) {
                Snackbar.make(this.getCurrentFocus(), R.string.network_failure, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    public void onClickShow(View view) {

        if(view.getId() == R.id.cb)
            if(cb.isChecked())
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    public void onClickReturn(View view) {
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

        this.finish();
    }

    private String rsp;
    private Runnable register = new Runnable() {
        @Override
        public void run() {
            final String userName = username.getText().toString();
            final String original = password.getText().toString();

            rsp = internalNetworkHandler
                    .register(userName, original);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            performRegister();
        }
    };

    private void performRegister() {
        if (animator != null && animator.isShowing())
            animator.dismiss();

        if (rsp.startsWith("ERR")){
            Snackbar.make(onClickView, rsp, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void onClickRegister(View view) {
        onClickView = view;

        if (!password.getText().toString().contentEquals(
                passwordCheck.getText().toString())){
            Snackbar.make(onClickView, R.string.pwd_check_failure, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        animator.show();

        StudentClientApplication.join(register);
    }
}