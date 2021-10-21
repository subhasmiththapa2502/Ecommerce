package com.example.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.GlideApp;

import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

	// Context object
	Context context;
	
	// Array of images
	String[] images;
	
	// Layout Inflater
	LayoutInflater mLayoutInflater;


	// Viewpager Constructor
	public ViewPagerAdapter(Context context, String[] images) {
		this.context = context;
		this.images = images;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// return the number of images
		return images.length;
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == object;
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, final int position) {
		// inflating the item.xml
		View itemView = mLayoutInflater.inflate(R.layout.slider_image_item, container, false);

		// referencing the image view from the item.xml file
		ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);
		
		// setting the image in the imageView
		GlideApp
				.with(context)
				.load(AppConstants.BASE_URL_IMG_POSTER + images[position])
				.centerCrop()
				.into(imageView);
		// Adding the View
		Objects.requireNonNull(container).addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		container.removeView((LinearLayoutCompat) object);
	}
}
