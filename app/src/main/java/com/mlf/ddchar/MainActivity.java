package com.mlf.ddchar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mlf.ddchar.dialogs.DialogLauncher;
import com.mlf.ddchar.objects.MyConstant;
import com.mlf.ddchar.objects.MyLog;
import com.mlf.ddchar.recyclers.MainToolsAdapter;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
{
    private static final int TOAST_DURATION = 3000;
    private static final int TOAST_TICK = 1000;

    // Toast
    private final Object sincToast = new Object();
    private boolean toastShowed;
    private Toast toast;
    private CountDownTimer toastCountDown;

    private MainToolsAdapter mainToolsAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private DialogLauncher dialogLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Directorio de la aplicación
        MyConstant.WORKING_DIR = getFilesDir().getAbsolutePath();
        if(!MyConstant.WORKING_DIR.endsWith("/") && !MyConstant.WORKING_DIR.endsWith("\\"))
        {
            MyConstant.WORKING_DIR += "/";
        }
        MyLog.e("WORKING_DIR:" + MyConstant.WORKING_DIR);

        toastShowed = false;

        // Dialog launcher
        dialogLauncher = new DialogLauncher(this);
        mainToolsAdapter = new MainToolsAdapter(getSupportFragmentManager(), getLifecycle(), this, dialogLauncher);
        mainToolsAdapter.setDialogLauncher(dialogLauncher);

        viewPager = findViewById(R.id.mainViewPager);
        viewPager.setAdapter(mainToolsAdapter);
        viewPager.setUserInputEnabled(false);   // Para evitar el scroll horizontal

        tabLayout = findViewById(R.id.mainTabLayout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy()
        {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
            {
                tab.setText(mainToolsAdapter.getItemTitle(position));
            }
        });
        tabLayoutMediator.attach();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        synchronized(sincToast)
        {
            if(toastShowed)
            {
                toast.cancel();
                toastCountDown.cancel();
                finish();
            }
            else
            {
                toast = Toast.makeText(this, getResources().getString(R.string.toast_exit), Toast.LENGTH_SHORT);
                toastShowed = true;
                toast.show();

                toastCountDown = new CountDownTimer(TOAST_DURATION, TOAST_TICK)
                {
                    public void onTick(long millisUntilFinished){}
                    public void onFinish()
                    {
                        synchronized(sincToast)
                        {
                            toastShowed = false;
                            toast.cancel();
                        }
                    }
                };
                toastCountDown.start();
            }
        }
    }
}
