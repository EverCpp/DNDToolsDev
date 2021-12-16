package com.mlf.ddchar.recyclers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.enofir.utils.files.EnoFiles;
import com.mlf.ddchar.R;
import com.mlf.ddchar.dialogs.DialogLauncher;
import com.mlf.ddchar.objects.MyConstant;
import com.mlf.dndutils.CombatManager;
import com.mlf.dndutils.Monster;
import com.mlf.dndutils.types.EInitiative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CombatListItemAdapter extends RecyclerView.Adapter<CombatListItemAdapter.CombatListItemViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    private static final String FILE_DATA = "combat.dat";

    private final ArrayList<Monster> items;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;
    private final String fileName;

    public CombatListItemAdapter(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        fileName = MyConstant.WORKING_DIR + FILE_DATA;
        items = new ArrayList<>();
        Load();
    }

    private void Load()
    {
        items.clear();
        if(EnoFiles.Exists(fileName))
        {
            ArrayList<String> lines = EnoFiles.ReadLines(fileName, true, false);
            for(String line : lines)
            {
                Monster item = new Monster();
                item.fromJSON(line);
                items.add(item);
            }
        }
        else
        {
            items.clear();
            items.addAll(CombatManager.GenerateCombat(new Monster("NPC Ejemplo"), 4));
        }
    }

    private void Save()
    {
        ArrayList<String> lines = new ArrayList<>();
        for(Monster monster : items)
        {
            lines.add(monster.toString());
        }
        EnoFiles.WriteLines(fileName, lines, false);
    }

    @Override
    public void onRowSwipe(int position, int direction)
    {
        switch(direction)
        {
            case ItemTouchHelper.LEFT:
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        if((text != null) && !text.isEmpty())
                        {
                            updateItem(position, text);
                        }
                    }
                    @Override
                    public void OnCancel()
                    {
                        notifyItemChanged(position);
                    }
                });
                dialogLauncher.InputText(context.getString(R.string.dialog_edit_name), items.get(position).getName());
                break;
            case ItemTouchHelper.RIGHT:
                dialogLauncher.setOnOkCancelResult(new DialogLauncher.OnOkCancelResult()
                {
                    @Override
                    public void OnOk()
                    {
                        removeItem(position);
                    }
                    @Override
                    public void OnCancel()
                    {
                        notifyItemChanged(position);
                    }
                });
                String text = String.format(Locale.US, context.getString(R.string.dialog_remove_msg), items.get(position).getName());
                dialogLauncher.ShowOkCancelDialog(context.getString(R.string.dialog_remove_title), text, DialogLauncher.ICON_QUESTION);
                break;
        }
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition)
    {
        if(fromPosition < toPosition)
        {
            for(int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(items, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(items, i, i - 1);
            }
        }
        Save();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder myViewHolder)
    {
        myViewHolder.itemView.setFocusableInTouchMode(true);
        myViewHolder.itemView.requestFocus();
    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder myViewHolder)
    {
        myViewHolder.itemView.clearFocus();
        myViewHolder.itemView.setFocusableInTouchMode(false);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public ArrayList<Monster> getItems()
    {
        return items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(ArrayList<Monster> items)
    {
        this.items.clear();
        if(items != null && items.size() > 0)
        {
            this.items.addAll(items);
            notifyDataSetChanged();
            Save();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(Monster defMonster, int count, EInitiative initiativeType)
    {
        setItems(CombatManager.GenerateCombat(defMonster, count, initiativeType));
    }

    public Monster getItemAt(int position)
    {
        if((position < 0) || (position >= items.size()))
        {
            return null;
        }
        return items.get(position);
    }

    public void updateItem(int pos, String name)
    {
        items.get(pos).setName(name);
        notifyItemChanged(pos);
        Save();
    }

    public void updateItem(int pos, Monster item)
    {
        items.get(pos).Set(item);
        notifyItemChanged(pos);
        Save();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Monster monster, EInitiative initiativeType)
    {
        if((initiativeType == EInitiative.AUTO) || (initiativeType == EInitiative.MANUAL))
        {
            CombatManager.Add(this.items, monster);
            notifyDataSetChanged();
        }
        else
        {
            items.add(monster);
            notifyItemInserted(items.size() - 1);
        }
        Save();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItems(Monster defMonster, int count, EInitiative initiativeType)
    {
        CombatManager.Add(items, defMonster, count, initiativeType);
        notifyDataSetChanged();
        Save();
    }

    public void removeItem(int position)
    {
        items.remove(position);
        notifyItemRemoved(position);
        Save();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear()
    {
        items.clear();
        notifyDataSetChanged();
        Save();
    }

    /**
     * Provide a reference to the type of views that you are using (custom GridItemViewHolder).
     */
    public static class CombatListItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textName, textHitPoints;
        public TextView textInitiative1, textInitiative2;
        public Button butUp, butDown;
        public ConstraintLayout layout;
        private final View.OnLongClickListener listenerNone;

        public CombatListItemViewHolder(View viewItem)
        {
            super(viewItem);
            textName = viewItem.findViewById(R.id.combatItemTextName);
            textInitiative1 = viewItem.findViewById(R.id.combatItemTextInitiative1);
            textInitiative2 = viewItem.findViewById(R.id.combatItemTextInitiative2);
            textHitPoints = viewItem.findViewById(R.id.combatItemTextHitPoints);
            butUp = viewItem.findViewById(R.id.combatItemButUp);
            butDown = viewItem.findViewById(R.id.combatItemButDown);
            layout = viewItem.findViewById(R.id.combatItemLayout);

            listenerNone = new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    return true;
                }
            };
            textName.setFocusable(false);
            textName.setOnLongClickListener(listenerNone);
            textInitiative1.setFocusable(false);
            textInitiative1.setOnLongClickListener(listenerNone);
            textInitiative2.setFocusable(false);
            textInitiative2.setOnLongClickListener(listenerNone);
        }
    }

    @NonNull
    @Override
    public CombatListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_combat, parent, false);
        return new CombatListItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CombatListItemViewHolder holder, int position)
    {
        // Producto y contexto
        Monster item = items.get(position);

        holder.textInitiative1.setText(String.format(Locale.US, "%d", item.getInitiative1()));
        holder.textInitiative2.setText(String.format(Locale.US, "%d", item.getInitiative2()));
        holder.textName.setText(item.getName());
        holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));

        holder.textName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialogLauncher != null)
                {
                    dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                    {
                        @Override
                        public void OnOk(String text)
                        {
                            if((text != null) && !text.isEmpty())
                            {
                                item.setName(text);
                                holder.textName.setText(item.getName());
                                Save();
                            }
                        }
                    });
                    dialogLauncher.InputText(context.getString(R.string.dialog_edit_name), item.getName());
                }
            }
        });
        holder.textHitPoints.setOnClickListener(new View.OnClickListener()
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
                            item.setHitPoints(number);
                            holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));
                            Save();
                        }
                    });
                    dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_hitpoints), item.getName()), item.getHitPoints());
                }
            }
        });
        holder.butUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.getHitPoints().inc();
                holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));
                Save();
            }
        });
        holder.butUp.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(dialogLauncher != null)
                {
                    dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                    {
                        @Override
                        public void OnOk(int number)
                        {
                            item.getHitPoints().inc(number);
                            holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));
                            Save();
                        }
                    });
                    dialogLauncher.InputNumber(String.format(Locale.US, context.getResources().getString(R.string.dialog_edit_add), item.getName()), 0, false);
                }
                return true;
            }
        });
        holder.butDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.getHitPoints().dec();
                holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));
                Save();
            }
        });
        holder.butDown.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(dialogLauncher != null)
                {
                    dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                    {
                        @Override
                        public void OnOk(int number)
                        {
                            item.getHitPoints().dec(number);
                            holder.textHitPoints.setText(String.format(Locale.US, "%d", item.getHitPoints().getValue()));
                            Save();
                        }
                    });
                    dialogLauncher.InputNumber(String.format(Locale.US, context.getResources().getString(R.string.dialog_edit_suppress), item.getName()), 0, false);
                }
                return true;
            }
        });
    }
}
