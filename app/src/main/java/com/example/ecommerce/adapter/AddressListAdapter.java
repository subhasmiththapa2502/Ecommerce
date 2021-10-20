package com.example.ecommerce.adapter;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

import java.util.ArrayList;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ItemHolder> {

    private List<String> itemsName;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public AddressListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        itemsName = new ArrayList<String>();
    }

    @Override
    public AddressListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.address_list_item, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(AddressListAdapter.ItemHolder holder, int position) {
        holder.setItemName(itemsName.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsName.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(ItemHolder item, int position);
    }

    public void add(int location, String iName) {
        itemsName.add(location, iName);
        notifyItemInserted(location);
    }

    public void remove(int location) {
        if (location >= itemsName.size())
            return;

        itemsName.remove(location);
        notifyItemRemoved(location);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AddressListAdapter parent;
        TextView textItemName;

        public ItemHolder(View itemView, AddressListAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            textItemName = (TextView) itemView.findViewById(R.id.locationText);
        }

        public void setItemName(CharSequence name) {
            textItemName.setText(name);
        }

        public CharSequence getItemName() {
            return textItemName.getText();
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getPosition());
            }
        }
    }
}