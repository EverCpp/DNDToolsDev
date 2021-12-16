package com.mlf.ddchar.fragments;

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
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.ddchar.R;
import com.mlf.ddchar.dialogs.DialogLauncher;
import com.mlf.ddchar.objects.MyConstant;
import com.mlf.ddchar.objects.MyLog;
import com.mlf.ddchar.recyclers.CharAbilityItemAdapter;
import com.mlf.ddchar.recyclers.ItemMoveCallback;
import com.mlf.dndutils.Char;
import com.mlf.dndutils.types.EAlignment;
import com.mlf.dndutils.types.EClass;
import com.mlf.dndutils.types.ERace;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FragmentCharacter extends Fragment
{
    private static final String FILE_PLAYER = "character.dat";

    private View viewFragment;
    private RecyclerView recyclerAbilities;
    private CharAbilityItemAdapter adapter;
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
        character = new Char("Personaje");
        character.setFileName(MyConstant.WORKING_DIR + FILE_PLAYER);
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
        adapter = new CharAbilityItemAdapter(dialogLauncher, character);
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
        recyclerAbilities.setLayoutManager(new GridLayoutManager(requireActivity(), adapter.getItemCount()));
        recyclerAbilities.setAdapter(adapter);

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
                if(dialogLauncher != null)
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
            }
        });
    }

    private void loadArraysToSpinners()
    {
        for(ERace race : raceTypes)
        {
            raceStrings.add(raceToString(race));
        }
        for(EClass type : classTypes)
        {
            classStrings.add(classToString(type));
        }
        for(EAlignment type : alignTypes)
        {
            alignStrings.add(alignmentToString(type));
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

    private String classToString(EClass type)
    {
        switch(type)
        {
            case BARBARIAN:
                return getResources().getString(R.string.class_barbarian);
            case BARD:
                return getResources().getString(R.string.class_bard);
            case CLERIC:
                return getResources().getString(R.string.class_cleric);
            case DRUID:
                return getResources().getString(R.string.class_druid);
            case FIGHTER:
                return getResources().getString(R.string.class_fighter);
            case MONK:
                return getResources().getString(R.string.class_monk);
            case PALADIN:
                return getResources().getString(R.string.class_paladin);
            case RANGER:
                return getResources().getString(R.string.class_ranger);
            case ROUGE:
                return getResources().getString(R.string.class_rouge);
            case SORCERER:
                return getResources().getString(R.string.class_sorcerer);
            case WARLOCK:
                return getResources().getString(R.string.class_warlock);
            case WIZARD:
                return getResources().getString(R.string.class_wizard);
        }
        return type.name();
    }

    private String raceToString(ERace type)
    {
        switch(type)
        {
            case DRAGONBORN:
                return getResources().getString(R.string.race_dragonborn);
            case DWARF:
                return getResources().getString(R.string.race_dwarf);
            case ELF:
                return getResources().getString(R.string.race_elf);
            case GNOME:
                return getResources().getString(R.string.race_gnome);
            case HALFELF:
                return getResources().getString(R.string.race_halfelf);
            case HALFLING:
                return getResources().getString(R.string.race_halfling);
            case HALFORC:
                return getResources().getString(R.string.race_halforc);
            case HUMAN:
                return getResources().getString(R.string.race_human);
            case TIEFLING:
                return getResources().getString(R.string.race_tiefling);
        }
        return type.name();
    }

    private String alignmentToString(EAlignment type)
    {
        switch(type)
        {
            case UNALIGNED:
                return getResources().getString(R.string.align_unaligned);
            case LAWFUL_GOOD:
                return getResources().getString(R.string.align_lawful_good);
            case NEUTRAL_GOOD:
                return getResources().getString(R.string.align_neutral_good);
            case CHAOTIC_GOOD:
                return getResources().getString(R.string.align_chaotic_good);
            case LAWFUL_NEUTRAL:
                return getResources().getString(R.string.align_lawful_neutral);
            case TRUE_NEUTRAL:
                return getResources().getString(R.string.align_true_neutral);
            case CHAOTIC_NEUTRAL:
                return getResources().getString(R.string.align_chaotic_neutral);
            case LAWFUL_EVIL:
                return getResources().getString(R.string.align_lawful_evil);
            case NEUTRAL_EVIL:
                return getResources().getString(R.string.align_neutral_evil);
            case CHAOTIC_EVIL:
                return getResources().getString(R.string.align_chaotic_evil);
        }
        return type.name();
    }
}
