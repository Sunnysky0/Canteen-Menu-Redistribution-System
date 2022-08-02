package cn.sunnysky.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cn.sunnysky.R;
import cn.sunnysky.activities.LoginActivity;
import cn.sunnysky.activities.MainActivity;

public class LoginMessageNotification extends DialogFragment {

    private boolean login_State;

    public LoginMessageNotification(boolean login_State) {
        super();
        this.login_State = login_State;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.login_msg_head);

        if (login_State)
            builder.setMessage(R.string.login_success);
        else
            builder.setMessage(R.string.login_failure);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (login_State) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
