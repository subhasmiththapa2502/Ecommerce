package com.example.ecommerce.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.adapter.PaginationAdapterListActivity;
import com.example.ecommerce.api.MovieApi;
import com.example.ecommerce.api.MovieService;
import com.example.ecommerce.database.CartItem;
import com.example.ecommerce.database.DatabaseClient;
import com.example.ecommerce.model.Result;
import com.example.ecommerce.model.TopRatedMovies;
import com.example.ecommerce.utils.Converter;
import com.example.ecommerce.utils.PaginationAdapterCallback;
import com.example.ecommerce.utils.PaginationScrollListener;
import com.example.ecommerce.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private static final String TAG = ListActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    // limiting to 10 for this demo, since total pages in actual API is very large. Feel free to modify.
    private static final int TOTAL_PAGES = 10;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    private MovieService movieService;
    Button btnRetry;
    ProgressBar progressBar;
    LinearLayout errorLayout;

    TextView txtError;
    PaginationAdapterListActivity paginationAdapterListActivity;
    private int cart_count=0;
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkForRTL(this);
    }

    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        Utils.changeStatusBarColour(this);
        rv = findViewById(R.id.main_recycler);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);

        //init service and load data
        movieService = MovieApi.getClient(this).create(MovieService.class);

        loadFirstPage();

        btnRetry.setOnClickListener(view -> {
            loadFirstPage();
        });

        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);
        setUpTopRatedRecyclerView();
    }
    /**
     * Triggers the actual background refresh via the {@link SwipeRefreshLayout}
     */
    private void doRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        if (callTopRatedMoviesApi().isExecuted())
            callTopRatedMoviesApi().cancel();


        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        paginationAdapterListActivity.getMovies().clear();
        paginationAdapterListActivity.notifyDataSetChanged();
        loadFirstPage();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();
        currentPage = PAGE_START;

        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                hideErrorView();

//                Log.i(TAG, "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                // Got data. Send it to adapter
                List<Result> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                paginationAdapterListActivity.addAll(results);

                if (currentPage <= TOTAL_PAGES) paginationAdapterListActivity.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }
    private void setUpTopRatedRecyclerView(){
        paginationAdapterListActivity = new PaginationAdapterListActivity(ListActivity.this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(paginationAdapterListActivity);

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

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
//                Log.i(TAG, "onResponse: " + currentPage
//                        + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                paginationAdapterListActivity.removeLoadingFooter();
                isLoading = false;

                List<Result> results = fetchResults(response);
                paginationAdapterListActivity.addAll(results);

                if (currentPage != TOTAL_PAGES) paginationAdapterListActivity.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                paginationAdapterListActivity.showRetry(true, fetchErrorMessage(t));
            }
        });
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
    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieService.getUpComingMovies(
                getString(R.string.my_api_key),
                "en_US",
                currentPage
        );
    }

    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }
    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void retryPageLoad() {

    }
    @Override
    protected void onStart() {
        super.onStart();
        getCartCount();
    }
    private void getCartCount() {
        class GetTasks extends AsyncTask<Void, Void, List<CartItem>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<CartItem> doInBackground(Void... voids) {

                List<CartItem> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getCartItemDataBase()
                        .cartItemDao()
                        .getCartItems();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<CartItem> tasks) {
                if (tasks.isEmpty()){
                    cart_count = 0;
                }else{
                    cart_count = tasks.size();
                }
                if (menuItem!= null){
                    menuItem.setIcon(Converter.convertLayoutToImage(ListActivity.this, cart_count, R.drawable.ic_shopping_cart));
                }
                super.onPostExecute(tasks);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        menuItem = menu.findItem(R.id.cart);
        menuItem.setIcon(Converter.convertLayoutToImage(ListActivity.this, cart_count, R.drawable.ic_shopping_cart));

        return true;
    }

}