package se.bitsplz.presencedetection.activity;

import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.model.BeaconInRange;
import se.bitsplz.presencedetection.model.BeaconOutOfRange;
import se.bitsplz.presencedetection.service.RetrofitBuilder;
import se.bitsplz.presencedetection.service.Storage;
import se.bitsplz.presencedetection.adapter.SubscribedBeaconAdapter;
import se.bitsplz.presencedetection.model.SubscribedBeacon;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public class SubscribedBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String SUB_BEACONS = "se.bitsplz.presencedetection.SUB_BEACONS";
    public static final String USER_ID = "se.bitsplz.presencedetection.USER_ID";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BottomBar bottomBar;

    private Set<SubscribedBeacon> subscribedBeacons;
    private List<SubscribedBeacon> subscribedBeaconList;

    private SubscribedBeaconAdapter subscribedBeaconAdapter;
    private Gson gson;
    private RetrofitBuilder retrofitBuilder;
    private String userId;

    private List<Beacon> beacons;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_beacon);

        gson = new Gson();
        retrofitBuilder = new RetrofitBuilder();
        subscribedBeaconList = new ArrayList<>();

        String jsonBeacons = Storage.readString(DeviceScanActivity.appContext, SUB_BEACONS, "");
        if (jsonBeacons.isEmpty()) {
            subscribedBeacons = new HashSet<>();
        } else {
            final Type subBeacons = new TypeToken<HashSet<SubscribedBeacon>>() {
            }.getType();
            subscribedBeacons = gson.fromJson(jsonBeacons, subBeacons);
            subscribedBeaconList.addAll(subscribedBeacons);
        }

        userId = Storage.readString(DeviceScanActivity.appContext, USER_ID, "");

        beacons = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_subscribed_beacon);
        layoutManager = new LinearLayoutManager(this);
        subscribedBeaconAdapter = new SubscribedBeaconAdapter(this, beacons);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(subscribedBeaconAdapter);
        bottomBar = BottomBar.attach(this, savedInstanceState);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        createBottomBar();
    }

    private void createBottomBar() {
        bottomBar.setItemsFromMenu(R.menu.bottom_bar_menu_main, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(@IdRes int menuItemId) {
                Intent intent;

                switch (menuItemId) {
                    case R.id.scan_beacon:
                        intent = new Intent(SubscribedBeaconActivity.this, DeviceScanActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeBeaconSet();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBeaconSet();
        beaconManager.unbind(this);
        subscribedBeaconAdapter.clearData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribedBeaconAdapter = new SubscribedBeaconAdapter(this, beacons);
        recyclerView.setAdapter(subscribedBeaconAdapter);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    for (SubscribedBeacon subBeacon : subscribedBeaconList) {
                        if (beacon.getId1().equals(subBeacon.getBeacon().getId1())) {
                            subscribedBeaconAdapter.addResult(beacon);
                            if (beacon.getRssi() > -70) {
                                inRangeNotifier(subBeacon);
                            } else {
                                outOfRangeNotifier(subBeacon);
                            }
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        subscribedBeaconAdapter.notifyDataSetChanged();
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

    public void inRangeNotifier(final SubscribedBeacon subBeacon) {
        final Long timestamp = new Date().getTime();
        if (timestamp > subBeacon.getBeaconInRangeTime() + 60000) {
            final Beacon beacon = subBeacon.getBeacon();
            final BeaconInRange beaconInRange = new BeaconInRange(userId, beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString(),
                    String.valueOf(beacon.getRssi()), timestamp.toString());
            final String beaconInRangeJson = gson.toJson(beaconInRange);

            Log.d("Notlog inputString", "input=" + beaconInRangeJson);

            Call<String> callResult = retrofitBuilder.getPresenceDetectionService().setBeaconInRangeMode("input=" + beaconInRangeJson);
            callResult.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("Notlog Response: ", response.body());

                    final String responseBody = response.body();

                    if (responseBody.contains("\"response_value\":\"200\"")) {
                        subBeacon.setbeaconInRangeTime(timestamp);
                        Toast.makeText(SubscribedBeaconActivity.this, "In range to " + subBeacon.getAliasName() + ".", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Notlog OnFailure", t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    public void outOfRangeNotifier(final SubscribedBeacon subBeacon) {
        final Long timestamp = new Date().getTime();
        if (timestamp > subBeacon.getBeaconInRangeTime() + 120000 && subBeacon.getBeaconOutOfRangeTime() < subBeacon.getBeaconInRangeTime()) {
            final Beacon beacon = subBeacon.getBeacon();
            final BeaconOutOfRange beaconOutOfRange = new BeaconOutOfRange(userId, beacon.getId1().toString(), timestamp.toString());
            final String beaconOutOfRangeJson = gson.toJson(beaconOutOfRange);

            Log.d("Notlog inputString", "input=" + beaconOutOfRangeJson);

            Call<String> callResult = retrofitBuilder.getPresenceDetectionService().setBeaconOutOfRangeMode("input=" + beaconOutOfRangeJson);
            callResult.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("Notlog Response: ", response.body());

                    final String responseBody = response.body();

                    if (responseBody.contains("\"response_value\":\"200\"")) {
                        subBeacon.setBeaconOutOfRangeTime(timestamp);
                        Toast.makeText(SubscribedBeaconActivity.this, "Out of range to " + subBeacon.getAliasName(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Notlog OnFailure", t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    private void storeBeaconSet() {
        subscribedBeacons.clear();
        for (SubscribedBeacon subscribedBeacon : subscribedBeaconList) {
            subscribedBeacons.add(subscribedBeacon);
        }
        String subsBeaconsJson = gson.toJson(subscribedBeacons);
        Storage.writeToString(DeviceScanActivity.appContext, SubscribeToBeaconActivity.SUB_BEACONS, subsBeaconsJson);
    }

}