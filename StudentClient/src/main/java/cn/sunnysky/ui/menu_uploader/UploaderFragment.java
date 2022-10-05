package cn.sunnysky.ui.menu_uploader;

import android.accounts.NetworkErrorException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import cn.sunnysky.IntegratedManager;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.activities.MainActivity;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.databinding.FragmentGalleryBinding;
import cn.sunnysky.dialogs.OperationProgressAnimator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class UploaderFragment extends Fragment {

    private UploaderViewModel uploaderViewModel;
    private Map<CheckBox,TextView> mapping;
    private @Nullable Map<String, String> loadedMapping;
    private FragmentGalleryBinding binding;
    private TableLayout tableLayout;
    private View bindingRoot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        uploaderViewModel =
                new ViewModelProvider(this).get(UploaderViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        bindingRoot = binding.getRoot();

        ((MainActivity) getActivity()).hideFab();

        mapping = new HashMap<>();
        loadedMapping = StudentClientApplication.DATABASE_INSTANCE.readSerializedDataFromFile("chosenMenu");

        tableLayout = binding.TABLE;

        binding.UPLOADBTN.setOnClickListener(this::onClickUpload);

        synchronize();

        return bindingRoot;
    }

    private void loadData() throws URISyntaxException {
        Map<String, String> map = ((DefaultFileManager) IntegratedManager.fileManager)
                .readSerializedDataFromFile(
                        new File(getActivity().getFilesDir().getPath()
                                + "/download/food_data_s1.fson").toURI(),
                        this::consume);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            assert map != null;
            map.forEach(this::renderRow);
        }
    }

    private void renderRow(String k,String v){

        TextView[] buf = new TextView[2];

        TextView name = new TextView(getContext());
        name.setText(k);
        buf[0] = name;

        TextView category = new TextView(getContext());
        final String[] strings = v.split(",");
        category.setText(strings[strings.length - 1]);
        buf[1] = category;


        TableRow tableRow = new TableRow(getActivity());

        for (TextView t : buf){
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextSize(22);
            tableRow.addView(t);
        }

        final CheckBox checkBox = new CheckBox(getContext());
        tableRow.addView(checkBox);

        String s;

        if (loadedMapping != null && !loadedMapping.keySet().isEmpty())
            if ((s = loadedMapping.get(k)) != null)
                if (Boolean.parseBoolean(s))
                    checkBox.setChecked(true);

        if (mapping != null)
            mapping.put(checkBox,name);


        this.tableLayout.addView(tableRow);
    }

    private void consume(String str, Map<String,String> map){
        String[] temp = str.split(";");

        String target = "";
        String path = "";

        for( String s : temp){
            if(s.startsWith("target")) target = s.split(":")[1];
            else if(s.startsWith("path")) path = s.split(":")[1];
        }

        map.put(target,path);

    }



    private void synchronize() {

        OperationProgressAnimator animator = new OperationProgressAnimator(Objects.requireNonNull(getContext()), R.string.synchornizing);

        animator.show();


        Runnable renderThread = () -> {
            try {
                loadData();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };

        Runnable networkThread = new Runnable() {
            @Override
            public void run() {
                boolean b = false;
                try {
                    StudentClientApplication.initializeNetwork();

                    b = StudentClientApplication.internalNetworkHandler.synchronize(
                            getActivity().getFilesDir().getPath() + "/download");
                } catch (NetworkErrorException
                        | URISyntaxException
                        | IOException e) {
                    e.printStackTrace();
                }

                animator.dismiss();

                if (b)
                    Snackbar.make(bindingRoot, R.string.synchornized, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else Snackbar.make(bindingRoot, R.string.network_failure, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new Handler(Looper.getMainLooper()).post(renderThread);
            }
        };

        StudentClientApplication.join(networkThread);
    }

    private void performUpload() {
        String[] strs = new String[mapping.size()];

        AtomicInteger i = new AtomicInteger(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mapping.forEach((checkBox, textView) -> {
                if (checkBox.isChecked())
                    strs[i.getAndIncrement()] = (String) textView.getText();
            });
        }

        int f = 0;
        for (String str : strs)
            if (str != null && !str.contentEquals(""))
                f++;

        String[] menu = new String[f];
        System.arraycopy(strs, 0, menu, 0, f);

        final String rsp = StudentClientApplication.internalNetworkHandler.uploadMenu(menu);

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            if (animator != null && animator.isShowing())
                animator.dismiss();
            e.printStackTrace();
        }

        if (animator != null && animator.isShowing())
            animator.dismiss();

        Snackbar.make(onClickView, R.string.upload_success, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    private OperationProgressAnimator animator;
    private View onClickView;
    public void onClickUpload(View view){
        onClickView = view;
        animator = new OperationProgressAnimator(
                Objects.requireNonNull(getContext()),R.string.uploading);

        animator.show();

        StudentClientApplication.join(this::performUpload);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Map<String,Boolean> finalMapping = new HashMap<>();

        StudentClientApplication.DATABASE_INSTANCE.createNewFileInstance("chosenMenu");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mapping.forEach((checkBox, textView) -> {
                if (checkBox.isChecked())
                    finalMapping.put((String) textView.getText(),true);
            });
        }

        StudentClientApplication.DATABASE_INSTANCE.writeSerializedData(finalMapping,"chosenMenu");

        binding = null;
    }
}