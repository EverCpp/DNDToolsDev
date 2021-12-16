package com.mlf.ddchar.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mlf.ddchar.R;
import com.mlf.ddchar.objects.MyConstant;
import com.mlf.dndutils.CombatManager;
import com.mlf.dndutils.types.EInitiative;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unused")
public class InputMonsterPlayerActivity extends AppCompatActivity
{
    public static final String TAG_NAME = "name";
    public static final String TAG_INITIATIVE_TYPE = "initiative_type";
    public static final String TAG_BONUS_INITIATIVE = "bonus_initiative";
    public static final String TAG_HITPOINTS = "hitPoints";
    public static final String TAG_COUNT = "count";

    private Button butOk, butCancel;
    private TextView textNamePref, textBonusValue;
    private EditText editNamePref, editCount, editBonusValue, editHitPoints;
    private Spinner spinnerInitType;
    private ArrayList<String> initypeStrings;
    private ArrayList<EInitiative> initypes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_monster_player);
        setFinishOnTouchOutside(false);

        // Ancho al 80% de la pantalla
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int)(params.width*0.8);
        this.getWindow().setAttributes(params);

        textNamePref = findViewById(R.id.inputMonsterTextNamePref);
        textBonusValue = findViewById(R.id.inputMonsterTextBonusValue);
        editNamePref = findViewById(R.id.inputMonsterEditName);
        editCount = findViewById(R.id.inputMonsterEditCount);
        spinnerInitType = findViewById(R.id.inputMonsterSpinnerInitType);
        editBonusValue = findViewById(R.id.inputMonsterEditBonusValue);
        editHitPoints = findViewById(R.id.inputMonsterEditHitPoints);

        butOk = findViewById(R.id.inputMonsterButOk);
        butCancel = findViewById(R.id.inputMonsterButCancel);

        // Spinner
        loadSpinners();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
        {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        editNamePref.requestFocus();
    }

    private void loadSpinners()
    {
        initypeStrings = new ArrayList<>();
        initypeStrings.add(getString(R.string.input_monster_init_manual));
        initypeStrings.add(getString(R.string.input_monster_init_auto));
        initypeStrings.add(getString(R.string.input_monster_init_none));

        initypes = EInitiative.getAll();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, initypeStrings);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerInitType.setAdapter(adapter);
        spinnerInitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(initypes.get(i) == EInitiative.MANUAL)
                {
                    textBonusValue.setText(getString(R.string.input_monster_init_text_value));
                }
                else
                {
                    textBonusValue.setText(getString(R.string.input_monster_init_text_bonus));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        spinnerInitType.setSelection(0);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Cambio de Nombre/Prefijo según cambia el valor de la cantidad
        editCount.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}
            @Override
            public void afterTextChanged(Editable s)
            {
                if((s != null) && !s.toString().isEmpty())
                {
                    int value = Integer.parseInt(s.toString());
                    if(value > 1)
                    {
                        textNamePref.setText(getString(R.string.input_monster_prefix));
                    }
                    else
                    {
                        textNamePref.setText(getString(R.string.input_monster_name));
                    }
                }
            }
        });
        // Botón Aceptar
        butOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Nombre / Prefijo
                if(!MyConstant.isValidAndNotEmpty(editNamePref, MyConstant.InType.TEXT)){ return; }
                String name = editNamePref.getText().toString().trim();

                // Cantidad
                if(!MyConstant.isValidAndNotEmpty(editCount, MyConstant.InType.NUM_UNSIGNED_NUM)){ return; }
                int count = Integer.parseInt(editCount.getText().toString().trim());
                if((count < 1) || (count > CombatManager.MAX_AT_ONCE))
                {
                    editCount.setError(String.format(Locale.US, getResources().getString(R.string.check_err_range), 1, CombatManager.MAX_AT_ONCE));
                }

                // Iniciativa
                if(!MyConstant.isValidAndNotEmpty(editBonusValue, MyConstant.InType.NUM_UNSIGNED_NUM)){ return; }
                int bonus_value = Integer.parseInt(editBonusValue.getText().toString().trim());

                // Puntos de golpe
                if(!MyConstant.isValidAndNotEmpty(editHitPoints, MyConstant.InType.NUM_UNSIGNED_NUM)){ return; }
                int hitPoints = Integer.parseInt(editHitPoints.getText().toString().trim());

                // Intent
                Intent intent = new Intent();
                intent.putExtra(TAG_NAME, name);
                intent.putExtra(TAG_COUNT, count);
                intent.putExtra(TAG_BONUS_INITIATIVE, bonus_value);
                intent.putExtra(TAG_INITIATIVE_TYPE, initypes.get(spinnerInitType.getSelectedItemPosition()).name());
                intent.putExtra(TAG_HITPOINTS, hitPoints);
                setResult(AppCompatActivity.RESULT_OK, intent);
                closeKeyboard();
                finish();
            }
        });

        // Botón Cancelar
        butCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(AppCompatActivity.RESULT_CANCELED);
                closeKeyboard();
                finish();
            }
        });
    }

    private void closeKeyboard()
    {
        // this will give us the view which is currently focus in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently focus then this will protect the app from crash
        if (view != null) {

            // now assign the system service to InputMethodManager
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}