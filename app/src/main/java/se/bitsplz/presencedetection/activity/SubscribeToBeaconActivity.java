package se.bitsplz.presencedetection.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.service.RetrofitBuilder;
import se.bitsplz.presencedetection.service.Storage;
import se.bitsplz.presencedetection.dialog.SubscribeToBeaconDialog;
import se.bitsplz.presencedetection.model.SubscribedBeacon;
import se.bitsplz.presencedetection.model.Subscription;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public class SubscribeToBeaconActivity extends AppCompatActivity implements SubscribeToBeaconDialog.OnCompleteListener {

    public static final String SUB_BEACONS = "se.bitsplz.presencedetection.SUB_BEACONS";

    private DialogFragment subscriptionDialog;
    private RetrofitBuilder retrofitBuilder;
    private Gson gson;
    private Intent intent;
    private Beacon beacon;

    private Set<SubscribedBeacon> subscribedBeacons;
    private TextView majorText;
    private TextView minorText;
    private TextView uuidText;
    private TextView rssiText;
    private TextView deviceNameText;
    private String major;
    private String minor;
    private String uuid;
    private String rssi;
    private String deviceName;
    private String beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_to_beacon);
        intent = getIntent();
        gson = new Gson();

        beacons = Storage.readString(DeviceScanActivity.appContext, SUB_BEACONS, "");

        Log.d("BeaconSet: ", beacons);
        if (beacons.isEmpty()) {
            subscribedBeacons = new HashSet<>();
        } else {
            final Type subBeacons = new TypeToken<HashSet<SubscribedBeacon>>() {
            }.getType();
            subscribedBeacons = gson.fromJson(beacons, subBeacons);
        }

        this.beacon = intent.getParcelableExtra("beacon");
        this.deviceName = beacon.getBluetoothName();
        this.uuid = beacon.getId1().toString();
        this.major = beacon.getId2().toString();
        this.minor = beacon.getId3().toString();
        this.rssi = String.valueOf(beacon.getRssi());
        this.uuidText = (TextView) findViewById(R.id.proximity_uuid_subscribe);
        this.majorText = (TextView) findViewById(R.id.major_subscribe);
        this.minorText = (TextView) findViewById(R.id.minor_subscribe);
        this.rssiText = (TextView) findViewById(R.id.rssi_subscribe);
        this.deviceNameText = (TextView) findViewById(R.id.device_name_subscribe);

        this.deviceNameText.setText(deviceName);
        this.uuidText.setText("UUID: " + uuid);
        this.majorText.setText("Major: " + major);
        this.minorText.setText("Minor: " + minor);
        this.rssiText.setText("Rssi: " + rssi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_subscribe_to_beacon, menu);

        return true;
    }


    public void subToBeacon(MenuItem item) {
        retrofitBuilder = new RetrofitBuilder();

        final String userId = Storage.readString(DeviceScanActivity.getAppContext(), DeviceScanActivity.USER_ID, "");
        final Subscription beaconSubscription = new Subscription(userId, beacon.getId1().toString());
        final String subscriptionJson = gson.toJson(beaconSubscription);

        Call<String> callResult = retrofitBuilder.getPresenceDetectionService().subscribeBeacon("input=" + subscriptionJson);
        callResult.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("response", response.body());

                final String responseBody = response.body();

                if (responseBody.contains("\"response_value\":\"200\"")) {

                    showSubscriptionDialog();

                } else {
                    Log.d("Respone: ", responseBody);
                    Log.d("SubscribedBeacons:", "" + subscribedBeacons.size());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showSubscriptionDialog() {
        subscriptionDialog = new SubscribeToBeaconDialog();
        subscriptionDialog.show(getFragmentManager(), "SubscribeToBeaconDialog");
    }


    @Override
    public void onComplete(String aliasName) {
        subscribedBeacons.add(new SubscribedBeacon(aliasName, beacon, new Date().getTime(), 0L, 0L));
        String subsBeaconsJson = gson.toJson(subscribedBeacons);
        Storage.writeToString(DeviceScanActivity.appContext, SUB_BEACONS, subsBeaconsJson);
        Toast.makeText(this, "Success on subscring to this beacon", Toast.LENGTH_LONG).show();
    }

}
