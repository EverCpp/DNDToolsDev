package com.mlf.dndtools.recyclers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.enofir.utils.files.EnoFiles;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.utils.Constant;
import com.mlf.dndutils.common.Counter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CounterListItemAdapter extends RecyclerView.Adapter<CounterListItemAdapter.CounterListItemViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    private static final String FILE_DATA = "counters.dat";

    private final ArrayList<Counter> items;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;
    private final String fileName;

    public CounterListItemAdapter(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
        context = this.dialogLauncher.getContext();
        fileName = Constant.WORKING_DIR + FILE_DATA;
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
                Counter item = new Counter();
                item.fromJSON(line);
                addItem(item);
            }
        }
        else
        {
            addItem(new Counter("Contador ejemplo 1"));
            addItem(new Counter("Contador ejemplo 2"));
            addItem(new Counter("Contador ejemplo 3"));
        }
    }

    private void Save()
    {
        ArrayList<String> lines = new ArrayList<>();
        for(Counter counter : items)
        {
            lines.add(counter.toString());
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

    public ArrayList<Counter> getItems()
    {
        return items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(ArrayList<Counter> items)
    {
        this.items.clear();
        if(items != null && items.size() > 0)
        {
            this.items.addAll(items);
            notifyDataSetChanged();
            Save();
        }
    }

    public Counter getItemAt(int position)
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

    public void updateItem(int pos, Counter item)
    {
        items.get(pos).Set(item);
        notifyItemChanged(pos);
        Save();
    }

    public void addItem(Counter item)
    {
        items.add(item);
        notifyItemInserted(items.size() - 1);
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
    public static class CounterListItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textDesc;
        public TextView textvalue;
        public Button butUp, butDown;
        private final View.OnLongClickListener listenerNone;

        public CounterListItemViewHolder(View viewItem)
        {
            super(viewItem);
            textDesc = viewItem.findViewById(R.id.counterItemTextName);
            textvalue = viewItem.findViewById(R.id.counterItemTextValue);
            butUp = viewItem.findViewById(R.id.counterItemButUp);
            butDown = viewItem.findViewById(R.id.counterItemButDown);

            listenerNone = new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    return true;
                }
            };
            textDesc.setFocusable(false);
            textDesc.setOnLongClickListener(listenerNone);
            textvalue.setFocusable(false);
            textvalue.setOnLongClickListener(listenerNone);
        }
    }

    @NonNull
    @Override
    public CounterListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_counter, parent, false);
        return new CounterListItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CounterListItemViewHolder holder, int position)
    {
        // Producto y contexto
        Counter item = items.get(position);

        holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
        holder.textDesc.setText(item.getName());
        holder.textDesc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        if((text != null) && !text.isEmpty())
                        {
                            item.setName(text);
                            holder.textDesc.setText(item.getName());
                            Save();
                        }
                    }
                });
                dialogLauncher.InputText(context.getString(R.string.dialog_edit_name), item.getName());
            }

        });
        holder.textvalue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                {
                    @Override
                    public void OnOk(int number)
                    {
                        item.setValue(number);
                        holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
                        Save();
                    }
                });
                dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_val), item.getName()), item);
            }
        });
        holder.butUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.inc();
                holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
                Save();
            }
        });
        holder.butUp.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                {
                    @Override
                    public void OnOk(int number)
                    {
                        item.inc(number);
                        holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
                        Save();
                    }
                });
                dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_add), item.getName()), 0, false);
                return true;
            }
        });
        holder.butDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                item.dec();
                holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
                Save();
            }
        });
        holder.butDown.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                dialogLauncher.setOnInputNumberResult(new DialogLauncher.OnInputNumberResult()
                {
                    @Override
                    public void OnOk(int number)
                    {
                        item.dec(number);
                        holder.textvalue.setText(String.format(Locale.US, "%d", item.getValue()));
                        Save();
                    }
                });
                dialogLauncher.InputNumber(String.format(Locale.US, context.getString(R.string.dialog_edit_suppress), item.getName()), 0, false);
                return true;
            }
        });
    }
}
