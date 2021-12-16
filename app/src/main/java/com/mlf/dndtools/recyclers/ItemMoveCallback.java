package com.mlf.dndtools.recyclers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;

public class ItemMoveCallback extends ItemTouchHelper.Callback
{
    private static final float ICON_PROP = 0.8f;
    private static final boolean LEFT = true;
    private static final boolean RIGHT = false;

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter)
    {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
    {
        mAdapter.onRowSwipe(viewHolder.getAdapterPosition(), direction);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
    {
        if(viewHolder.getItemViewType() != target.getItemViewType())
        {
            return false;
        }
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)
        {
            mAdapter.onRowSelected(viewHolder);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);
        mAdapter.onRowClear(viewHolder);
    }

    private Rect getIconRect(View view, boolean left)
    {
        int areaSide = view.getHeight();
        int margin = (int)(areaSide*(1 - ICON_PROP)*0.5f);
        Rect rc = new Rect();
        rc.top = view.getTop() + margin;
        rc.bottom = view.getBottom() - margin;
        if(left)
        {
            rc.left = view.getLeft() + margin;
            rc.right = view.getLeft() + areaSide - margin;
        }
        else
        {
            rc.left = view.getRight() - areaSide + margin;
            rc.right = view.getRight() - margin;
        }
        return rc;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
    {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            View view = viewHolder.itemView;
            Context context = view.getContext();
            float deltaX = view.getHeight();
            Rect rcLeft = getIconRect(view, LEFT);
            Rect rcRight = getIconRect(view, RIGHT);

            if(dX < -deltaX)
            {
                dX = -deltaX;
            }
            else if(dX > deltaX)
            {
                dX = deltaX;
            }
            // Ícono derecho, editar
            if(dX < rcRight.left - view.getRight())
            {
                Drawable icon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_item_edit, null);
                if(icon != null)
                {
                    icon.setBounds(rcRight);
                    icon.draw(c);
                }
            }
            // Ícono izquierdo, eliminar
            else if(dX > rcLeft.right)
            {
                Drawable icon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_item_delete, null);
                if(icon != null)
                {
                    icon.setBounds(rcLeft);
                    icon.draw(c);
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @SuppressWarnings("unused")
    public interface ItemTouchHelperContract
    {
        default void onRowSwipe(int position, int direction){}
        default void onRowMoved(int fromPosition, int toPosition){}
        default void onRowSelected(RecyclerView.ViewHolder myViewHolder){}
        default void onRowClear(RecyclerView.ViewHolder myViewHolder){}
    }
}
