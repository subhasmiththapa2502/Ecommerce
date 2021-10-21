package com.example.ecommerce.utils;

import android.content.Context;


import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Pagination
 * Created by Subhasmith
 */
@GlideModule
public class PaginationGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultRequestOptions(
                new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
        );
    }
}
