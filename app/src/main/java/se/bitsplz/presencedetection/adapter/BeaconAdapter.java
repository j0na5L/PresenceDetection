package se.bitsplz.presencedetection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.List;


import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.activity.SubscribeToBeaconActivity;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconDeviceHolder> {

    private final Context context;
    private final List<Beacon> beacons;

    public BeaconAdapter(Context context, List<Beacon> beacons) {
        this.context = context;
        this.beacons = beacons;
    }

    public void addResult(Beacon beacon) {
        if (!beacons.contains(beacon)) {
            beacons.add(beacon);
        } else {
            for (Beacon beaconInList : beacons) {
                if (beaconInList.getId1().equals(beacon.getId1())) {
                    beaconInList.setRssi(beacon.getRssi());
                }
            }
        }
    }

    @Override
    public BeaconDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_beacon, parent, false);

        return new BeaconDeviceHolder(view, context, beacons);
    }

    @Override
    public void onBindViewHolder(BeaconAdapter.BeaconDeviceHolder holder, int position) {

        holder.deviceNameView.setText(beacons.get(position).getBluetoothName());
        holder.proximityUuidView.setText("UUId: " + beacons.get(position).getId1().toString());
        holder.majorView.setText("Major: " + beacons.get(position).getId2().toString());
        holder.minorView.setText("Minor: " + beacons.get(position).getId3().toString());
        holder.rssiView.setText("Rssi: " + String.valueOf(beacons.get(position).getRssi()));
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public void clearData() {
        beacons.clear();
        notifyDataSetChanged();
    }

    public static final class BeaconDeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView deviceNameView;
        public final TextView rssiView;
        public final TextView proximityUuidView;
        public final TextView minorView;
        public final TextView majorView;

        private List<Beacon> beacons;
        private Beacon beacon;
        private Context context;
        private CardView cardView;

        public BeaconDeviceHolder(View view, Context context, List<Beacon> beacons) {
            super(view);
            cardView = (CardView) itemView.findViewById(R.id.recycler_view_card_view);
            this.beacons = beacons;
            this.context = context;

            view.setOnClickListener(this);
            this.deviceNameView = (TextView) view.findViewById(R.id.device_name);
            this.proximityUuidView = (TextView) view.findViewById(R.id.proximity_uuid);
            this.majorView = (TextView) view.findViewById(R.id.major);
            this.minorView = (TextView) view.findViewById(R.id.minor);
            this.rssiView = (TextView) view.findViewById(R.id.rssi);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            beacon = this.beacons.get(position);
            Intent intent = new Intent(this.context, SubscribeToBeaconActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("beacon", beacon);
            this.context.startActivity(intent);
        }
    }
}
