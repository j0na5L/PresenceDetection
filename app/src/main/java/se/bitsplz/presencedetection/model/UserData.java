package se.bitsplz.presencedetection.model;

import com.google.gson.annotations.SerializedName;


/**
 * @author jonnakollin
 * @author j0na5L
 */
public final class UserData {

    @SerializedName("api_key")
    private String apiKey = "28742sk238sdkAdhfue243jdfhvnsa1923347";
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("id_user")
    private Long userId;

    public UserData(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }
}
