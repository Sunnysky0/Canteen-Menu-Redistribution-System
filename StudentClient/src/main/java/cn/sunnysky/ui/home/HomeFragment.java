package cn.sunnysky.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import cn.sunnysky.R;
import cn.sunnysky.activities.MainActivity;
import cn.sunnysky.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        ((MainActivity) getActivity()).showFab();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        homeViewModel.getText().setValue(getString(R.string.count_down));

        Calendar c = Calendar.getInstance();

        int daysLeft;
        switch (c.get(Calendar.DAY_OF_WEEK)){
            case 1:
                daysLeft = 6;
                break;
            case 2:
                daysLeft = 5;
                break;
            case 3:
                daysLeft = 4;
                break;
            case 4:
                daysLeft = 3;
                break;
            case 5:
                daysLeft = 2;
                break;
            case 6:
                daysLeft = 1;
                break;
            default:
                daysLeft = 0;
        }

        binding.textCtd.setText(daysLeft + "天");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}