package com.mlf.ddchar.interfaces;

public interface OnRecyclerChangeDataListener
{
    int ITEM_EDIT = 1;
    int ITEM_REMOVE = 2;
    default void onDataChange(int position, int action){}
}
