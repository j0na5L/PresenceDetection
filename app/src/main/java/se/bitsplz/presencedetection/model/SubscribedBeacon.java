package se.bitsplz.presencedetection.model;

import org.altbeacon.beacon.Beacon;

/**
 * @author jonnakollin
 * @author jonaslydmark
 */
public final class SubscribedBeacon {

    private Beacon beacon;
    private String aliasName;
    private Long beaconInRangeTime;
    private Long beaconOutOfRangeTime;
    private Long beaconSubDate;

    public SubscribedBeacon(String aliasName, Beacon beacon, Long beaconSubDate, Long beaconInRangeTime, Long beaconOutOfRangeTime) {
        this.aliasName = aliasName;
        this.beacon = beacon;
        this.beaconSubDate = beaconSubDate;
        this.beaconInRangeTime = beaconInRangeTime;
        this.beaconOutOfRangeTime = beaconOutOfRangeTime;
    }

    public void setBeaconOutOfRangeTime(Long beaconOutOfRangeTime) {
        this.beaconOutOfRangeTime = beaconOutOfRangeTime;
    }

    public void setbeaconInRangeTime(Long beaconInRangeTime) {
        this.beaconInRangeTime = beaconInRangeTime;
    }

    public Long getBeaconInRangeTime() {
        return beaconInRangeTime;
    }

    public Long getBeaconSubDate() {
        return beaconSubDate;
    }

    public Long getBeaconOutOfRangeTime() {
        return beaconOutOfRangeTime;
    }

    public String getAliasName() {
        return aliasName;
    }

    public Beacon getBeacon() {
        return beacon;
    }
}
