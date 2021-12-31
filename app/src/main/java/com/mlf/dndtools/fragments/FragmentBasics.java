package com.mlf.dndtools.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mlf.dndtools.R;
import com.mlf.dndtools.dialogs.DialogLauncher;
import com.mlf.dndtools.utils.MyLog;
import com.mlf.dndtools.recyclers.CounterListItemAdapter;
import com.mlf.dndtools.recyclers.InventoryItemAdapter;
import com.mlf.dndtools.recyclers.ItemMoveCallback;
import com.mlf.dndutils.common.Counter;
import com.mlf.dndutils.common.Item;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class FragmentBasics extends Fragment
{
    private View viewFragment;
    private TextView textCounters, textInventory;
    private RecyclerView recyclerCounters, recyclerInventory;
    private Toolbar toolbar;
    private ImageButton butAddCounter, butAddItem;
    private View divider;
    private InventoryItemAdapter adapterInventory;
    private CounterListItemAdapter adapterCounters;
    private final DialogLauncher dialogLauncher;

    private int viewHeight, viewWidth;
    private int dividerMinY, dividerMaxY;

    public FragmentBasics(DialogLauncher dialogLauncher)
    {
        this.dialogLauncher = dialogLauncher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Adapters
        adapterInventory = new InventoryItemAdapter(dialogLauncher);
        adapterCounters = new CounterListItemAdapter(dialogLauncher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_basics, container, false);

        // Text y Buttons
        textCounters = viewFragment.findViewById(R.id.basicsTextCounters);
        textInventory = viewFragment.findViewById(R.id.basicsTextInventory);
        butAddCounter = viewFragment.findViewById(R.id.basicsButAddCounter);
        butAddItem = viewFragment.findViewById(R.id.basicsButAddItem);
        toolbar = viewFragment.findViewById(R.id.basicsToolbar);
        divider = viewFragment.findViewById(R.id.basicsViewDivider);
        // Recyclers
        recyclerInventory = viewFragment.findViewById(R.id.basicsRecyclerInventory);
        recyclerInventory.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerInventory.setAdapter(adapterInventory);

        recyclerCounters = viewFragment.findViewById(R.id.basicsRecyclerCounters);
        recyclerCounters.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerCounters.setAdapter(adapterCounters);

        // Para el manejo del swipe
        ItemTouchHelper.Callback callbackCounters = new ItemMoveCallback(adapterCounters);
        ItemTouchHelper touchHelperCounter = new ItemTouchHelper(callbackCounters);
        touchHelperCounter.attachToRecyclerView(recyclerCounters);

        ItemTouchHelper.Callback callbackInventory = new ItemMoveCallback(adapterInventory);
        ItemTouchHelper touchHelperInventory = new ItemTouchHelper(callbackInventory);
        touchHelperInventory.attachToRecyclerView(recyclerInventory);

        viewFragment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                viewFragment.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                viewHeight = viewFragment.getHeight();
                viewWidth = viewFragment.getWidth();
                int recyclerHeight = viewHeight - textCounters.getHeight() - textInventory.getHeight() - toolbar.getHeight() - divider.getHeight();
                ConstraintLayout.LayoutParams par = (ConstraintLayout.LayoutParams) divider.getLayoutParams();
                par.topMargin = recyclerHeight/2;
                divider.setLayoutParams(par);
                dividerMinY = 0;
                dividerMaxY = viewHeight - toolbar.getHeight() - textInventory.getHeight() - divider.getHeight();
            }
        });
        return viewFragment;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed(){}
        });

        butAddItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        Item item = new Item(text);
                        adapterInventory.addItem(item);
                    }
                });
                dialogLauncher.InputText("Descripción del ítem", "");
            }
        });
        butAddCounter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLauncher.setOnInputTextResult(new DialogLauncher.OnInputTextResult()
                {
                    @Override
                    public void OnOk(String text)
                    {
                        Counter counter = new Counter(text);
                        adapterCounters.addItem(counter);
                    }
                });
                dialogLauncher.InputText("Nombre del contador", "");
            }
        });

        divider.setOnTouchListener(new View.OnTouchListener()
        {
            int prevX, prevY;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final ConstraintLayout.LayoutParams par = (ConstraintLayout.LayoutParams) v.getLayoutParams();

                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        MyLog.e("ACTION_MOVE - " + par.topMargin);
                        par.topMargin += ((int)event.getRawY() - prevY);
                        if(par.topMargin < dividerMinY)
                        {
                            par.topMargin = dividerMinY;
                        }
                        else if(par.topMargin > dividerMaxY)
                        {
                            par.topMargin = dividerMaxY;
                        }
                        prevY = (int)event.getRawY();
                        prevX = (int)event.getRawX();
                        v.setLayoutParams(par);
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        v.setPressed(false);
                        par.topMargin += ((int)event.getRawY() - prevY);
                        v.setLayoutParams(par);
                        return true;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        v.setPressed(true);
                        prevX = (int) event.getRawX();
                        prevY = (int) event.getRawY();
                        return true;
                    }
                }
                return false;
            }
        });

        /*divider.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                // Create a new ClipData.Item from the View object's tag
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());

                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This will create a new ClipDescription object within the
                // ClipData, and set its MIME type entry to "text/plain"
                String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
                ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);

                // Instantiates the drag shadow builder.
                View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);

                // Starts the drag
                v.startDrag(data       // data to be dragged
                        , dragshadow  // drag shadow
                        , v            // local data about the drag and drop operation
                        , 0          // flags set to 0 because not using currently
                );
                return true;
            }
        });*/

        /*
        divider.setTag("basicsDivider");
        divider.setOnDragListener(new View.OnDragListener()
        {
            @Override
            public boolean onDrag(View v, DragEvent event)
            {
                switch(event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        MyLog.e("ACTION_DRAG_STARTED");
                        // Determines if this View can accept the dragged data
                        if(event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                        {
                            // applies a blue color tint to the View to indicate that it can accept the data
                            //v.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                            v.setBackgroundColor(getResources().getColor(R.color.brown_dark));
                            // Invalidate the view to force a redraw in the new tint
                            v.invalidate();
                            // returns true to indicate that the View can accept the dragged data.
                            return true;
                        }
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        return false;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        MyLog.e("ACTION_DRAG_ENTERED");

                        // Applies a YELLOW or any color tint to the View. Return true; the return value is ignored.
                        //v.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Ignore the event
                        MyLog.e("ACTION_DRAG_LOCATION");
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        MyLog.e("ACTION_DRAG_EXITED");
                        // Re-sets the color tint to blue, if you had set the BLUE color or any color in ACTION_DRAG_STARTED. Returns true; the return value is ignored.
                        //v.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                        //If u had not provided any color in ACTION_DRAG_STARTED then clear color filter.
                        //v.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        // Gets the item containing the dragged data
                        //ClipData.Item item = event.getClipData().getItemAt(0);
                        // Gets the text data from the item.
                        //String dragData = item.getText().toString();
                        // Displays a message containing the dragged data.
                        //Toast.makeText(v.getContext(), "Dragged data is " + dragData, Toast.LENGTH_SHORT).show();
                        // Turns off any color tints
                        //v.getBackground().clearColorFilter();
                        // Invalidates the view to force a redraw
                        v.invalidate();
                        // Returns true. DragEvent.getResult() will return true.
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        MyLog.e("ACTION_DRAG_ENDED");
                        // Turns off any color tinting
                        //v.getBackground().clearColorFilter();
                        v.setBackgroundColor(getResources().getColor(R.color.brown_light));
                        // Invalidates the view to force a redraw
                        v.invalidate();
                        // Does a getResult(), and displays what happened.
                        //if(event.getResult())
                        //    Toast.makeText(v.getContext(), "The drop was handled.", Toast.LENGTH_SHORT).show();
                        //else
                        //    Toast.makeText(v.getContext(), "The drop didn't work.", Toast.LENGTH_SHORT).show();
                        // returns true; the value is ignored.
                        return true;
                    // An unknown action type was received.
                    default:
                        MyLog.e("Unknown action type received by OnDragListener.");
                        break;
                }
                return false;
            }
        });*/
    }

    public InventoryItemAdapter getAdapterInventory()
    {
        return adapterInventory;
    }

    public CounterListItemAdapter getAdapterCounters()
    {
        return adapterCounters;
    }
}
