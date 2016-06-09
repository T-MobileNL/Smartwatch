package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.oberon.tmobile.datalayer.model.Bundle;

public class PostpaidDataBundleStatus extends BaseCapiResponse {

    @SerializedName("BalanceDate") private String balanceDate;
    @SerializedName("NextReset") private String nextReset;
    @SerializedName("Bundles") private List<Bundle> bundles;

    public String getBalanceDate() {
        return balanceDate;
    }

    public String getNextReset() {
        return nextReset;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }
}
