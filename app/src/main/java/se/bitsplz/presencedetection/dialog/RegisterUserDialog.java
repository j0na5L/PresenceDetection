package se.bitsplz.presencedetection.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.bitsplz.presencedetection.R;
import se.bitsplz.presencedetection.service.RetrofitBuilder;
import se.bitsplz.presencedetection.service.Storage;
import se.bitsplz.presencedetection.activity.DeviceScanActivity;
import se.bitsplz.presencedetection.model.UserData;

/**
 * @author jonnakollin
 * @author j0na5L
 */
public class RegisterUserDialog extends DialogFragment {

    public static final String USER_ID = "se.bitsplz.presencedetection.USER_ID";

    private RetrofitBuilder retrofitBuilder;
    private Gson gson;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_register_user, null);

        retrofitBuilder = new RetrofitBuilder();
        gson = new Gson();

        builder.setTitle(R.string.dialog_title);
        builder.setView(view)
                .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText firstNameEditText = (EditText) view.findViewById(R.id.first_name);
                        final EditText lastNameEditText = (EditText) view.findViewById(R.id.last_name);

                        final String firstName = firstNameEditText.getText().toString();
                        final String lastName = lastNameEditText.getText().toString();

                        if (firstName.trim().length() == 0 || lastName.trim().length() == 0) {
                            Toast.makeText(getActivity(), "You must fill in your first name and last name.", Toast.LENGTH_LONG).show();
                        } else {
                            final UserData user = new UserData(firstName, lastName);
                            final String userJson = gson.toJson(user);

                            Log.d("inputString", "input=" + userJson);

                            Call<String> callResult = retrofitBuilder.getPresenceDetectionService().registerUser("input=" + userJson);
                            callResult.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    Log.d("response", response.body());

                                    final String responseBody = response.body();

                                    if (responseBody.contains("\"response_value\":\"200\"")) {
                                        final UserData user = gson.fromJson(responseBody, UserData.class);
                                        final Long userId = user.getUserId();
                                        Log.d("userId", "" + userId);

                                        Storage.writeToString(DeviceScanActivity.getAppContext(), USER_ID, userId.toString());

                                        Toast.makeText(DeviceScanActivity.getAppContext(), "User is registered", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("onFailure", t.getMessage());
                                }
                            });
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });

        return builder.create();

    }
}
