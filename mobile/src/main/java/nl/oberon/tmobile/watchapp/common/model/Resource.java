package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

public class Resource {

    @SerializedName("Label") private String label;
    @SerializedName("Url") private String url;
    @SerializedName("Availability") private String availability;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getAvailability() {
        return availability;
    }
}
