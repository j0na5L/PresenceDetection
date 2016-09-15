package se.bitsplz.presencedetection.activity;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.dialog.RegisterUserDialog;
import se.bitsplz.presencedetection.service.Storage;
import se.bitsplz.presencedetection.adapter.BeaconAdapter;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public class DeviceScanActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String USER_ID = "se.bitsplz.presencedetection.USER_ID";

    public static Context appContext;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BottomBar bottomBar;

    private DialogFragment registerUserDialog;
    private BeaconAdapter beaconAdapter;

    private List<Beacon> beacons;
    private BeaconManager beaconManager;
    private boolean sorting = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        beacons = new ArrayList<>();
        appContext = getApplicationContext();

        String userIsRegistered = Storage.readString(appContext, USER_ID, "");
        Log.d("userId, user in reg: ", userIsRegistered);

        if (userIsRegistered.isEmpty()) {
            showRegistrationDialog();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_scan_beacon);
        layoutManager = new LinearLayoutManager(this);
        beaconAdapter = new BeaconAdapter(getAppContext(), beacons);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(beaconAdapter);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        bottomBar = new BottomBar(this);
        bottomBar = BottomBar.attach(this, savedInstanceState);
        createBottomBar();
    }

    private void showRegistrationDialog() {
        registerUserDialog = new RegisterUserDialog();
        registerUserDialog.show(getFragmentManager(), "RegisterUserDialog");
    }

    private void createBottomBar() {
        bottomBar.setItemsFromMenu(R.menu.bottom_bar_menu_main, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(@IdRes int menuItemId) {
                Intent intent;

                switch (menuItemId) {
                    case R.id.my_subscribed_beacons:
                        intent = new Intent(DeviceScanActivity.getAppContext(), SubscribedBeaconActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getAppContext().startActivity(intent);
                        break;
                }
            }
        });

    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scan_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_rssi:
                sortByRssi(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
        beaconAdapter.clearData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconAdapter = new BeaconAdapter(getAppContext(), beacons);
        recyclerView.setAdapter(beaconAdapter);
        beaconManager.bind(this);
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    beaconAdapter.addResult(beacon);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sorting) {
                            sortBeaconByRssi();
                        }
                        beaconAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("rangingBeacons", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sortByRssi(boolean sort) {
        sorting = sort;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sortBeaconByRssi() {
        Collections.sort(beacons, new Comparator<Beacon>() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(Beacon beacon, Beacon beacon1) {

                return Integer.compare(beacon.getRssi(), beacon1.getRssi());
            }
        });
        Collections.reverse(beacons);
    }

}


