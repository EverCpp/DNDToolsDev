package com.mlf.ddchar.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.ddchar.R;
import com.mlf.ddchar.dialogs.DialogLauncher;
import com.mlf.dndutils.Char;
import com.mlf.dndutils.common.Ability;
import com.mlf.dndutils.types.EAbility;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CharAbilityItemAdapter extends RecyclerView.Adapter<CharAbilityItemAdapter.CharAbilityItemViewHolder>
{
    private final Char player;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;

    public CharAbilityItemAdapter(DialogLauncher dialogLauncher, Char player)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        this.player = player;
    }

    @Override
    public int getItemCount()
    {
        return player.getAbilitiesCount();
    }

    private String getAbilityName(EAbility type)
    {
        switch(type)
        {
            case STRENGTH:
                return context.getResources().getString(R.string.ability_strength_short);
            case DEXTERITY:
                return context.getResources().getString(R.string.ability_dextery_short);
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
        Ability ability = player.getAbility(position);
        Context context = holder.layout.getContext();

        holder.textName.setText(getAbilityName(ability.getType()));
        holder.textValue.setText(String.format(Locale.US, "%d", ability.getValue()));
        if(ability.getBonusValue() >= 0)
        {
            holder.textBonus.setText(String.format(Locale.US, "+%d", ability.getBonusValue()));
        }
        else
        {
            holder.textBonus.setText(String.format(Locale.US, "%d", ability.getBonusValue()));
        }
        holder.textValue.setOnClickListener(new View.OnClickListener()
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
                            ability.calcBonus();
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
        });
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
