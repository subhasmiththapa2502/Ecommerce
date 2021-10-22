package com.example.ecommerce.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.api.MovieApi;
import com.example.ecommerce.api.MovieService;
import com.example.ecommerce.database.CartItem;
import com.example.ecommerce.database.DatabaseClient;
import com.example.ecommerce.model.Result;
import com.example.ecommerce.utils.AppConstants;
import com.example.ecommerce.utils.CircleAnimationUtil;
import com.example.ecommerce.utils.Converter;
import com.example.ecommerce.utils.GlideApp;
import com.example.ecommerce.utils.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeepLinkingActivity extends AppCompatActivity {
    ImageView movie_poster;
    TextView movie_title, movie_desc;
    int id;
    int cost;
    int cart_count = 0;
    String imagePath;
    TextView addToCart, addToCartDummy;
    ProgressBar movie_progress;
    MenuItem menuItem;

    private MovieService movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_linking);

        movieService = MovieApi.getClient(this).create(MovieService.class);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (data != null){

            try {
                id = extractIdFromData(data);
                callMovieDetailsApi();
            } catch (Exception e) {
                Toast.makeText(this, "No Movies Found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }else{
            String title = getIntent().getStringExtra(AppConstants.TITLE);
            String desc = getIntent().getStringExtra(AppConstants.DESC);
            imagePath = getIntent().getStringExtra(AppConstants.IMAGE_PATH);
            id = getIntent().getIntExtra(AppConstants.ID, 0);
            cost = getIntent().getIntExtra(AppConstants.COST, 0);
            init(title);
            checkIfItemExistsInDb();
            showImage(imagePath);
            setMovie_title(title);
            setMovie_Desc(desc);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkForRTL(this);

    }
    private  Integer extractIdFromData(Uri data){
        return Integer.valueOf(data.toString().substring(data.toString().lastIndexOf("/")+1,data.toString().indexOf("-")));
    }

    public void showImage(String posterPath) {
        GlideApp
                .with(this)
                .load(posterPath)
                .centerCrop()
                .into(movie_poster);
    }

    private void makeFlyAnimation(TextView targetView) {

        ActionMenuItemView destView = (ActionMenuItemView) findViewById(R.id.cart);

        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(1000).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                cart_count = cart_count+1;
                menuItem.setIcon(Converter.convertLayoutToImage(DeepLinkingActivity.this, cart_count, R.drawable.ic_shopping_cart));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }


    public void setMovie_title(String title) {
        movie_title.setText(title);
    }

    public void setMovie_Desc(String desc) {
        movie_desc.setText(desc);
    }

    public void init(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout coll_toolbar = findViewById(R.id.collapsing_toolbar_layout);
        coll_toolbar.setTitle(title);

        coll_toolbar.setContentScrimColor(Color.WHITE);

        movie_progress = findViewById(R.id.movie_progress);
        movie_title = findViewById(R.id.movie_title);
        movie_desc = findViewById(R.id.movie_desc);


        movie_poster = findViewById(R.id.movie_poster);


        addToCart = findViewById(R.id.addToCart);
        addToCartDummy = findViewById(R.id.addToCartDummy);
        addToCartDummy.setVisibility(View.GONE);
        addToCart.setOnClickListener(view -> {
            if (addToCart.getText().toString().equalsIgnoreCase("Add to cart")) {
                Handler handler = new Handler();
                addToCartDummy.setVisibility(View.VISIBLE);
                movie_progress.setVisibility(View.VISIBLE);

                final Runnable runnable = new Runnable() {
                    public void run() {
                        movie_progress.setVisibility(View.GONE);
                        makeFlyAnimation(addToCartDummy);
                        saveTask();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }else{
                goToCart();
            }






        });

    }

    private void goToCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void checkIfItemExistsInDb() {
        class CheckTask extends AsyncTask<Boolean, Void, Boolean> {
            @Override
            protected void onPreExecute() {
                if (movie_progress != null){
                    movie_progress.setVisibility(View.VISIBLE);
                }
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Boolean... voids) {


                return DatabaseClient
                        .getInstance(getApplicationContext())
                        .getCartItemDataBase()
                        .cartItemDao()
                        .exists(id);
            }

            @Override
            protected void onPostExecute(Boolean exists) {
                if (movie_progress != null){
                    movie_progress.setVisibility(View.GONE);
                }
                super.onPostExecute(exists);
                if (exists) {
                    addToCart.setText(R.string.go_to_cart);
                }else {
                    addToCart.setText(R.string.add_to_cart);
                }

                //startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        }

        CheckTask st = new CheckTask();
        st.execute();
    }

    private void saveTask() {


        class SaveTask extends AsyncTask<Boolean, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Boolean... voids) {

                boolean exists = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getCartItemDataBase()
                        .cartItemDao()
                        .exists(id);
                if (exists) {

                } else {
                    //creating a task
                    CartItem task = new CartItem();
                    task.setTitle(movie_title.getText().toString());
                    task.setId(id);
                    task.setTotalCost(cost);
                    task.setCost(cost);
                    task.setUnitCost(cost);
                    task.setImagePath(imagePath);

                    //adding to database
                    DatabaseClient.getInstance(DeepLinkingActivity.this).getCartItemDataBase()
                            .cartItemDao()
                            .insertCartItem(task);
                }

                return exists;
            }

            @Override
            protected void onPostExecute(Boolean exists) {
                super.onPostExecute(exists);
                addToCart.setText(R.string.go_to_cart);

            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        menuItem = menu.findItem(R.id.cart);
        menuItem.setIcon(Converter.convertLayoutToImage(DeepLinkingActivity.this, cart_count, R.drawable.ic_shopping_cart));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.cart:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                break;


        }
        return true;
    }

    /**
     * Performs a Retrofit call with movie id to get the movie details.
     */
    private Call<Result> callMovieDetails() {
        return movieService.getMovieDetail(id,getString(R.string.my_api_key),
                "en_us"
        );
    }

    private void callMovieDetailsApi() {

        callMovieDetails().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                imagePath = AppConstants.BASE_URL_IMG+result.getBackdropPath();
                cost = result.getVoteCount();
                init(result.getTitle());
                checkIfItemExistsInDb();
                setUI(result.getTitle(), result.getOverview(), result.getVoteCount(),result.getPosterPath());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
                showSnackBar(t);

            }
        });
    }

    private void setUI(String title, String description, int price, String imgPath){
        setMovie_title(title);
        setMovie_Desc(description);
        showImage(AppConstants.BASE_URL_IMG_POSTER+imgPath);
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
                if (menuItem != null){
                    menuItem.setIcon(Converter.convertLayoutToImage(DeepLinkingActivity.this, cart_count, R.drawable.ic_shopping_cart));
                }
                super.onPostExecute(tasks);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void showSnackBar(Throwable t){
        Toast.makeText(this, fetchErrorMessage(t), Toast.LENGTH_SHORT).show();
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
}