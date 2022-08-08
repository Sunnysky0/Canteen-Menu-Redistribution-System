package cn.sunnysky.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.widget.TextView;
import androidx.annotation.NonNull;
import cn.sunnysky.R;

public class OperationProgressAnimator extends Dialog {

    public OperationProgressAnimator(@NonNull Context context) {
        super(context);
        this.setContentView(R.layout.dialog_progress_anim);
    }

    public OperationProgressAnimator(@NonNull Context context, String msg){
        super(context);

        this.setContentView(R.layout.dialog_progress_anim);

        TextView text = findViewById(R.id.msg);

        text.setText(msg);

    }

    public OperationProgressAnimator(@NonNull Context context, int msg){
        super(context);

        this.setContentView(R.layout.dialog_progress_anim);

        TextView text = findViewById(R.id.msg);

        text.setText(msg);
    }


}
