package se.bitsplz.presencedetection.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import se.bitsplz.presencedetection.R;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public class SubscribeToBeaconDialog extends DialogFragment {

    private OnCompleteListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_subscribe_to_beacon, null);

        builder.setTitle("Give your beacon an alias.");
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText aliasNameEditText = (EditText) view.findViewById(R.id.edit_alias_name);
                        final String aliasName = aliasNameEditText.getText().toString();

                        if (aliasName.trim().length() == 0) {
                            Toast.makeText(getActivity(), "You can't leave this field empty.", Toast.LENGTH_LONG).show();
                        } else {
                            listener.onComplete(aliasName);
                        }
                    }
                });

        return builder.create();
    }

    public interface OnCompleteListener {
        void onComplete(String aliasName);
    }
}
