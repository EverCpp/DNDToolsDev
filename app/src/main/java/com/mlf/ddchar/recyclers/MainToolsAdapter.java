package com.mlf.ddchar.recyclers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.mlf.ddchar.R;
import com.mlf.ddchar.dialogs.DialogLauncher;
import com.mlf.ddchar.fragments.FragmentBasics;
import com.mlf.ddchar.fragments.FragmentCharacter;
import com.mlf.ddchar.fragments.FragmentCombat;

public class MainToolsAdapter extends FragmentStateAdapter
{
    private static final int FRAGMENT_COUNT = 3;

    private DialogLauncher dialogLauncher;
    private final String[] titles = new String[FRAGMENT_COUNT];
    private Fragment[] fragment = new Fragment[FRAGMENT_COUNT];

    public MainToolsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, DialogLauncher dialogLauncher)
    {
        super(fragmentManager, lifecycle);
        this.dialogLauncher = dialogLauncher;

        titles[0] = context.getResources().getString(R.string.tab_basics);
        titles[1] = context.getResources().getString(R.string.tab_combat);
        titles[2] = context.getResources().getString(R.string.tab_character);

        fragment[0] = new FragmentBasics(this.dialogLauncher);
        fragment[1] = new FragmentCombat(this.dialogLauncher);
        fragment[2] = new FragmentCharacter(this.dialogLauncher);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragment[position];
        /*switch(position)
        {
            case 0:
                return new FragmentBasics(dialogLauncher);
            case 1:
                return new FragmentCombat(dialogLauncher);
            default:
            case 2:
                return new FragmentCharacter(dialogLauncher);
        }*/
    }

    @Override
    public int getItemCount()
    {
        return FRAGMENT_COUNT;
    }

    public String getItemTitle(int position)
    {
        return titles[position];
    }

    public void setDialogLauncher(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
    }
}
