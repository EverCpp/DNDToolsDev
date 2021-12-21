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
import com.mlf.dndutils.common.Skill;
import com.mlf.dndutils.enums.EAbility;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CharSkillItemAdapter extends RecyclerView.Adapter<CharSkillItemAdapter.CharSkillItemViewHolder>
{
    private final Char character;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;

    public CharSkillItemAdapter(DialogLauncher dialogLauncher, Char character)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        this.character = character;
    }

    @Override
    public int getItemCount()
    {
        return character.getSkillsCount();
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
    public static class CharSkillItemViewHolder extends RecyclerView.ViewHolder
    {
        public CheckBox checkBox;
        public TextView textBonus;
        public ConstraintLayout layout;

        public CharSkillItemViewHolder(View viewItem)
        {
            super(viewItem);
            checkBox = viewItem.findViewById(R.id.skillItemCheckBox);
            textBonus = viewItem.findViewById(R.id.skillItemTextBonus);
            layout = viewItem.findViewById(R.id.skillItemLayout);
        }
    }

    @NonNull
    @Override
    public CharSkillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_skill, parent, false);
        return new CharSkillItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CharSkillItemViewHolder holder, int position)
    {
        // Producto y contexto
        Skill skill = character.getSkill(position);
        Context context = holder.layout.getContext();
        int color = getAbilityColor(skill.getAbility());

        // Color del texto
        holder.checkBox.setTextColor(color);
        holder.textBonus.setTextColor(color);

        // Texto/estado
        holder.textBonus.setText(String.format(Locale.US, (skill.getBonusValue() < 0) ? "%d" : "%+d", skill.getBonusValue()));
        holder.checkBox.setText(Constant.lang.getName(skill));
        holder.checkBox.setChecked(skill.isSelected());

        // Listeners
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                character.setSkillSelected(skill.getSkill(), isChecked);
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
                        skill.setBonus(number);
                        holder.textBonus.setText(String.format(Locale.US, (skill.getBonusValue() < 0) ? "%d" : "%+d", skill.getBonusValue()));
                    }
                });
                dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_bonus),  Constant.lang.getName(skill)), skill.getBonus());
            }
        });
    }
}
