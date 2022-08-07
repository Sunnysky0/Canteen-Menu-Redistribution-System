package cn.sunnysky.activities;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import cn.sunnysky.R;
import cn.sunnysky.StudentClientApplication;
import cn.sunnysky.dialogs.OperationProgressNotification;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import cn.sunnysky.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final boolean[] flag = {true};

                Runnable r = () -> {
                    try {
                        StudentClientApplication.initializeNetwork();
                    } catch (NetworkErrorException e) {
                        e.printStackTrace();
                        flag[0] = false;
                    }
                };

                StudentClientApplication.join(r);

                if(!flag[0] || StudentClientApplication.internalNetworkHandler == null)
                    Snackbar.make(view, R.string.cannot_connect, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                else{
                    Snackbar.make(view, R.string.connection_established, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Snackbar.make(view, R.string.file_transferring, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    OperationProgressNotification notification = new OperationProgressNotification(getContext(),R.string.synchornizing);
                    // notification.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    notification.show();

                    r = () -> {
                        boolean b = false;
                        try {
                            b = StudentClientApplication.internalNetworkHandler.transferRemoteFile(
                                    ".//food_data_s1.fson", getFilesDir().getPath() + "/download/" + "food_data_s1.fson");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        notification.dismiss();

                        if (b)
                            Snackbar.make(view, R.string.file_transferred, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        else Snackbar.make(view, R.string.ftp_failure, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    };

                    StudentClientApplication.join(r);
                    // new Thread(r).start();
                }

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}