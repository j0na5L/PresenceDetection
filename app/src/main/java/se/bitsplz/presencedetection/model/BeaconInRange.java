package se.bitsplz.presencedetection.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class BeaconInRange extends AbstractEntity {

    @SerializedName("major")
    private String major;
    @SerializedName("minor")
    private String minor;
    @SerializedName("rssi")
    private String rssi;
    @SerializedName("timestamp")
    private String timestamp;

    public BeaconInRange(String userId, String beaconUuid, String major, String minor, String rssi, String timestamp) {
        this.userId = userId;
        this.beaconUuid = beaconUuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }
}
