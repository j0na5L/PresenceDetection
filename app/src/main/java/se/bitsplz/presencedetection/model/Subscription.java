package se.bitsplz.presencedetection.model;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class Subscription extends AbstractEntity {


    public Subscription(String userId, String beaconUuid) {
        this.userId = userId;
        this.beaconUuid = beaconUuid;
    }
}
