package com.mlf.ddchar.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.enofir.utils.EnoKeyb;
import com.enofir.utils.EnoKeybCash;
import com.mlf.ddchar.R;
import com.mlf.ddchar.objects.MyLog;

public class InputNumberActivity extends AppCompatActivity
{
    private static final String DEF_DECRIPTION = "Número";

    public static final String TAG_MAX_INT_DIGIT = "maxIntDigits";
    public static final String TAG_MAX_DEC_DIGIT = "maxDecDigits";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_INITIAL_VAL = "initialVal";
    public static final String TAG_RESULT = "result";

    private final Button[] buttons = new Button[10];

    private EnoKeybCash enoKeyb;
    private Button buttonDel, button00;
    private Button buttonInputNumberOk, buttonInputNumberCancel;
    private TextView textViewNumber, textViewInputNumberDesc;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_number);

        setFinishOnTouchOutside(false);

        // Load views
        textViewInputNumberDesc = findViewById(R.id.textViewInputNumberDesc);
        textViewNumber = findViewById(R.id.textViewNumber);
        buttons[0] = findViewById(R.id.buttonInputNum0);
        buttons[1] = findViewById(R.id.buttonInputNum1);
        buttons[2] = findViewById(R.id.buttonInputNum2);
        buttons[3] = findViewById(R.id.buttonInputNum3);
        buttons[4] = findViewById(R.id.buttonInputNum4);
        buttons[5] = findViewById(R.id.buttonInputNum5);
        buttons[6] = findViewById(R.id.buttonInputNum6);
        buttons[7] = findViewById(R.id.buttonInputNum7);
        buttons[8] = findViewById(R.id.buttonInputNum8);
        buttons[9] = findViewById(R.id.buttonInputNum9);
        button00 = findViewById(R.id.buttonInputNum00);
        buttonDel = findViewById(R.id.buttonInputNumDel);
        buttonInputNumberOk = findViewById(R.id.buttonInputNumberOk);
        buttonInputNumberCancel = findViewById(R.id.buttonInputNumberCancel);

        // Get intent
        Bundle extras = getIntent().getExtras();
        int maxIntDigits, maxDecDigits;
        double initVal;
        if(extras != null)
        {
            description = extras.getString(TAG_DESCRIPTION, DEF_DECRIPTION);
            maxIntDigits = extras.getInt(TAG_MAX_INT_DIGIT, EnoKeyb.DEF_MAX_INT_DIGITS);
            maxDecDigits = extras.getInt(TAG_MAX_DEC_DIGIT, EnoKeyb.DEF_MAX_DEC_DIGITS);
            initVal = extras.getDouble(TAG_INITIAL_VAL, EnoKeyb.DEF_INITIAL_VAL);
        }
        else
        {
            description = DEF_DECRIPTION;
            maxIntDigits = EnoKeyb.DEF_MAX_INT_DIGITS;
            maxDecDigits = EnoKeyb.DEF_MAX_DEC_DIGITS;
            initVal = EnoKeyb.DEF_INITIAL_VAL;
        }
        setTitle(description);
        enoKeyb = new EnoKeybCash(initVal, maxIntDigits, maxDecDigits);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Descripción y valor en el textView
        textViewInputNumberDesc.setText(description);
        displayNumber(enoKeyb.getCurrentText());

        // Botones numéricos
        for(int i = 0; i < 10; ++i)
        {
            buttons[i].setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    enoKeyb.processDigit(Integer.parseInt(((Button) v).getText().toString()));
                    displayNumber(enoKeyb.getCurrentText());
                }
            });
        }

        // Punto decimal
        button00.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                enoKeyb.processDigit(0);
                enoKeyb.processDigit(0);
                displayNumber(enoKeyb.getCurrentText());
            }
        });

        // Botón de borrar
        buttonDel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displayNumber(enoKeyb.processDel());
            }
        });

        // Botón Aceptar
        buttonInputNumberOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String strValue = enoKeyb.getCurrentText().trim();
                if(strValue.isEmpty())
                {
                    strValue = "0";
                }
                int value = (int) Double.parseDouble(strValue);
                    Intent intent = new Intent();
                intent.putExtra(TAG_RESULT, value);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();
            }
        });

        // Botón Cancelar
        buttonInputNumberCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void displayNumber(String text)
    {
        textViewNumber.setText(text);
    }
}