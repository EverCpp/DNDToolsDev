package com.mlf.dndtools.dialogs;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.mlf.dndtools.R;
import com.mlf.dndutils.Monster;
import com.mlf.dndutils.common.Counter;
import com.mlf.dndutils.types.EInitiative;

@SuppressWarnings("unused")
public class DialogLauncher
{
    public static final int ICON_INFO = R.drawable.ic_dialog_info;
    public static final int ICON_QUESTION = R.drawable.ic_dialog_question;
    public static final int ICON_ERROR = R.drawable.ic_dialog_error;

    private final AppCompatActivity activity;

    private ActivityResultLauncher<Intent> startInputText;      // Launcher de input de text
    private ActivityResultLauncher<Intent> startInputNumber;    // Launcher de input de número
    private ActivityResultLauncher<Intent> startInputMonster;   // Launcher de input de monstruo

    private OnInputTextResult onInputTextResult;
    private OnInputNumberResult onInputNumberResult;
    private OnOkCancelResult onOkCancelResult;
    private OnInputMonsterResult onInputMonsterResult;

    public DialogLauncher(AppCompatActivity parent)
    {
        activity = parent;
        dialogsInit();
    }

    public AppCompatActivity getContext()
    {
        return activity;
    }

    public void setOnInputTextResult(OnInputTextResult onInputTextResult)
    {
        this.onInputTextResult = onInputTextResult;
    }

    public void setOnInputNumberResult(OnInputNumberResult onInputNumberResult)
    {
        this.onInputNumberResult = onInputNumberResult;
    }

    public void setOnOkCancelResult(OnOkCancelResult onOkCancelResult)
    {
        this.onOkCancelResult = onOkCancelResult;
    }

    public void setOnInputMonsterResult(OnInputMonsterResult onInputMonsterResult)
    {
        this.onInputMonsterResult = onInputMonsterResult;
    }

