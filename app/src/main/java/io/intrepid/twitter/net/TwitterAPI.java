package io.intrepid.twitter.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TwitterAPI {
    @GET("statuses/user_timeline.json")
    Call<List<Tweet>> tweets(@Query("screen_name") String handle, @Query("count") int count);
}
