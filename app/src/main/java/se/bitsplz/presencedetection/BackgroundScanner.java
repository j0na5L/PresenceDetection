package se.bitsplz.presencedetection;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import se.bitsplz.presencedetection.activity.SubscribedBeaconActivity;
import se.bitsplz.presencedetection.model.SubscribedBeacon;

/**
 * Created by jonnakollin on 19/08/16.
 */
public class BackgroundScanner extends Application implements BootstrapNotifier {

    private static final String TAG = BackgroundScanner.class.getSimpleName();

    private BeaconManager beaconManager;
    private BackgroundPowerSaver backgroundPowerSaver;
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();

        backgroundPowerSaver = new BackgroundPowerSaver(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundBetweenScanPeriod(15000L);
        beaconManager.setBackgroundScanPeriod(1100L);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        regionBootstrap = new RegionBootstrap(this, new Region("closeBeacons", null, null, null));

    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Did enter region");

        Intent intent = new Intent(this, SubscribedBeaconActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "Got a didExitRegion call");

        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
