package cn.sunnysky.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cn.sunnysky.R;
import cn.sunnysky.activities.MainActivity;

import java.util.Objects;

public class CommonNotification extends DialogFragment {

    private String tittle;

    public CommonNotification(String tittle) {
        super();
        this.tittle = tittle;
    }

    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(tittle);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);

                Objects.requireNonNull(getActivity()).finish();
            }
        });

        return builder.create();
    }
}
