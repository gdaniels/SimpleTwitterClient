package io.intrepid.twitter.net;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

public class TwitterService {
    public interface Callback {
        void tweetsReceived(List<Tweet> body);
    }

    TwitterAPI api;
    Callback callback;

    public TwitterService(TwitterAPI api, Callback callback) {
        this.api = api;
        this.callback = callback;
    }

    public void requestTweets(String username, int numTweets) {
        api.tweets(username, numTweets).enqueue(new retrofit2.Callback<List<Tweet>>() {
            @Override
            public void onResponse(Response<List<Tweet>> response) {
                if (response.isSuccess()) {
                    List<Tweet> responseBody = response.body();
                    Timber.d("Got %d tweets!", responseBody.size());
                    if (callback != null) {
                        callback.tweetsReceived(responseBody);
                    }
                    return;
                }
                try {
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        Timber.e(errorBody.string());
                    }
                } catch (IOException e) {
                    Timber.e(e, "Caught IOException");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Failure");
            }
        });
    }
}
