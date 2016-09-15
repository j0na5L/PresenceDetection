package se.bitsplz.presencedetection.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class BeaconOutOfRange extends AbstractEntity {

    @SerializedName("timestamp")
    private String timestamp;

    public BeaconOutOfRange(String userId, String beaconUuid, String timestamp) {
        this.userId = userId;
        this.beaconUuid = beaconUuid;
        this.timestamp = timestamp;
    }
}
