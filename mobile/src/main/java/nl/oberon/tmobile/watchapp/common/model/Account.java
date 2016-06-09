package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

public class Account extends BaseCapiResponse{
    @SerializedName("Username") private String username;
    @SerializedName("Type") private String type;
    @SerializedName("IsCompanyAdmin") private boolean isCompanyAdmin;

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public boolean isCompanyAdmin() {
        return isCompanyAdmin;
    }
}