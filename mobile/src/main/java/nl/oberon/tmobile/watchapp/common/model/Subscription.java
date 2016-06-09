package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

public class Subscription extends BaseCapiResponse{

    @SerializedName("CustomerNumber") private String customerNumber;
    @SerializedName("Msisdn") private String msisdn;

    public String getCustomerNumber() {
        return customerNumber;
    }
}
