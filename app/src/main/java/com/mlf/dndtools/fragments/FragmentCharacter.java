package com.mlf.dndtools.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.recyclers.CharSavingItemAdapter;
import com.mlf.dndtools.utils.Constant;
import com.mlf.dndtools.utils.MyLog;
import com.mlf.dndtools.recyclers.CharAbilityItemAdapter;
import com.mlf.dndtools.recyclers.CharSkillItemAdapter;
import com.mlf.dndutils.Char;
import com.mlf.dndutils.enums.EAlignment;
import com.mlf.dndutils.enums.EClass;
import com.mlf.dndutils.enums.ERace;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FragmentCharacter extends Fragment
{
    private static final String FILE_PLAYER = "character.dat";

    private View viewFragment;
    private RecyclerView recyclerAbilities;
    private RecyclerView recyclerSkills;
    private RecyclerView recyclerSavings;
    private CharAbilityItemAdapter adapterAbilities;
    private CharSkillItemAdapter adapterSkills;
    private CharSavingItemAdapter adapterSavings;
    private final DialogLauncher dialogLauncher;
    // Controles
    private Toolbar toolbar;
    private ImageButton butBag;
    private TextView textName;
    private Spinner spinnerRace, spinnerClass;
    private TextView textBackground;
    private Spinner spinnerAlign;
    private TextView textLevel;
    // Personaje
    private final Char character;
    // Razas
    private final ArrayList<ERace> raceTypes;
    private final ArrayList<String> raceStrings;
    // Clases
    private final ArrayList<EClass> classTypes;
    private final ArrayList<String> classStrings;
    // Alignments
    private final ArrayList<EAlignment> alignTypes;
    private final ArrayList<String> alignStrings;

    public FragmentCharacter(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
        // Personaje
        character = new Char();
        character.setFileName(Constant.WORKING_DIR + FILE_PLAYER);
        MyLog.d("load: " + character.toJSON());

        // Razas
        raceTypes = ERace.getAll();
        raceStrings = new ArrayList<>();
        // Clases
        classTypes = EClass.getAll();
        classStrings = new ArrayList<>();
        // Alignments
        alignTypes = EAlignment.getAll();
        alignStrings = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Adapters
        adapterAbilities = new CharAbilityItemAdapter(dialogLauncher, character);
        adapterSkills = new CharSkillItemAdapter(dialogLauncher, character);
        adapterSavings = new CharSavingItemAdapter(dialogLauncher, character);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_character , container, false);

        // Controles
        toolbar = viewFragment.findViewById(R.id.charToolbar);
        butBag = viewFragment.findViewById(R.id.charButBag);
        textName = viewFragment.findViewById(R.id.charTextName);
        spinnerRace = viewFragment.findViewById(R.id.charSpinnerRace);
        spinnerClass = viewFragment.findViewById(R.id.charSpinnerClass);
        spinnerAlign = viewFragment.findViewById(R.id.charSpinnerAlignment);
        textBackground = viewFragment.findViewById(R.id.charTextBackground);
        textLevel = viewFragment.findViewById(R.id.charTextLevel);

        loadArraysToSpinners();
        // Razas
        setupSpinnerRaces();
        // Clases
        setupSpinnerClasses();
        // Alineamientos
        setupSpinnerAlignments();
        // Recyclers
        recyclerAbilities = viewFragment.findViewById(R.id.charRecyclerAbilities);
        recyclerAbilities.setLayoutManager(new GridLayoutManager(requireActivity(), adapterAbilities.getItemCount()));
        recyclerAbilities.setAdapter(adapterAbilities);

        recyclerSkills = viewFragment.findViewById(R.id.charRecyclerSkills);
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), adapterSkills.getItemCount());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerSkills.setLayoutManager(layoutManager);
        recyclerSkills.setAdapter(adapterSkills);

        recyclerSavings = viewFragment.findViewById(R.id.charRecyclerSavings);
        recyclerSavings.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerSavings.setAdapter(adapterSavings);

        /*viewFragment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                viewFragment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/

        // Valores por defecto
        textName.setText(character.getName());
        textBackground.setText(character.getBackground());
        textLevel.setText(String.format(Locale.US, "%d", character.getLevelValue()));

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

        butBag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //spinnerRace.performClick();
            }
        });
        textName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        if((text != null) && !text.isEmpty())
                        {
                            character.setName(text);
                            textName.setText(character.getName());
                        }
                    }
                });
                dialogLauncher.InputText(getResources().getString(R.string.char_name), character.getName());
            }
        });
        textBackground.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        if((text != null) && !text.isEmpty())
                        {
                            character.setBackground(text);
                            textBackground.setText(character.getBackground());
                        }
                    }
                });
                dialogLauncher.InputText(getResources().getString(R.string.char_background), character.getBackground());
            }
        });
        textLevel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                {
                    @Override
                    public void OnOk(int number)
                    {
                        character.setLevel(number);
                        textLevel.setText(String.format(Locale.US, "%d", character.getLevelValue()));
                    }
                });
                dialogLauncher.InputNumber(getResources().getString(R.string.char_level), character.getLevel());
            }
        });
    }

    private void loadArraysToSpinners()
    {
        for(ERace race : raceTypes)
        {
            raceStrings.add(Constant.lang.getName(race));
        }
        for(EClass type : classTypes)
        {
            classStrings.add(Constant.lang.getName(type));
        }
        for(EAlignment type : alignTypes)
        {
            alignStrings.add(Constant.lang.getName(type));
        }
        for(String aling : alignStrings)
        {
            MyLog.e(aling);
        }
    }

    private void setupSpinnerRaces()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.item_spinner, raceStrings);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerRace.setAdapter(adapter);
        spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                character.setRaceType(raceTypes.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        spinnerRace.setSelection(raceTypes.indexOf(character.getRaceType()));
    }

    private void setupSpinnerClasses()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.item_spinner, classStrings);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerClass.setAdapter(adapter);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                character.setClassType(classTypes.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        spinnerClass.setSelection(classTypes.indexOf(character.getClassType()));
    }

    private void setupSpinnerAlignments()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.item_spinner, alignStrings);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerAlign.setAdapter(adapter);
        spinnerAlign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                character.setAlignment(alignTypes.get(i));
                MyLog.e("Setted " + character.getAlignment().name());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        spinnerAlign.setSelection(alignTypes.indexOf(character.getAlignment()));
    }
}
