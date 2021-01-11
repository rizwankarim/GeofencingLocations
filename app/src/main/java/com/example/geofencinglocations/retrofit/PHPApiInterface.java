package com.example.geofencinglocations.retrofit;


import com.example.geofencinglocations.models.locResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PHPApiInterface {

    @POST("create_history.php")
    Call<locResponse> saveLocation(
            @Query("userId") String userId,
            @Query("placeLatitude") String placeLatitude,
            @Query("placeLongitude") String placeLongitude,
            @Query("placeAddress") String placeAddress,
            @Query("placeName") String placeName,
            @Query("placeType") String placeType,
            @Query("visitStatus") String visitStatus,
            @Query("placeTime") String placeTime
    );



}
