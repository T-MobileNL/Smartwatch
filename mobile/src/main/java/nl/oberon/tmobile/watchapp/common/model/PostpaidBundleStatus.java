package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.oberon.tmobile.datalayer.model.Bundle;

public class PostpaidBundleStatus extends BaseCapiResponse {

    @SerializedName("UnbilledAirTime") private int unbilledAirTime;
    @SerializedName("BalanceDate") private String balanceDate;
    @SerializedName("NextBillDate") private String nextBillDate;
    @SerializedName("Bundles") private List<Bundle> bundles;

    public int getUnbilledAirTime() {
        return unbilledAirTime;
    }

    public String getBalanceDate() {
        return balanceDate;
    }

    public String getNextBillDate() {
        return nextBillDate;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }
}
