package com.mlf.dndtools.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndutils.Char;
import com.mlf.dndutils.common.Ability;
import com.mlf.dndutils.enums.EAbility;

import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CharAbilityItemAdapter extends RecyclerView.Adapter<CharAbilityItemAdapter.CharAbilityItemViewHolder>
{
    private final Char character;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;

    public CharAbilityItemAdapter(DialogLauncher dialogLauncher, Char character)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        this.character = character;
    }

    @Override
    public int getItemCount()
    {
        return character.getAbilitiesCount();
    }

    private String getAbilityName(EAbility type)
    {
        switch(type)
        {
            case STRENGTH:
                return context.getResources().getString(R.string.ability_strength);
            case DEXTERITY:
                return context.getResources().getString(R.string.ability_dexterity);
            case CONSTITUTION:
                return context.getResources().getString(R.string.ability_constitution);
            case INTELLIGENCE:
                return context.getResources().getString(R.string.ability_intelligence);
            case WISDOM:
                return context.getResources().getString(R.string.ability_wisdom);
            case CHARISMA:
                return context.getResources().getString(R.string.ability_charisma);
        }
        return type.name();
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

    private String getAbilityShortName(EAbility type)
    {
        switch(type)
        {
            case STRENGTH:
                return context.getResources().getString(R.string.ability_strength_short);
            case DEXTERITY:
                return context.getResources().getString(R.string.ability_dexterity_short);
            case CONSTITUTION:
                return context.getResources().getString(R.string.ability_constitution_short);
            case INTELLIGENCE:
                return context.getResources().getString(R.string.ability_intelligence_short);
            case WISDOM:
                return context.getResources().getString(R.string.ability_wisdom_short);
            case CHARISMA:
                return context.getResources().getString(R.string.ability_charisma_short);
        }
        return type.name();
    }

    /**
     * Provide a reference to the type of views that you are using (custom GridItemViewHolder).
     */
    public static class CharAbilityItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textName, textValue, textBonus;
        public ConstraintLayout layout;

        public CharAbilityItemViewHolder(View viewItem)
        {
            super(viewItem);
            textName = viewItem.findViewById(R.id.abilityItemTextName);
            textValue = viewItem.findViewById(R.id.abilityItemTextValue);
            textBonus = viewItem.findViewById(R.id.abilityItemTextBonus);
            layout = viewItem.findViewById(R.id.abilityItemLayout);
        }
    }

    @NonNull
    @Override
    public CharAbilityItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_ability, parent, false);
        return new CharAbilityItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CharAbilityItemViewHolder holder, int position)
    {
        // Producto y contexto
        Ability ability = character.getAbility(position);
        Context context = holder.layout.getContext();
        int color = getAbilityColor(ability.getType());

        // Color del texto
        holder.textName.setTextColor(color);
        holder.textValue.setTextColor(color);
        holder.textBonus.setTextColor(color);

        // Textos
        holder.textName.setText(getAbilityShortName(ability.getType()));
        holder.textValue.setText(String.format(Locale.US, "%d", ability.getValue()));
        if(ability.getBonusValue() < 0)
        {
            holder.textBonus.setText(String.format(Locale.US, "%d", ability.getBonusValue()));
        }
        else
        {
            holder.textBonus.setText(String.format(Locale.US, "+%d", ability.getBonusValue()));
        }

        // Listeners
        View.OnClickListener onClickValueListener = new View.OnClickListener()
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
                            ability.setValue(number);
                            ability.updateBonus();
                            holder.textValue.setText(String.format(Locale.US, "%d", ability.getValue()));
                            if(ability.getBonusValue() >= 0)
                            {
                                holder.textBonus.setText(String.format(Locale.US, "+%d", ability.getBonusValue()));
                            }
                            else
                            {
                                holder.textBonus.setText(String.format(Locale.US, "%d", ability.getBonusValue()));
                            }
                        }
                    });
                    dialogLauncher.InputNumber(getAbilityName(ability.getType()), ability);
                }
            }
        };

        holder.textValue.setOnClickListener(onClickValueListener);
        holder.textName.setOnClickListener(onClickValueListener);
        holder.textBonus.setOnClickListener(new View.OnClickListener()
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
                            ability.setBonus(number);
                            if(ability.getBonusValue() >= 0)
                            {
                                holder.textBonus.setText(String.format(Locale.US, "+%d", ability.getBonusValue()));
                            }
                            else
                            {
                                holder.textBonus.setText(String.format(Locale.US, "%d", ability.getBonusValue()));
                            }
                        }
                    });
                    dialogLauncher.InputNumber("Bonificador de " + getAbilityName(ability.getType()), ability.getBonus());
                }
            }
        });
    }
}
