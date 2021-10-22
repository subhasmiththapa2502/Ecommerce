package com.example.ecommerce.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerce.R;
import com.example.ecommerce.adapter.PaginationAdapter;

import com.example.ecommerce.adapter.PaginationAdapterLatest;
import com.example.ecommerce.adapter.PaginationAdapterNowPlaying;
import com.example.ecommerce.adapter.SliderAdapter;
import com.example.ecommerce.api.MovieApi;
import com.example.ecommerce.api.MovieService;
import com.example.ecommerce.model.LatestMovies;
import com.example.ecommerce.model.NowPlaying;
import com.example.ecommerce.model.Result;
import com.example.ecommerce.model.TopRatedMovies;
import com.example.ecommerce.utils.PaginationAdapterCallback;
import com.example.ecommerce.utils.PaginationScrollListener;
import com.example.ecommerce.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */
public class HomeFragment extends Fragment implements PaginationAdapterCallback, View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    PaginationAdapter adapter;
    PaginationAdapterNowPlaying adapterNowPlaying;
    PaginationAdapterLatest adapterLatest;
    LinearLayoutManager linearLayoutManager, linearLayoutManagerNowPlaying, linearLayoutManagerLatest;

    RecyclerView rv, main_recycler_now_playing, main_recycler_latest;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView nowPlayingSeeAll;
    TextView mostPopularSeeAll;
    TextView latestMoviesSeeAll;

    // limiting to 10 for this demo, since total pages in actual API is very large. Feel free to modify.
    private static final int TOTAL_PAGES = 200;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;


    //FOR NOW PLAYING
    private static final int PAGE_START_NOW_PLAYING = 20;
    private int currentPageNowPlaying = PAGE_START_NOW_PLAYING;
    private boolean isLoadingNowPlaying = false;
    private boolean isLastPageNowPlaying = false;

    //FOR LATEST
    private static final int PAGE_START_LATEST = 100;
    private int currentPageLatest = PAGE_START_LATEST;
    private boolean isLoadingLatest = false;
    private boolean isLastPageLatest = false;


    private MovieService movieService;

    // images array
    List<String> images = new ArrayList<>();
    private ViewPager2 viewPager2;
    private final Handler sliderHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        ButterKnife.bind(requireActivity());
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        init();
        Utils.checkForRTL(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);

    }

    public void init() {
        rv = requireActivity().findViewById(R.id.main_recycler);
        main_recycler_now_playing = requireActivity().findViewById(R.id.main_recycler_now_playing);
        main_recycler_latest = requireActivity().findViewById(R.id.main_recycler_latest);
        progressBar = requireActivity().findViewById(R.id.main_progress);
        errorLayout = requireActivity().findViewById(R.id.error_layout);
        btnRetry = requireActivity().findViewById(R.id.error_btn_retry);
        txtError = requireActivity().findViewById(R.id.error_txt_cause);
        swipeRefreshLayout = requireActivity().findViewById(R.id.main_swiperefresh);

        viewPager2 = requireActivity().findViewById(R.id.viewPagerMain);

        nowPlayingSeeAll = requireActivity().findViewById(R.id.nowPlayingSeeAll);
        mostPopularSeeAll = requireActivity().findViewById(R.id.mostPopularSeeAll);
        latestMoviesSeeAll = requireActivity().findViewById(R.id.latestMoviesSeeAll);

        nowPlayingSeeAll.setOnClickListener(this);
        mostPopularSeeAll.setOnClickListener(this);
        latestMoviesSeeAll.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManagerNowPlaying = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManagerLatest = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);

        //init service and load data
        movieService = MovieApi.getClient(requireActivity()).create(MovieService.class);

        loadFirstPage();
        callNowPlaying();
        callLatestApi();

        btnRetry.setOnClickListener(view -> {
            loadFirstPage();
            callNowPlaying();
            callLatestApi();
        });

        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);

        setUpViewPager();
        setUpTopRatedRecyclerView();
        setUpNowPlayingRecyclerView();
        setUpLatestRecyclerView();
    }

    private void setUpViewPager() {
        images.add("/70nxSw3mFBsGmtkvcs91PbjerwD.jpg");
        images.add("/8Y43POKjjKDGI9MH89NW0NAzzp8.jpg");
        images.add("/mDTrJbn2YEzYrBsdu7ITKh3ef69.jpg");
        // Initializing the ViewPagerAdapter
        viewPager2.setAdapter(new SliderAdapter(requireActivity(), images, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        compositePageTransformer.addTransformer((page, position) -> {
            float r = Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000); // slide duration 2 seconds
            }
        });


    }

    private void setUpTopRatedRecyclerView() {
        adapter = new PaginationAdapter(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    private void setUpLatestRecyclerView() {
        adapterLatest = new PaginationAdapterLatest(this);
        main_recycler_latest.setLayoutManager(linearLayoutManagerLatest);
        main_recycler_latest.setItemAnimator(new DefaultItemAnimator());
        main_recycler_latest.setAdapter(adapterNowPlaying);

        main_recycler_latest.addOnScrollListener(new PaginationScrollListener(linearLayoutManagerLatest) {
            @Override
            protected void loadMoreItems() {
                isLoadingLatest = true;
                currentPageLatest += 1;

                loadNextPageLatest();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPageLatest;
            }

            @Override
            public boolean isLoading() {
                return isLoadingLatest;
            }
        });


    }

    private void setUpNowPlayingRecyclerView() {
        adapterNowPlaying = new PaginationAdapterNowPlaying(this);
        main_recycler_now_playing.setLayoutManager(linearLayoutManagerNowPlaying);
        main_recycler_now_playing.setItemAnimator(new DefaultItemAnimator());
        main_recycler_now_playing.setAdapter(adapterNowPlaying);

        main_recycler_now_playing.addOnScrollListener(new PaginationScrollListener(linearLayoutManagerNowPlaying) {
            @Override
            protected void loadMoreItems() {
                isLoadingNowPlaying = true;
                currentPageNowPlaying += 1;

                loadNextPageNowPlaying();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPageNowPlaying;
            }

            @Override
            public boolean isLoading() {
                return isLoadingNowPlaying;
            }
        });


    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    /**
     * Triggers the actual background refresh via the {@link SwipeRefreshLayout}
     */
    private void doRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        if (callUpComingMovies().isExecuted())
            callUpComingMovies().cancel();

        if (callNowPlayingMoviesApi().isExecuted())
            callNowPlayingMoviesApi().cancel();

        if (callLatest().isExecuted())
            callLatest().cancel();

        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        adapter.getMovies().clear();
        adapter.notifyDataSetChanged();

        adapterLatest.getMovies().clear();
        adapterLatest.notifyDataSetChanged();

        adapterNowPlaying.getMovies().clear();
        adapterNowPlaying.notifyDataSetChanged();

        loadFirstPage();
        callNowPlaying();
        callLatestApi();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void callLatestApi() {
        currentPageLatest = PAGE_START_LATEST;

        callLatest().enqueue(new Callback<LatestMovies>() {
            @Override
            public void onResponse(Call<LatestMovies> call, Response<LatestMovies> response) {
                hideErrorView();
                List<Result> results = fetchResultsLatest(response);
                progressBar.setVisibility(View.GONE);
                adapterLatest.addAll(results);

                if (currentPageLatest < TOTAL_PAGES) adapterLatest.addLoadingFooter();
                else isLastPageLatest = true;
            }

            @Override
            public void onFailure(Call<LatestMovies> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
                showSnackBar(t);
            }
        });
    }

    private void callNowPlaying() {
        currentPageNowPlaying = PAGE_START_NOW_PLAYING;
        callNowPlayingMoviesApi().enqueue(new Callback<NowPlaying>() {
            @Override
            public void onResponse(Call<NowPlaying> call, Response<NowPlaying> response) {
                hideErrorView();
                List<Result> results = fetchResultsNowPlaying(response);
                progressBar.setVisibility(View.GONE);
                adapterNowPlaying.addAll(results);

                if (currentPageNowPlaying < TOTAL_PAGES) adapterNowPlaying.addLoadingFooter();
                else isLastPageNowPlaying = true;
            }

            @Override
            public void onFailure(Call<NowPlaying> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
                showSnackBar(t);
            }
        });

    }

    private void showSnackBar(Throwable t){
        FrameLayout frameLayout = requireActivity().findViewById(R.id.frameLL);
        Utils.showSnackBar(frameLayout,fetchErrorMessage(t));
    }
    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();
        currentPage = PAGE_START;

        callUpComingMovies().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                hideErrorView();

//                Log.i(TAG, "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                // Got data. Send it to adapter
                List<Result> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
                showSnackBar(t);
            }
        });
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<TopRatedMovies> callUpComingMovies() {
        return movieService.getUpComingMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<NowPlaying> callNowPlayingMoviesApi() {
        return movieService.getNowPlaying(getString(R.string.my_api_key),
                "en_us",
                currentPageNowPlaying
        );
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<LatestMovies> callLatest() {
        return movieService.getLatest(getString(R.string.my_api_key),
                "en_us",
                currentPageLatest
        );
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void retryPageLoad() {

    }

    private void loadNextPageNowPlaying() {
        Log.d(TAG, "loadNextPage: " + currentPageNowPlaying);
        callNowPlayingMoviesApi().enqueue(new Callback<NowPlaying>() {
            @Override
            public void onResponse(Call<NowPlaying> call, Response<NowPlaying> response) {
                adapterNowPlaying.removeLoadingFooter();
                isLoadingNowPlaying = false;

                List<Result> results = fetchResultsNowPlaying(response);
                adapterNowPlaying.addAll(results);

                if (currentPageNowPlaying != TOTAL_PAGES) adapterNowPlaying.addLoadingFooter();
                else isLastPageNowPlaying = true;

            }

            @Override
            public void onFailure(Call<NowPlaying> call, Throwable t) {
                t.printStackTrace();
                showSnackBar(t);
                adapterNowPlaying.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private void loadNextPageLatest() {
        Log.d(TAG, "loadNextPage: " + currentPageLatest);
        callLatest().enqueue(new Callback<LatestMovies>() {
            @Override
            public void onResponse(Call<LatestMovies> call, Response<LatestMovies> response) {
                adapterLatest.removeLoadingFooter();
                isLoadingLatest = false;

                List<Result> results = fetchResultsLatest(response);
                adapterLatest.addAll(results);

                if (currentPageLatest != TOTAL_PAGES) adapterLatest.addLoadingFooter();
                else isLastPageLatest = true;
            }

            @Override
            public void onFailure(Call<LatestMovies> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
                showSnackBar(t);
            }
        });
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callUpComingMovies().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
//                Log.i(TAG, "onResponse: " + currentPage
//                        + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                adapter.removeLoadingFooter();
                isLoading = false;

                List<Result> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
                showSnackBar(t);
            }
        });
    }

    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResultsLatest(Response<LatestMovies> response) {
        LatestMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResultsNowPlaying(Response<NowPlaying> response) {
        NowPlaying nowPlaying = response.body();
        return nowPlaying.getResults();
    }

    public void openListingActivity() {
        Intent intent = new Intent(requireActivity(), ListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        AppCompatTextView b = (AppCompatTextView) view;
        switch (b.getId()) {
            case R.id.nowPlayingSeeAll:
            case R.id.mostPopularSeeAll:
            case R.id.latestMoviesSeeAll:
                //OPEN LIST PAGE
                openListingActivity();

                break;
        }
    }
}
