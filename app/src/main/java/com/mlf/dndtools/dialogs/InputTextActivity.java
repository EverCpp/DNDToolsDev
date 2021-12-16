package com.mlf.dndtools.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mlf.dndtools.R;
import com.mlf.dndtools.objects.MyConstant;
import com.mlf.dndutils.common.Counter;

import java.util.Locale;

public class InputTextActivity extends AppCompatActivity
{
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_UNSIGNED_NUM = 1;
    public static final int TYPE_SIGNED_NUM = 2;

    public static final String DEF_DESC = "Texto";
    public static final String DEF_TEXT = "";
    public static final int DEF_NUM = 0;
    public static final int DEF_TYPE = 0;
    public static final int DEF_MIN = Counter.DEF_MIN;
    public static final int DEF_MAX = Counter.DEF_MAX;

    public static final String TAG_DESC = "desc";
    public static final String TAG_TEXT = "text";
    public static final String TAG_NUM = "num";
    public static final String TAG_TYPE = "type";
    public static final String TAG_MIN = "min";
    public static final String TAG_MAX = "max";

    private Button butOk, butCancel;
    private EditText editText;
    private TextView textDesc;

    private int type, min, max;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_text);
        setFinishOnTouchOutside(false);

        // Ancho al 80% de la pantalla
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int)(params.width*0.8);
        this.getWindow().setAttributes(params);

        textDesc = findViewById(R.id.inputTextTextDesc);
        editText = findViewById(R.id.inputTextEditText);
        butOk = findViewById(R.id.inputTextButOk);
        butCancel = findViewById(R.id.inputTextButCancel);

        // Get intent
        String description, text;
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            description = extras.getString(TAG_DESC, DEF_DESC);
            type = extras.getInt(TAG_TYPE, DEF_TYPE);
            switch(type)
            {
                case TYPE_SIGNED_NUM:
                case TYPE_UNSIGNED_NUM:
                    text = Integer.toString(extras.getInt(TAG_NUM, DEF_NUM));
                    break;
                case TYPE_TEXT:
                default:
                    text = extras.getString(TAG_TEXT, DEF_TEXT);
                    type = TYPE_TEXT;
                    break;
            }
            min = extras.getInt(TAG_MIN, DEF_MIN);
            max = extras.getInt(TAG_MAX, DEF_MAX);
        }
        else
        {
            description = DEF_DESC;
            text = DEF_TEXT;
            type = TYPE_TEXT;
            min = DEF_MIN;
            max = DEF_MAX;
        }
        textDesc.setText(description);
        switch(type)
        {
            case TYPE_SIGNED_NUM:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                break;
            case TYPE_UNSIGNED_NUM:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                break;
            case TYPE_TEXT:
            default:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                break;
        }
        editText.setText(text);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
        {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        editText.requestFocus();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Botón Aceptar
        butOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MyConstant.InType inType;
                switch(type)
                {
                    case TYPE_SIGNED_NUM:
                        inType = MyConstant.InType.NUM_SIGNED_NUM;
                        break;
                    case TYPE_UNSIGNED_NUM:
                        inType = MyConstant.InType.NUM_UNSIGNED_NUM;
                        break;
                    case TYPE_TEXT:
                    default:
                        inType = MyConstant.InType.TEXT;
                        break;
                }
                if(!MyConstant.isValidAndNotEmpty(editText, inType)){ return; }

                    Intent intent = new Intent();
                String text = editText.getText().toString().trim();
                if((type == TYPE_SIGNED_NUM) || (type == TYPE_UNSIGNED_NUM))
                {
                    int value = Integer.parseInt(text);
                    if((value < min) || (value > max))
                    {
                        editText.setError(String.format(Locale.US, getResources().getString(R.string.check_err_range), min, max));
                        return;
                    }
                    intent.putExtra(TAG_NUM, value);
                }
                else
                {
                    intent.putExtra(TAG_TEXT, text);
                }
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