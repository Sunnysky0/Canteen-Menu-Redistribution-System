package cn.sunnysky.ui.feedback_channel;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.activities.MainActivity;
import cn.sunnysky.api.LogType;
import cn.sunnysky.databinding.FeedbackChannelFragmentBinding;
import cn.sunnysky.dialogs.CommonNotification;
import cn.sunnysky.dialogs.OperationProgressAnimator;
import cn.sunnysky.util.DateUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.*;
import java.util.function.Consumer;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static cn.sunnysky.IntegratedManager.logger;

public class FeedbackChannel extends Fragment {

    private FeedbackChannelViewModel mViewModel;
    private FeedbackChannelFragmentBinding binding;
    private Map<RatingBar, TextView> mapping;
    private @org.jetbrains.annotations.Nullable Map<String, String> loadedMapping;
    private TableLayout tableLayout;
    private Set<String> recommendedMenu = new HashSet<>();
    private volatile OperationProgressAnimator animator;

    public static FeedbackChannel newInstance() {
        return new FeedbackChannel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).hideFab();

        binding = FeedbackChannelFragmentBinding.inflate(inflater, container, false);

        mapping = new HashMap<RatingBar, TextView>();
        loadedMapping = StudentClientApplication.DATABASE_INSTANCE.readSerializedDataFromFile("scoredFood");

        View root = binding.getRoot();

        tableLayout = binding.TABLE;

        binding.sendReq.setOnClickListener(this::onClickRequest);

        loadPublishedData();

        return root;
    }

    private void syncMenu(){

        String rsp = StudentClientApplication.internalNetworkHandler.getRecommendedMenu();

        if ( rsp.startsWith("ERR")){
            logger.log("Network failure", LogType.ERROR);
            logger.log(rsp,LogType.ERROR);

            animator.dismiss();

            final DialogFragment dialogFragment = new CommonNotification(getString(R.string.menu_not_published));
            dialogFragment.show(getParentFragmentManager(),"");
        }
        else {
            recommendedMenu.clear();

            recommendedMenu.addAll(Arrays.asList(rsp.split(",")));

            if (animator != null)
                animator.dismiss();

            Runnable renderThread = new Runnable() {
                @Override
                public void run() {
                    recommendedMenu.forEach(render);
                }
            };


            new Handler(Looper.getMainLooper()).post(renderThread);

        }

    }


    public Consumer<String> render = new Consumer() {
        @Override
        public void accept(Object v) {
            TextView[] buf = new TextView[1];

            TextView foodName = new TextView(getContext());
            foodName.setText((CharSequence) v);
            buf[0] = foodName;


            TableRow tableRow = new TableRow(getActivity());

            for (TextView t : buf) {
                t.setGravity(CENTER_HORIZONTAL);
                t.setTextSize(22);
                tableRow.addView(t);
            }

            final RatingBar ratingBar = new RatingBar(getContext());
            ratingBar.setNumStars(4);
            ratingBar.setRating(0);
            ratingBar.setStepSize(0.5f);
            tableRow.addView(ratingBar);

            String s;

            if (loadedMapping != null && !loadedMapping.keySet().isEmpty())
                if ((s = loadedMapping.get(v)) != null)
                    ratingBar.setRating(Float.parseFloat(s));

            if (mapping != null)
                mapping.put(ratingBar,foodName);


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

    private void onClickRequest(View view){
        String [] ratings = new String[mapping.size()];
        final Iterator<RatingBar> iterator = mapping.keySet().iterator();
        for (int i = 0; i < ratings.length; i++) {
            final RatingBar next = iterator.next();
            ratings[i]
             = mapping.get(next).getText() + "-" + next.getRating();
        }


        StudentClientApplication.join(
                () ->{
                    final String s = StudentClientApplication.internalNetworkHandler.sendRating(ratings);

                    if (s.startsWith("ERR"))
                        Snackbar.make(view,R.string.network_failure,Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(view,R.string.upload_success,Snackbar.LENGTH_LONG).show();

                }
        );

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedbackChannelViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Map<String, Float> finalMapping = new HashMap<>();

        StudentClientApplication.DATABASE_INSTANCE.createNewFileInstance("scoredFood");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mapping.forEach((ratingBar, textView) -> {
                    finalMapping.put((String) textView.getText(),ratingBar.getRating());
            });
        }

        StudentClientApplication.DATABASE_INSTANCE.writeSerializedData(finalMapping,"scoredFood");

        binding = null;
    }

}