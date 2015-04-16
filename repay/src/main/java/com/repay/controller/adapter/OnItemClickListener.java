package com.repay.controller.adapter;

/**
 * Simple item click listener interface for {@link android.support.v7.widget.RecyclerView}
 *
 * @author Matt Allen
 * @project Repay
 */
public interface OnItemClickListener<T>
{
	public void onItemClicked(T obj, int position);
}
