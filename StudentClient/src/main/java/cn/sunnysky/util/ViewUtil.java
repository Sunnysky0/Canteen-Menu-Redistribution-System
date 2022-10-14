package cn.sunnysky.util;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import cn.sunnysky.R;

public class ViewUtil {
    public static final View.OnClickListener tableCheckBoxOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof CheckBox) {
                final CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    checkBox.setText(R.string.selected);
                    checkBox.setButtonDrawable(R.mipmap.icon_v2);
                    checkBox.setTextSize(25);
                    checkBox.setTextColor(Color.GREEN);
                } else {
                    checkBox.setText(R.string.unselected);
                    checkBox.setButtonDrawable(R.mipmap.icon_v2_r);
                    checkBox.setTextSize(22);
                    checkBox.setTextColor(Color.LTGRAY);
                }
            }
        }
    };
}
