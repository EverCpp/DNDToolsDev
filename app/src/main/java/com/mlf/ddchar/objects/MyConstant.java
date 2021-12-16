package com.mlf.ddchar.objects;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.mlf.ddchar.R;
import com.mlf.ddchar.dialogs.DialogLauncher;

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

    public static boolean checkConnection(Context context)
    {
        if(isConnectionAvailable(context))
        {
           return true;
        }
        DialogLauncher dialogLauncher = new DialogLauncher((AppCompatActivity) context);
        dialogLauncher.ShowOkDialog(R.string.err_no_connection_title, R.string.err_no_connection_msg, DialogLauncher.ICON_ERROR);
        return false;
    }

    public static boolean isConnectionAvailable(@NonNull Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNW = cm.getActiveNetworkInfo();
        MyLog.e("activeNW " + activeNW);
        return activeNW != null && activeNW.isConnected();
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
