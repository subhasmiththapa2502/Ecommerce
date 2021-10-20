package com.example.ecommerce.ui.viewholder;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private int mCurrentPosition;
    public BaseViewHolder(View itemView) {
        super(itemView);
    }
    protected abstract void clear();
    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }
    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
