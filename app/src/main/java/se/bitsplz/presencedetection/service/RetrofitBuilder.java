package se.bitsplz.presencedetection.service;

import retrofit2.Retrofit;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class RetrofitBuilder {

    public static final String BASE_URL = "http://beacons.zenzor.io/sys/api/";

    private PresenceDetectionService service;

    public RetrofitBuilder() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new StringConverterFactory())
                .build();

        service = retrofit.create(PresenceDetectionService.class);
    }

    public PresenceDetectionService getPresenceDetectionService() {
        return service;
    }
}
