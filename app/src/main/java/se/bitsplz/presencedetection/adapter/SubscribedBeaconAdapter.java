package se.bitsplz.presencedetection.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.service.Storage;
import se.bitsplz.presencedetection.activity.DeviceScanActivity;
import se.bitsplz.presencedetection.model.SubscribedBeacon;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class SubscribedBeaconAdapter extends RecyclerView.Adapter<SubscribedBeaconAdapter.SubscribedBeaconDeviceHolder> {

    public static final String SUB_BEACONS = "se.bitsplz.presencedetection.SUB_BEACONS";

    private final Context context;
    private Gson gson;
    private final List<SubscribedBeacon> subscribedBeacons;
    private Set<SubscribedBeacon> subscribedBeaconsSet;
    private List<Beacon> beacons;

    public SubscribedBeaconAdapter(Context context, List<Beacon> beacons) {
        this.context = context;
        this.beacons = beacons;
        subscribedBeacons = new ArrayList<>();
        gson = new Gson();
        String jsonBeacons = Storage.readString(DeviceScanActivity.appContext, SUB_BEACONS, "");
        Log.d("BeaconSet: ", jsonBeacons);
        if (jsonBeacons.isEmpty()) {
            subscribedBeaconsSet = new HashSet<>();
        } else {
            final Type subBeacons = new TypeToken<HashSet<SubscribedBeacon>>() {
            }.getType();
            subscribedBeaconsSet = gson.fromJson(jsonBeacons, subBeacons);
            subscribedBeacons.addAll(subscribedBeaconsSet);
        }
    }

    public void addResult(Beacon beacon) {
        if (!beacons.contains(beacon)) {
            beacons.add(beacon);
        }
    }

    @Override
    public SubscribedBeaconDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_subscribed_beacon, parent, false);

        return new SubscribedBeaconDeviceHolder(view, context, beacons);
    }

    @Override
    public void onBindViewHolder(SubscribedBeaconAdapter.SubscribedBeaconDeviceHolder holder, int position) {

        Beacon beacon = beacons.get(position);
        for (SubscribedBeacon subscribedBeacon : subscribedBeacons) {
            if (beacon.getId1().equals(subscribedBeacon.getBeacon().getId1())) {
                holder.aliasNameView.setText(subscribedBeacon.getAliasName());
            }
        }
        holder.proximityUuidView.setText("UUId: " + beacon.getId1().toString());
        holder.majorView.setText("Major: " + beacon.getId2().toString());
        holder.minorView.setText("Minor: " + beacon.getId3().toString());
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public void clearData() {
        beacons.clear();
        notifyDataSetChanged();
    }

    public static final class SubscribedBeaconDeviceHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public final TextView aliasNameView;
        public final TextView proximityUuidView;
        public final TextView minorView;
        public final TextView majorView;

        private List<Beacon> beacons;
        private Context context;

        public SubscribedBeaconDeviceHolder(View view, Context context, List<Beacon> beacons) {
            super(view);
            this.beacons = beacons;
            this.context = context;
            cardView = (CardView) itemView.findViewById(R.id.recycler_view_card_view_subscribed_beacon);

            this.aliasNameView = (TextView) view.findViewById(R.id.alias_name_subscribed);
            this.proximityUuidView = (TextView) view.findViewById(R.id.proximity_uuid_subscribed);
            this.majorView = (TextView) view.findViewById(R.id.major_subscribed);
            this.minorView = (TextView) view.findViewById(R.id.minor_subscribed);
        }

    }
}

