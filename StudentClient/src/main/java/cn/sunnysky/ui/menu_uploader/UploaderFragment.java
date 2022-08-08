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
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

public class UploaderFragment extends Fragment {

    private UploaderViewModel uploaderViewModel;
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

        tableLayout = binding.TABLE;

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
        category.setText(v);
        buf[1] = category;


        TableRow tableRow = new TableRow(getActivity());

        for (TextView t : buf){
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextSize(22);
            tableRow.addView(t);
        }

        tableRow.addView(new CheckBox(getContext()));

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}