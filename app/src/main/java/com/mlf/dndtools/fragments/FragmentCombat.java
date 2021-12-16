package com.mlf.dndtools.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.recyclers.CombatListItemAdapter;
import com.mlf.dndtools.recyclers.ItemMoveCallback;
import com.mlf.dndutils.Monster;
import com.mlf.dndutils.types.EInitiative;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FragmentCombat extends Fragment
{
    private View viewFragment;
    private RecyclerView recyclerCounters;
    private Toolbar toolbar;
    private ImageButton butClear;
    private ImageButton butAdd;
    private CombatListItemAdapter adapter;
    private final DialogLauncher dialogLauncher;

    public FragmentCombat(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Adapters
        adapter = new CombatListItemAdapter(dialogLauncher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_combat, container, false);

        // Text y Buttons
        butAdd = viewFragment.findViewById(R.id.combatButAdd);
        butClear = viewFragment.findViewById(R.id.combatButClear);
        toolbar = viewFragment.findViewById(R.id.combatToolbar);

        // Recyclers
        recyclerCounters = viewFragment.findViewById(R.id.combatRecyclerFighters);
        recyclerCounters.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerCounters.setAdapter(adapter);

        // Para el manejo del swipe
        ItemTouchHelper.Callback callbackCounters = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelperCounter = new ItemTouchHelper(callbackCounters);
        touchHelperCounter.attachToRecyclerView(recyclerCounters);

        return viewFragment;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed(){}
        });

        butAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputMonsterResult(new DialogLauncher.OnInputMonsterResult()
                {
                    @Override
                    public void OnOk(Monster player, int count, EInitiative initiativeType)
                    {
                        adapter.addItems(player, count, initiativeType);
                    }
                });
                dialogLauncher.InputPlayer();
            }
        });

        butClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnOkCancelResult(new DialogLauncher.OnOkCancelResult()
                {
                    @Override
                    public void OnOk()
                    {
                        adapter.clear();
                    }
                });
                dialogLauncher.ShowOkCancelDialog(getString(R.string.dialog_remove_title), getString(R.string.dialog_remove_combat), DialogLauncher.ICON_QUESTION);
            }
        });
    }

    public CombatListItemAdapter getAdapter()
    {
        return adapter;
    }
}
