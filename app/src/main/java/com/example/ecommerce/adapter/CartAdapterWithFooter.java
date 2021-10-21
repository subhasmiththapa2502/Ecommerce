package com.example.ecommerce.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ecommerce.R;
import com.example.ecommerce.database.CartItem;
import com.example.ecommerce.model.Result;
import com.example.ecommerce.ui.CartActivity;
import com.example.ecommerce.utils.GlideApp;
import com.example.ecommerce.utils.GlideRequest;
import com.example.ecommerce.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Subhasmith Thapa on 21,October,2021
 */

/**
 * Created by Subhasmith on 19/10/16.
 */

public class CartAdapterWithFooter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private CartInterface cartInterface;

    private List<CartItem> cartItems;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    private FooterClickListener footerClickListener;
    public CartAdapterWithFooter(CartActivity activity) {
        this.context = activity;
        this.mCallback = (PaginationAdapterCallback) this.context;
        this.cartInterface = (CartInterface) context;
        this.footerClickListener = (FooterClickListener) context;
        cartItems = new ArrayList<>();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.cart_item, parent, false);
                viewHolder = new CartItemVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.cart_footer, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
/*            case HERO:
                View viewHero = inflater.inflate(R.layout.item_hero, parent, false);
                viewHolder = new HeroVH(viewHero);
                break;*/
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position); // Cart Item

        switch (getItemViewType(position)) {

            case ITEM:
                final CartItemVH movieVH = (CartItemVH) holder;


                movieVH.textViewStatus.setText(cartItem.getTitle());
                movieVH.textViewPrice.setText("₹" + cartItem.getTotalCost());
                movieVH.spinner.setSelection(getIndex(movieVH.spinner, String.valueOf(cartItem.getQuantity())));

                // load movie thumbnail
                loadImage(cartItem.getImagePath())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // TODO: 2/16/19 Handle failure
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // image ready, hide progress now
                                movieVH.mProgress.setVisibility(View.GONE);
                                return false;   // return false if you want Glide to handle everything else.
                            }
                        })
                        .into(movieVH.movie_poster);
                break;

            case LOADING:

                final LoadingVH loadingVH = (LoadingVH) holder;
                int totalPriceOfProducts=0;
                for(int i=0; i<cartItems.size(); i++){
                    totalPriceOfProducts += cartItems.get(i).getTotalCost();
                }
                loadingVH.orderValue.setText("₹" + totalPriceOfProducts);
                loadingVH.totalValue.setText("₹" + totalPriceOfProducts);

                break;
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == cartItems.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
        Helpers - bind Views
   _________________________________________________________________________________________________
    */

    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
     * <p>
     * //     * @param posterPath from {@link Result#getPosterPath()}
     *
     * @return Glide builder
     */

    private GlideRequest<Drawable> loadImage(@NonNull String posterPath) {
        return GlideApp
                .with(context)
                .load(posterPath)
                .centerCrop();
    }


    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(CartItem r) {
        cartItems.add(r);
        notifyItemInserted(cartItems.size() - 1);
    }

    public void addAll(List<CartItem> cartItems) {
        for (CartItem result : cartItems) {
            add(result);
        }
    }

    public void remove(CartItem r) {
        int position = cartItems.indexOf(r);
        if (position > -1) {
            cartItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new CartItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = cartItems.size() - 1;
        CartItem result = getItem(position);

        if (result != null) {
            cartItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public CartItem getItem(int position) {
        return cartItems.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(cartItems.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class CartItemVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewStatus, textViewPrice;
        ImageView delete, movie_poster;
        Spinner spinner;
        private ProgressBar mProgress;

        public CartItemVH(View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id.tvTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            delete = itemView.findViewById(R.id.delete);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            spinner = itemView.findViewById(R.id.spinner);
            mProgress = itemView.findViewById(R.id.movie_progress);

            //itemView.setOnClickListener(this);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.deleteItem(cartItems.get(getPosition()));
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Log.d("CARD ADAPTER", String.valueOf(position + 1));
                    int quantity = position + 1;
                    if (quantity == cartItems.get((getAdapterPosition())).getQuantity()) {
                        return;
                    }

                    cartInterface.changeQuantity(cartItems.get((getAdapterPosition())), quantity);


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

        @Override
        public void onClick(View view) {

        }
    }

    public interface CartInterface {
        void deleteItem(CartItem cartItem);

        void changeQuantity(CartItem cartItem, int quantity);
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView orderValue, totalValue;
        private Button proceedOrder;


        public LoadingVH(View itemView) {
            super(itemView);
            totalValue = itemView.findViewById(R.id.totalValue);
            orderValue = itemView.findViewById(R.id.orderValue);
            proceedOrder = itemView.findViewById(R.id.proceedOrder);

            proceedOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    footerClickListener.onProceedClicked();
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.proceedOrder:
                    showPopup(true);

                    break;
            }
        }

        private void showPopup(boolean b) {

        }
    }

    public interface FooterClickListener{
        void onProceedClicked();
    }
    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
}
