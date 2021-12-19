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
import com.mlf.dndutils.Char;
import com.mlf.dndutils.common.Skill;
import com.mlf.dndutils.enums.EAbility;
import com.mlf.dndutils.enums.ESkill;
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

    private String getSkillName(ESkill type)
    {
        switch(type)
        {
            case ATHLETICS:
                return context.getResources().getString(R.string.skill_athletics);
            case ACROBATICS:
                return context.getResources().getString(R.string.skill_acrobatics);
            case SLEIGHT_OF_HAND:
                return context.getResources().getString(R.string.skill_sleight_of_hand);
            case STEALTH:
                return context.getResources().getString(R.string.skill_stealth);
            case ARCANA:
                return context.getResources().getString(R.string.skill_arcana);
            case HISTORY:
                return context.getResources().getString(R.string.skill_history);
            case INVESTIGATION:
                return context.getResources().getString(R.string.skill_investigation);
            case NATURE:
                return context.getResources().getString(R.string.skill_nature);
            case RELIGION:
                return context.getResources().getString(R.string.skill_religion);
            case ANIMAL_HANDLING:
                return context.getResources().getString(R.string.skill_animal_handling);
            case INSIGHT:
                return context.getResources().getString(R.string.skill_insight);
            case MEDICINE:
                return context.getResources().getString(R.string.skill_medicine);
            case PERCEPTION:
                return context.getResources().getString(R.string.skill_perception);
            case SURVIVAL:
                return context.getResources().getString(R.string.skill_survival);
            case DECEPTION:
                return context.getResources().getString(R.string.skill_deception);
            case INTIMIDATION:
                return context.getResources().getString(R.string.skill_intimidation);
            case PERFORMANCE:
                return context.getResources().getString(R.string.skill_performance);
            case PERSUASION:
                return context.getResources().getString(R.string.skill_persuasion);
        }
        return type.name();
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

        //String textName = String.format(Locale.US, "%s (%s)", getSkillName(skill.getSkill()), getAbilityShortName(skill.getAbility()));
        //holder.checkBox.setText(textName);
        holder.checkBox.setText(getSkillName(skill.getSkill()));
        holder.checkBox.setChecked(skill.isSelected());
        String textBonus;
        if(skill.getBonus() < 0)
        {
            textBonus = String.format(Locale.US, "%d", skill.getBonus());
        }
        else
        {
            textBonus = String.format(Locale.US, "+%d", skill.getBonus());
        }
        holder.textBonus.setText(textBonus);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                skill.setSelected(isChecked);
            }
        });
    }
}
