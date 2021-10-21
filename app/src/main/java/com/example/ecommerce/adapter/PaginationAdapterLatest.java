package com.example.ecommerce.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ecommerce.R;
import com.example.ecommerce.model.Result;
import com.example.ecommerce.ui.DetailActivity;
import com.example.ecommerce.ui.HomeFragment;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.GlideApp;
import com.example.ecommerce.utils.GlideRequest;
import com.example.ecommerce.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */


public class PaginationAdapterLatest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;



    private List<Result> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapterLatest(HomeFragment context) {
        this.context = context.getContext();
        this.mCallback = (PaginationAdapterCallback) this.context;
        movieResults = new ArrayList<>();
    }

    public List<Result> getMovies() {
        return movieResults;
    }

    public void setMovies(List<Result> movieResults) {
        this.movieResults = movieResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_list, parent, false);
                viewHolder = new MovieVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
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
        Result result = movieResults.get(position); // Movie

        switch (getItemViewType(position)) {

/*
            case HERO:
                final HeroVH heroVh = (HeroVH) holder;

                heroVh.mMovieTitle.setText(result.getTitle());
                heroVh.mYear.setText(formatYearLabel(result));
                heroVh.mMovieDesc.setText(result.getOverview());

                loadImage(result.getBackdropPath())
                        .into(heroVh.mPosterImg);
                break;
*/

            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.mMovieTitle.setText(result.getTitle());
                movieVH.mYear.setText(formatYearLabel(result));
                movieVH.mMovieDesc.setText(result.getOverview());

                // load movie thumbnail
                loadImage(result.getPosterPath())
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
                        .into(movieVH.mPosterImg);

                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if (position == 0) {
            return HERO;
        } else {
            return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }*/
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
        Helpers - bind Views
   _________________________________________________________________________________________________
    */

    /**
     * @param result
     * @return [releasedate] | [2letterlangcode]
     */
    private String formatYearLabel(Result result) {
        String str ="";
        try {
            str = result.getReleaseDate().substring(0, 4)  // we want the year only
                    + " | "
                    + result.getOriginalLanguage().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
     * <p>
     * //     * @param posterPath from {@link Result#getPosterPath()}
     *
     * @return Glide builder
     */
//    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
//        return Glide
//                .with(context)
//                .load(BASE_URL_IMG + posterPath)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
//                .centerCrop()
//                .crossFade();

//    }
    private GlideRequest<Drawable> loadImage(@NonNull String posterPath) {
        return GlideApp
                .with(context)
                .load(AppConstants.BASE_URL_IMG + posterPath)
                .centerCrop();
    }


    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Result r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Result> moveResults) {
        for (Result result : moveResults) {
            add(result);
        }
    }

    public void remove(Result r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
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
        add(new Result());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Result result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Result getItem(int position) {
        return movieResults.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(movieResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Header ViewHolder
     */
    protected class HeroVH extends RecyclerView.ViewHolder {
        private TextView mMovieTitle;
        private TextView mMovieDesc;
        private TextView mYear; // displays "year | language"
        private ImageView mPosterImg;

        public HeroVH(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieDesc = itemView.findViewById(R.id.movie_desc);
            mYear = itemView.findViewById(R.id.movie_year);
            mPosterImg = itemView.findViewById(R.id.movie_poster);
        }
    }

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mMovieTitle;
        private TextView mMovieDesc;
        private TextView mYear; // displays "year | language"
        private ImageView mPosterImg;
        private ProgressBar mProgress;

        public MovieVH(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieDesc = itemView.findViewById(R.id.movie_desc);
            mYear = itemView.findViewById(R.id.movie_year);
            mPosterImg = itemView.findViewById(R.id.movie_poster);
            mProgress = itemView.findViewById(R.id.movie_progress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, DetailActivity.class);
// Pass data object in the bundle and populate details activity.
            intent.putExtra(AppConstants.IMAGE_PATH, AppConstants.BASE_URL_IMG_POSTER+movieResults
                    .get(getPosition()).getBackdropPath());
            intent.putExtra(AppConstants.TITLE, movieResults
                    .get(getPosition()).getTitle());
            intent.putExtra(AppConstants.DESC, movieResults
                    .get(getPosition()).getOverview());
            intent.putExtra(AppConstants.COST, movieResults
                    .get(getPosition()).getVoteCount());

            Pair<View, String> p1 = Pair.create(mPosterImg, "item");
            Pair<View, String> p2 = Pair.create(mMovieTitle, "text");
            Pair<View, String> p3 = Pair.create((View)mMovieDesc, "desc");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
            /*ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, (View)mPosterImg, "item");*/
            context.startActivity(intent, options.toBundle());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}
