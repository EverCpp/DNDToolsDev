package com.mlf.dndtools.recyclers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.enofir.utils.files.EnoFiles;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.objects.MyConstant;
import com.mlf.dndutils.common.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    private static final String FILE_DATA = "inventory.dat";

    private final ArrayList<Item> items;
    private final DialogLauncher dialogLauncher;
    private final AppCompatActivity context;
    private final String fileName;

    public InventoryItemAdapter(DialogLauncher dialogLauncher)
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
                Item item = new Item();
                item.fromJSON(line);
                addItem(item);
            }
        }
        else
        {
            addItem(new Item("Item de ejemplo 1"));
            addItem(new Item("Item de ejemplo 2"));
        }
    }

    private void Save()
    {
        ArrayList<String> lines = new ArrayList<>();
        for(Item item : items)
        {
            lines.add(item.toString());
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
                dialogLauncher.InputText(context.getString(R.string.dialog_edit_desc), items.get(position).getDescription());
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
                String text = String.format(Locale.US, context.getString(R.string.dialog_remove_msg), items.get(position).getDescription());
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

    public ArrayList<Item> getItems()
    {
        return items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(ArrayList<Item> items)
    {
        this.items.clear();
        if(items != null && items.size() > 0)
        {
            this.items.addAll(items);
            Save();
            notifyDataSetChanged();
        }
    }

    public Item getItemAt(int position)
    {
        if((position < 0) || (position >= items.size()))
        {
            return null;
        }
        return items.get(position);
    }

    public void updateItem(int pos, String name)
    {
        items.get(pos).setDescription(name);
        notifyItemChanged(pos);
        Save();
    }

    public void updateItem(int pos, Item item)
    {
        items.get(pos).Set(item);
        notifyItemChanged(pos);
        Save();
    }

    public void addItem(Item item)
    {
        items.add(item);
        Save();
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int index)
    {
        items.remove(index);
        notifyItemRemoved(index);
        Save();
    }

    /**
     * Provide a reference to the type of views that you are using (custom GridItemViewHolder).
     */
    public static class InventoryItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textDesc;
        private final View.OnLongClickListener listenerNone;
        public InventoryItemViewHolder(View viewItem)
        {
            super(viewItem);
            textDesc = viewItem.findViewById(R.id.inventoryItemTextName);

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
        }
    }

    @NonNull
    @Override
    public InventoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_inventory, parent, false);
        return new InventoryItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, int position)
    {
        // Item
        Item item = items.get(position);

        holder.textDesc.setText(item.getDescription());
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
                            item.setDescription(text);
                            holder.textDesc.setText(item.getDescription());
                            Save();
                        }
                    }
                });
                dialogLauncher.InputText(context.getString(R.string.dialog_edit_desc), item.getDescription());
            }
        });
    }
}
