package se.bitsplz.presencedetection.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public interface PresenceDetectionService {
    
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("register_user")
    Call<String> registerUser(@Body String input);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("subscribe_beacon")
    Call<String> subscribeBeacon(@Body String input);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("beacon_nearby")
    Call<String> setBeaconInRangeMode(@Body String input);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("beacon_outofrange")
    Call<String> setBeaconOutOfRangeMode(@Body String input);
}
