package cn.sunnysky.ui.request_channel;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.api.LogType;
import cn.sunnysky.databinding.FragmentSlideshowBinding;
import cn.sunnysky.dialogs.OperationProgressAnimator;
import cn.sunnysky.util.DateUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static cn.sunnysky.IntegratedManager.logger;

public class ChannelFragment extends Fragment {

    private ChannelViewModel channelViewModel;
    private FragmentSlideshowBinding binding;
    private Map<CheckBox,TextView> mapping;
    private @Nullable Map<String, String> loadedMapping;
    private TableLayout tableLayout;
    private Set<String> recommendedMenu = new HashSet<>();
    private volatile OperationProgressAnimator animator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        channelViewModel =
                new ViewModelProvider(this).get(ChannelViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);

        mapping = new HashMap<>();
        loadedMapping = StudentClientApplication.DATABASE_INSTANCE.readSerializedDataFromFile("chosenFood");

        View root = binding.getRoot();

        tableLayout = binding.TABLE;

        loadPublishedData();

        return root;
    }

    private void syncMenu(){

        String rsp = StudentClientApplication.internalNetworkHandler.getRecommendedMenu();

        if ( rsp.startsWith("ERR")){
            logger.log("Network failure", LogType.ERROR);

            final DialogFragment dialogFragment = new DialogFragment();
            dialogFragment.show(getParentFragmentManager(),getString(R.string.menu_not_published));
        }
        else {
            recommendedMenu.clear();

            recommendedMenu.addAll(Arrays.asList(rsp.split(",")));

            if (animator != null)
                animator.dismiss();

            Runnable renderThread = new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        recommendedMenu.forEach(render);
                    }
                }
            };


            new Handler(Looper.getMainLooper()).post(renderThread);

        }

    }

    @SuppressWarnings("NewApi")
    public Consumer<String> render = new Consumer() {
        @Override
        public void accept(Object v) {
            TextView[] buf = new TextView[2];

            TextView date = new TextView(getContext());
            date.setText(DateUtil.getCurrentTimeYMD());
            buf[0] = date;

            TextView foodName = new TextView(getContext());
            foodName.setText((CharSequence) v);
            buf[1] = foodName;


            TableRow tableRow = new TableRow(getActivity());

            for (TextView t : buf){
                t.setGravity(CENTER_HORIZONTAL);
                t.setTextSize(22);
                tableRow.addView(t);
            }

            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setGravity(Gravity.CENTER);
            tableRow.addView(checkBox);

            String s;

            if (loadedMapping != null && !loadedMapping.keySet().isEmpty())
                if ((s = loadedMapping.get(v)) != null)
                    if (Boolean.parseBoolean(s))
                        checkBox.setChecked(true);

            if (mapping != null)
                mapping.put(checkBox,foodName);


            tableLayout.addView(tableRow);
        }
    };

    public void loadPublishedData(){
        animator = new OperationProgressAnimator(Objects.requireNonNull(getContext()),
                R.string.synchornizing);
        if (animator != null)
            animator.show();
        StudentClientApplication.join(this::syncMenu);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Map<String,Boolean> finalMapping = new HashMap<>();

        StudentClientApplication.DATABASE_INSTANCE.createNewFileInstance("chosenFood");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mapping.forEach((checkBox, textView) -> {
                if (checkBox.isChecked())
                    finalMapping.put((String) textView.getText(),true);
            });
        }

        StudentClientApplication.DATABASE_INSTANCE.writeSerializedData(finalMapping,"chosenFood");

        binding = null;
    }
}