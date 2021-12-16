package com.mlf.dndtools.recyclers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndutils.common.Item;
import java.util.ArrayList;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FileChooserItemAdapter extends RecyclerView.Adapter<FileChooserItemAdapter.FileChooserItemViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    private final ArrayList<Item> items;
    //private String curFileName;

    public FileChooserItemAdapter(DialogLauncher dialogLauncher)
    {
        items = new ArrayList<>();
        //curFileName = "";
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

    public void updateItem(int pos, Item item)
    {
        items.get(pos).Set(item);
        notifyItemChanged(pos);
    }

    public void addItem(Item item)
    {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int index)
    {
        items.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * Provide a reference to the type of views that you are using (custom GridItemViewHolder).
     */
    public static class FileChooserItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textName;
        public ImageView imageIcon;
        public ConstraintLayout layout;
        public FileChooserItemViewHolder(View viewItem)
        {
            super(viewItem);
            textName = viewItem.findViewById(R.id.chooseFileItemTextName);
            imageIcon = viewItem.findViewById(R.id.chooseFileItemImageIcon);
            layout = viewItem.findViewById(R.id.chooseFileItemLayout);
        }
    }

    @NonNull
    @Override
    public FileChooserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_file_chooser, parent, false);
        return new FileChooserItemViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FileChooserItemViewHolder holder, int position)
    {
        // Item
        Item item = items.get(position);

        holder.textName.setText(item.getDescription());

        holder.textName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        });
    }
}
