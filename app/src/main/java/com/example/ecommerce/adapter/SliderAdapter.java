package com.example.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerce.R;
import com.example.ecommerce.model.SliderItems;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.GlideApp;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<String> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;

    public SliderAdapter(Context context, List<String> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slider_image_item, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
        if (position == sliderItems.size()- 2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMain);
        }

        void setImage(String sliderItems){

//use glide or picasso in case you get image from internet
            //imageView.setImageResource(sliderItems.getImage());

            // setting the image in the imageView
            GlideApp
                    .with(context)
                    .load(AppConstants.BASE_URL_IMG_POSTER + sliderItems)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

}