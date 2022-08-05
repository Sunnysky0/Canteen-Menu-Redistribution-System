package cn.sunnysky.ui.request_channel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cn.sunnysky.R;

public class ChannelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChannelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Initial value");
    }

    public MutableLiveData<String> getText() {
        return mText;
    }
}