    private void dialogsInit()
    {
        startInputMonster = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    Intent intent = result.getData();
                    if(intent != null)
                    {
                        String name = intent.getStringExtra(InputMonsterPlayerActivity.TAG_NAME);
                        int bonus_initiative = intent.getIntExtra(InputMonsterPlayerActivity.TAG_BONUS_INITIATIVE, 0);
                        int hitPoints =  intent.getIntExtra(InputMonsterPlayerActivity.TAG_HITPOINTS, 20);
                        int count = intent.getIntExtra(InputMonsterPlayerActivity.TAG_COUNT, 1);
                        EInitiative initiativeType = EInitiative.fromName(intent.getStringExtra(InputMonsterPlayerActivity.TAG_INITIATIVE_TYPE));

                        Monster monster = new Monster(name).setHitPoints(hitPoints);
                        if(initiativeType == EInitiative.MANUAL)
                        {
                            monster.setInitiative1(bonus_initiative);
                        }
                        else // Auto / None
                        {
                            monster.setInitiativeBonus(bonus_initiative);
                        }
                        onInputMonsterResult.OnOk(monster, count, initiativeType);
                    }
                }
                else
                {
                    onInputMonsterResult.OnCancel();
                }
            }
        });

        startInputNumber = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    Intent intent = result.getData();
                    if(intent != null)
                    {
                        int number = intent.getIntExtra(InputTextActivity.TAG_NUM, 0);
                        onInputNumberResult.OnOk(number);
                    }
                }
                else
                {
                    onInputNumberResult.OnCancel();
                }
            }
        });

        startInputText = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode() == AppCompatActivity.RESULT_OK)
                {
                    Intent intent = result.getData();
                    if(intent != null)
                    {
                        String text = intent.getStringExtra(InputTextActivity.TAG_TEXT);
                        onInputTextResult.OnOk(text);
                    }
                }
                else
                {
                    onInputTextResult.OnCancel();
                }
            }
        });
    }

    private void ShowOkCancelDialog(int title, int message, int icon, boolean onlyOk)
    {
        ShowOkCancelDialog(activity.getString(title), activity.getString(message), icon, onlyOk);
    }

    private void ShowOkCancelDialog(String title, String message, int icon, boolean onlyOk)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_show_info, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        TextView textTitle = dialogView.findViewById(R.id.showDialogTextTitle);
        textTitle.setText(title);
        TextView textMessage = dialogView.findViewById(R.id.showDialogTextMessage);
        textMessage.setText(message);
        ImageView imagenIcon = dialogView.findViewById(R.id.showDialogIcon);
        imagenIcon.setImageResource(icon);

        AlertDialog alertDialog = builder.create();

        Button butOk = dialogView.findViewById(R.id.showDialogButOk);
        butOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(alertDialog.isShowing())
                {
                    alertDialog.dismiss();
                    if(onOkCancelResult != null)
                    {
                        onOkCancelResult.OnOk();
                    }
                }
            }
        });
        if(!onlyOk)
        {
            Button butCancel = dialogView.findViewById(R.id.showDialogButCancel);
            butCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(alertDialog.isShowing())
                    {
                        alertDialog.dismiss();
                        if(onOkCancelResult != null)
                        {
                            onOkCancelResult.OnCancel();
                        }
                    }
                }
            });
        }
        alertDialog.show();
    }

    public void ShowOkCancelDialog(int title, int message, int icon)
    {
        ShowOkCancelDialog(title, message, icon, false);
    }

    public void ShowOkCancelDialog(String title, String message, int icon)
    {
        ShowOkCancelDialog(title, message, icon, false);
    }

    public void ShowOkDialog(int title, int message, int icon)
    {
        ShowOkCancelDialog(title, message, icon, true);
    }

    public void ShowOkDialog(String title, String message, int icon)
    {
        ShowOkCancelDialog(title, message, icon, true);
    }

    public void InputNumber(String description, Counter value)
    {
        Intent intent = new Intent(activity, InputTextActivity.class);
        if(value.getMin() < 0 || value.getMax() < 0)
        {
            intent.putExtra(InputTextActivity.TAG_TYPE, InputTextActivity.TYPE_SIGNED_NUM);
        }
        else
        {
            intent.putExtra(InputTextActivity.TAG_TYPE, InputTextActivity.TYPE_UNSIGNED_NUM);
        }
        intent.putExtra(InputTextActivity.TAG_DESC, description);
        intent.putExtra(InputTextActivity.TAG_NUM, value.getValue());
        intent.putExtra(InputTextActivity.TAG_MIN, value.getMin());
        intent.putExtra(InputTextActivity.TAG_MAX, value.getMax());
        startInputNumber.launch(intent);
    }

    public void InputNumber(String description, int value, boolean signed)
    {
        Intent intent = new Intent(activity, InputTextActivity.class);
        if(signed)
        {
            intent.putExtra(InputTextActivity.TAG_TYPE, InputTextActivity.TYPE_SIGNED_NUM);
        }
        else
        {
            intent.putExtra(InputTextActivity.TAG_TYPE, InputTextActivity.TYPE_UNSIGNED_NUM);
        }
        intent.putExtra(InputTextActivity.TAG_DESC, description);
        intent.putExtra(InputTextActivity.TAG_NUM, value);
        startInputNumber.launch(intent);
    }

    public void InputMonter()
    {
        Intent intent = new Intent(activity, InputMonsterPlayerActivity.class);
        startInputMonster.launch(intent);
    }

    public void InputPlayer()
    {
        Intent intent = new Intent(activity, InputMonsterPlayerActivity.class);
        startInputMonster.launch(intent);
    }

    public void InputText(String desc, String text)
    {
        Intent intent = new Intent(activity, InputTextActivity.class);
        intent.putExtra(InputTextActivity.TAG_TEXT, text);
        intent.putExtra(InputTextActivity.TAG_DESC, desc);
        startInputText.launch(intent);
    }

    public void InputText()
    {
        Intent intent = new Intent(activity, InputTextActivity.class);
        startInputText.launch(intent);
    }

    public interface OnOkCancelResult
    {
        default void OnOk(){}
        default void OnCancel(){}
    }

    public interface OnInputTextResult
    {
        default void OnOk(String text){}
        default void OnCancel(){}
    }

    public interface OnInputNumberResult
    {
        default void OnOk(int number){}
        default void OnCancel(){}
    }

    public interface OnInputMonsterResult
    {
        default void OnOk(Monster defMonster, int count, EInitiative initiativeType){}
        default void OnCancel(){}
    }
}
