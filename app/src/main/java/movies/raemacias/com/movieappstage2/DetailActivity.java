package movies.raemacias.com.movieappstage2;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import movies.raemacias.com.movieappstage1.BuildConfig;
import movies.raemacias.com.movieappstage1.R;
import movies.raemacias.com.movieappstage2.adapter.MoviesAdapter;
import movies.raemacias.com.movieappstage2.adapter.ReviewAdapter;
import movies.raemacias.com.movieappstage2.adapter.TrailerAdapter;
import movies.raemacias.com.movieappstage2.api.Client;
import movies.raemacias.com.movieappstage2.api.MovieInterface;
import movies.raemacias.com.movieappstage2.model.MovieModel;
import movies.raemacias.com.movieappstage2.model.Result;
import movies.raemacias.com.movieappstage2.model.ReviewModel;
import movies.raemacias.com.movieappstage2.model.ReviewResult;
import movies.raemacias.com.movieappstage2.model.TrailerModel;
import movies.raemacias.com.movieappstage2.model.TrailerResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;

import static movies.raemacias.com.movieappstage2.api.MovieInterface.API_KEY;

public class DetailActivity extends AppCompatActivity {

    private List<Result> results;

    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<TrailerResult> trailerResult;

    private List<ReviewResult> mResults;
    private ReviewAdapter mReviewAdapter;

    private TextView textViewOriginalTitle;
    private TextView textViewVoteAverage;
    private TextView textViewPlotSynopsis;
    private TextView textViewReleaseDate;
    private ImageView imageViewMovieListItem;
    private ImageView imageViewMovieThumb;

    int movie_id;
    String review;
    String author;

    private final AppCompatActivity activity = DetailActivity.this;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textViewOriginalTitle = findViewById(R.id.original_title_tv);
        imageViewMovieListItem = findViewById(R.id.movie_poster_iv);
        imageViewMovieThumb = findViewById(R.id.movie_thumb_iv);
        textViewVoteAverage = findViewById(R.id.vote_average_tv);
        textViewPlotSynopsis = findViewById(R.id.plot_synopsis_tv);
        textViewReleaseDate = findViewById(R.id.release_tv);

        Intent intent = getIntent();
        if (intent.hasExtra("original_title")) {

            String poster = getIntent().getExtras().getString("poster_path");
            String movieTitle = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String release = getIntent().getExtras().getString("release_date");
            movie_id = getIntent().getExtras().getInt("movie_id");
            review = getIntent().getExtras().getString("review");
            author = getIntent().getExtras().getString("author");



            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w342" + poster)
                    .placeholder(R.drawable.popcorn)
                    .into(imageViewMovieThumb);

            textViewOriginalTitle.setText(movieTitle);
            textViewVoteAverage.setText(rating);
            textViewPlotSynopsis.setText(synopsis);
            textViewReleaseDate.setText(release);
        } else {
            Toast.makeText(this, "Information not available.", Toast.LENGTH_SHORT).show();
        }
        initViews();
        loadJSON();
        initViews1();
        loadJSON1();
    }

    private void initViews() {

    }

    private void loadJSON() {

        trailerResult = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerResult);

        recyclerView = findViewById(R.id.recyclerview_trailer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        try {

            Client Client = new Client();
            MovieInterface movieInterface = Client.getClient().create(MovieInterface.class);
            Call<TrailerModel> call = movieInterface.getMovieTrailer(movie_id, BuildConfig.API_KEY);
            call.enqueue(new Callback<TrailerModel>() {

                @Override
                public void onResponse(Call<TrailerModel> call, Response<TrailerModel> response) {
                    if (response.message().contentEquals("OK")) {
                        List<TrailerResult> results = response.body().getResults();
                        recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), results));
                        recyclerView.smoothScrollToPosition(0);
                    }
                }

                @Override
                public void onFailure(Call<TrailerModel> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews1() {

    }

    private void loadJSON1() {

        mResults = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(this, mResults);

        recyclerView = findViewById(R.id.recyclerview_reviews);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        try {

            Client Client = new Client();
            MovieInterface movieInterface = Client.getClient().create(MovieInterface.class);
            Call<ReviewModel> call = movieInterface.getContent(movie_id, BuildConfig.API_KEY);
            call.enqueue(new Callback<ReviewModel>() {

                @Override
                public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                    if (response.message().contentEquals("OK")) {

                        List<ReviewResult> mResults = response.body().getReviewList();
                        recyclerView.setAdapter(new ReviewAdapter(getApplicationContext(), mResults));
                        recyclerView.smoothScrollToPosition(0);
                    }
                }

                @Override
                public void onFailure(Call<ReviewModel> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Log.d(LOG_TAG, "Preferences updated.");
//        checkSortOrder();


//    }


