package com.mlf.dndtools.objects;

import android.content.Context;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.mlf.dndtools.R;

@SuppressWarnings("unused")
public class MyConstant
{
    public static boolean DEBUG = true;         // @MLF Poner en false para la versión final
    public static String WORKING_DIR;           // Directorio de trabajo

    public enum InType
    {
        NUM_UNSIGNED_NUM(R.string.check_err_num_int),
        NUM_SIGNED_NUM(R.string.check_err_num_signed),
        ALNUM(R.string.check_err_alnum),
        TEXT(R.string.check_err_text),
        EMAIL(R.string.check_err_email),
        IP(R.string.check_err_ip);

        int errorMsg;
        InType(int errorMsg)
        {
            this.errorMsg = errorMsg;
        }
        public int getErrorMsg()
        {
            return errorMsg;
        }
    }

    public static int dp2Px(@NonNull final Context context, final float dp)
    {
        return Math.round(dp*context.getResources().getDisplayMetrics().density);
    }

    public static int px2dp(@NonNull final Context context, final float px)
    {
        return Math.round(px/context.getResources().getDisplayMetrics().density);
    }

    public static boolean isValidAndNotEmpty(EditText edtext, InType type)
    {
        return isValid(edtext, type, true);
    }

    public static boolean isValid(@NonNull EditText edtext, InType type, boolean checkEmpty)
    {
        String text = edtext.getText().toString().trim();
        if(checkEmpty && text.isEmpty())
        {
            edtext.requestFocus();
            edtext.setError(edtext.getContext().getResources().getString(R.string.check_err_empty));
            return false;
        }
        if(isValid(text, type))
        {
            return true;
        }
        edtext.requestFocus();
        edtext.setError(edtext.getContext().getString(type.getErrorMsg()));
        return false;
    }

    public static boolean isValid(String text, InType type)
    {
        if(type == InType.TEXT)
        {
            return true;
        }
        switch(type)
        {
            case NUM_UNSIGNED_NUM:
                try
                {
                    int res = Integer.parseInt(text);
                    return (res >= 0);
                }
                catch(Exception e)
                {
                    return false;
                }
            case NUM_SIGNED_NUM:
                try
                {
                    Integer.parseInt(text);
                    return true;
                }
                catch(Exception e)
                {
                    return false;
                }
            case ALNUM:
                return text.matches("[a-zA-Z0-9]+");
            case EMAIL:
                return text.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
            case IP:
                return text.matches("[0-9]+\\.+[0-9]+\\.[0-9]+\\.[0-9]+");
        }
        return false;
    }
}
