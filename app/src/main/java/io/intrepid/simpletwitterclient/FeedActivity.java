package io.intrepid.simpletwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.twitter.net.Tweet;
import io.intrepid.twitter.net.TwitterService;
import io.intrepid.twitter.net.TwitterAPI;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.MoshiConverterFactory;
import retrofit2.Retrofit;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;


public class FeedActivity extends AppCompatActivity implements TwitterService.Callback {
    private static final String CONSUMER_KEY = "u9HWMWEDYbfpS7ZZ8RRstQwHa";
    private static final String CONSUMER_SECRET = "e1objLPsBsZcofeFlgacWrU7j459j6yyTZ4OwfHV8kn6dtSrBx";
    private static final String URL = "https://api.twitter.com/1.1/";
    private TwitterService twitterService;
    List<Tweet> tweets = new ArrayList<>();

    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.inject(this);

        setupRetrofit();

        adapter = new RecyclerView.Adapter() {
            class ViewHolder extends RecyclerView.ViewHolder {
                public ViewHolder(View itemView) {
                    super(itemView);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView itemView = new TextView(parent.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                 ViewGroup.LayoutParams.WRAP_CONTENT);
                int size = getResources().getDimensionPixelSize(R.dimen.tweet_text_margin);
                params.setMargins(size, size, size, size);
                itemView.setLayoutParams(params);
                return new ViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView)holder.itemView).setText(tweets.get(position).text);
            }

            @Override
            public int getItemCount() {
                return tweets.size();
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        requestTweets();
    }

    private void setupRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL);
        builder.addConverterFactory(MoshiConverterFactory.create());
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        SigningInterceptor signingInterceptor = new SigningInterceptor(consumer);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(signingInterceptor)
                .build();
        builder.client(okHttpClient);

        Retrofit retrofit = builder.build();

        twitterService = new TwitterService(retrofit.create(TwitterAPI.class), this);
    }

    private void requestTweets() {
        twitterService.requestTweets("rrridges", 20);
    }

    @Override
    public void tweetsReceived(List<Tweet> body) {
        tweets = body;
        adapter.notifyDataSetChanged();
    }
}
