package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

public class OAuthToken {

    @SerializedName("access_token") private String token;
    @SerializedName("token_type") private String token_type;
    @SerializedName("expires_in") private Long expiry;

    public String getToken() {
        return token;
    }

    public String getToken_type() {
        return token_type;
    }

    public Long getExpiry() {
        return expiry;
    }
}
