package com.mlf.dndtools.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.utils.Constant;
import com.mlf.dndutils.Char;
import com.mlf.dndutils.common.Ability;
import com.mlf.dndutils.enums.EAbility;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CharSavingItemAdapter extends RecyclerView.Adapter<CharSavingItemAdapter.CharSavingItemViewHolder>
{
    private final Char character;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;

    public CharSavingItemAdapter(DialogLauncher dialogLauncher, Char character)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        this.character = character;
    }

    @Override
    public int getItemCount()
    {
        return character.getSavingsCount();
    }

    private int getAbilityColor(EAbility type)
    {
        switch(type)
        {
            case STRENGTH:
                return context.getResources().getColor(R.color.ability_strength);
            case DEXTERITY:
                return context.getResources().getColor(R.color.ability_dexterity);
            case CONSTITUTION:
                return context.getResources().getColor(R.color.ability_constitution);
            case INTELLIGENCE:
                return context.getResources().getColor(R.color.ability_intelligence);
            case WISDOM:
                return context.getResources().getColor(R.color.ability_wisdom);
            case CHARISMA:
                return context.getResources().getColor(R.color.ability_charisma);
        }
        return context.getResources().getColor(R.color.content_text_enabled);
    }

    /**
     * Provide a reference to the type of views that you are using (custom GridItemViewHolder).
     */
    public static class CharSavingItemViewHolder extends RecyclerView.ViewHolder
    {
        public CheckBox checkBox;
        public TextView textBonus;
        public ConstraintLayout layout;

        public CharSavingItemViewHolder(View viewItem)
        {
            super(viewItem);
            checkBox = viewItem.findViewById(R.id.savingItemCheckBox);
            textBonus = viewItem.findViewById(R.id.savingItemTextBonus);
            layout = viewItem.findViewById(R.id.savingItemLayout);
        }
    }

    @NonNull
    @Override
    public CharSavingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_saving, parent, false);
        return new CharSavingItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CharSavingItemViewHolder holder, int position)
    {
        // Producto y contexto
        Ability ability = character.getAbility(position);
        Context context = holder.layout.getContext();
        int color = getAbilityColor(ability.getType());

        // Color del texto
        holder.checkBox.setTextColor(color);
        holder.textBonus.setTextColor(color);

        // Texto/estado
        holder.textBonus.setText(String.format(Locale.US, (ability.getSavingValue() < 0) ? "%d" : "%+d", ability.getSavingValue()));
        holder.checkBox.setText(Constant.lang.getName(ability));
        holder.checkBox.setChecked(ability.isSelSaving());

        // Listeners
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ability.setSelSaving(isChecked);
            }
        });

        holder.textBonus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                {
                    @Override
                    public void OnOk(int number)
                    {
                        ability.setSaving(number);
                        holder.textBonus.setText(String.format(Locale.US, (ability.getSavingValue() < 0) ? "%d" : "%+d", ability.getSavingValue()));
                    }
                });
                dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_saving),  Constant.lang.getName(ability)), ability.getSaving());
            }
        });
    }
}
